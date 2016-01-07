/*
 * Copyright 2016 Donald W - github@donaldw.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mypojo.jcl;

import io.mypojo.jcl.exception.JclException;
import io.mypojo.jcl.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Class that builds a local classpath by loading resources from different
 * files/paths
 *
 * @author Kamran Zafar
 */
public class ClasspathResources extends JarResources {

    private static Logger logger = LoggerFactory.getLogger(ClasspathResources.class);
    private boolean ignoreMissingResources;

    public ClasspathResources() {
        super();
        ignoreMissingResources = Configuration.suppressMissingResourceException();
    }

    /**
     * Reads the resource content
     */
    private void loadResourceContent(String resource, String pack) {
        File resourceFile = new File(resource);
        String entryName = "";

        try (FileInputStream fis = new FileInputStream(resourceFile)) {

            byte[] content = new byte[(int) resourceFile.length()];

            if (fis.read(content) != -1) {

                if (pack.length() > 0) {
                    entryName = pack + "/";
                }

                entryName += resourceFile.getName();

                if (jarEntryContents.containsKey(entryName)) {
                    if (!collisionAllowed)
                        throw new JclException("Resource " + entryName + " already loaded");
                    else {
                        if (logger.isDebugEnabled())
                            logger.debug("Resource " + entryName + " already loaded; ignoring entry...");
                        return;
                    }
                }

                if (logger.isDebugEnabled())
                    logger.debug("Loading resource: " + entryName);

                JclJarEntry entry = new JclJarEntry();
                File parentFile = resourceFile.getAbsoluteFile().getParentFile();
                if (parentFile == null) {
                    // I don't believe this is actually possible with an absolute path. With no parent, we must be at the root of the filesystem.
                    entry.setBaseUrl("file:/");
                } else {
                    entry.setBaseUrl(parentFile.toURI().toString());
                }
                entry.setResourceBytes(content);

                jarEntryContents.put(entryName, entry);
            }
        } catch (IOException e) {
            throw new JclException(e);
        }
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     */
    private void loadRemoteResource(URL url) {
        if (logger.isDebugEnabled())
            logger.debug("Attempting to load a remote resource.");

        if (url.toString().toLowerCase().endsWith(".jar")) {
            loadJar(url);
            return;
        }

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream stream = url.openStream()
        ) {
            int byt;
            while (((byt = stream.read()) != -1)) {
                out.write(byt);
            }

            byte[] content = out.toByteArray();

            if (jarEntryContents.containsKey(url.toString())) {
                if (!collisionAllowed)
                    throw new JclException("Resource " + url.toString() + " already loaded");
                else {
                    if (logger.isDebugEnabled())
                        logger.debug("Resource " + url.toString() + " already loaded; ignoring entry...");
                    return;
                }
            }

            if (logger.isDebugEnabled())
                logger.debug("Loading remote resource.");

            JclJarEntry entry = new JclJarEntry();
            entry.setResourceBytes(content);
            jarEntryContents.put(url.toString(), entry);
        } catch (IOException e) {
            throw new JclException(e);
        }
    }

    /**
     * Reads the class content
     */
    private void loadClassContent(String clazz, String pack) {
        File cf = new File(clazz);

        try (FileInputStream fis = new FileInputStream(cf)) {

            byte[] content = new byte[(int) cf.length()];

            if (fis.read(content) != -1) {
                String entryName = pack + "/" + cf.getName();

                if (jarEntryContents.containsKey(entryName)) {
                    if (!collisionAllowed)
                        throw new JclException("Class " + entryName + " already loaded");
                    else {
                        if (logger.isDebugEnabled())
                            logger.debug("Class " + entryName + " already loaded; ignoring entry...");
                        return;
                    }
                }

                if (logger.isDebugEnabled())
                    logger.debug("Loading class: " + entryName);

                JclJarEntry entry = new JclJarEntry();
                entry.setResourceBytes(content);
                jarEntryContents.put(entryName, entry);
            }
        } catch (IOException e) {
            throw new JclException(e);
        }
    }

    /**
     * Reads local and remote resources
     */
    public void loadResource(URL url) {
        try {
            // Is Local
            loadResource(new File(url.toURI()), "");
        } catch (IllegalArgumentException iae) {
            // Is Remote
            loadRemoteResource(url);
        } catch (URISyntaxException e) {
            throw new JclException("URISyntaxException", e);
        }
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     */
    public void loadResource(String path) {
        if (logger.isDebugEnabled())
            logger.debug("Resource: " + path);

        File fp = new File(path);

        if (!fp.exists() && !ignoreMissingResources) {
            throw new JclException("File/Path does not exist");
        }

        loadResource(fp, "");
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     */
    private void loadResource(File fol, String packName) {
        if (fol.isFile()) {
            if (fol.getName().toLowerCase().endsWith(".class")) {
                loadClassContent(fol.getAbsolutePath(), packName);
            } else {
                if (fol.getName().toLowerCase().endsWith(".jar")) {
                    loadJar(fol.getAbsolutePath());
                } else {
                    loadResourceContent(fol.getAbsolutePath(), packName);
                }
            }

            return;
        }

        if (fol.list() != null) {
            for (String f : fol.list()) {
                File fl = new File(fol.getAbsolutePath() + "/" + f);

                String pn = packName;

                if (fl.isDirectory()) {

                    if (!pn.equals(""))
                        pn = pn + "/";

                    pn = pn + fl.getName();
                }

                loadResource(fl, pn);
            }
        }
    }

    /**
     * Removes the loaded resource
     */
    public void unload(String resource) {
        if (jarEntryContents.containsKey(resource)) {
            if (logger.isDebugEnabled())
                logger.debug("Removing resource " + resource);
            jarEntryContents.remove(resource);
        } else {
            throw new ResourceNotFoundException(resource, "Resource not found in local ClasspathResources");
        }
    }

    public boolean isCollisionAllowed() {
        return collisionAllowed;
    }

    public void setCollisionAllowed(boolean collisionAllowed) {
        this.collisionAllowed = collisionAllowed;
    }

    public boolean isIgnoreMissingResources() {
        return ignoreMissingResources;
    }

    public void setIgnoreMissingResources(boolean ignoreMissingResources) {
        this.ignoreMissingResources = ignoreMissingResources;
    }
}
