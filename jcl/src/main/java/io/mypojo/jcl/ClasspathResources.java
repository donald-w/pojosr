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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;


/**
 * Class that builds a local classpath by loading resources from different
 * files/paths
 *
 * @author Kamran Zafar
 */
public class ClasspathResources {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathResources.class);
    private final Map<String, JclJarEntry> jarEntryContents = new HashMap<>();
    protected boolean collisionAllowed;
    private boolean ignoreMissingResources;

    public ClasspathResources() {
        collisionAllowed = Configuration.suppressCollisionException();
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

    public URL getResourceURL(String name) {

        JclJarEntry entry = jarEntryContents.get(name);
        if (entry != null) {
            if (entry.getBaseUrl() == null) {
                throw new JclException("non-URL accessible resource");
            }
            try {
                return new URL(entry.getBaseUrl() + name);
            } catch (MalformedURLException e) {
                throw new JclException(e);
            }
        }

        return null;
    }

    public byte[] getResource(String name) {
        JclJarEntry entry = jarEntryContents.get(name);
        if (entry != null) {
            return entry.getResourceBytes();
        } else {
            return null;
        }
    }

    /**
     * Returns an immutable Map of all jar resources
     *
     * @return Map
     */
    public Map<String, byte[]> getResources() {

        Map<String, byte[]> resourcesAsBytes = new HashMap<>(jarEntryContents.size());

        for (Map.Entry<String, JclJarEntry> entry : jarEntryContents.entrySet()) {
            resourcesAsBytes.put(entry.getKey(), entry.getValue().getResourceBytes());
        }

        return resourcesAsBytes;
    }

    /**
     * Reads the specified jar file
     */
    public void loadJar(String jarFile) {
        if (logger.isDebugEnabled())
            logger.debug("Loading jar: " + jarFile);

        File file = new File(jarFile);
        String baseUrl = "jar:" + file.toURI().toString() + "!/";

        try (FileInputStream fis = new FileInputStream(file)) {
            loadJar(baseUrl, fis);
        } catch (IOException e) {
            throw new JclException(e);
        }
    }

    /**
     * Reads the jar file from a specified URL
     */
    public void loadJar(URL url) {
        if (logger.isDebugEnabled())
            logger.debug("Loading jar: " + url.toString());

        try (InputStream in = url.openStream()) {
            String baseUrl = "jar:" + url.toString() + "!/";
            loadJar(baseUrl, in);
        } catch (IOException e) {
            throw new JclException(e);
        }
    }

    /**
     * Load the jar contents from InputStream
     */
    public void loadJar(String argBaseUrl, InputStream jarStream) {

        try (
                BufferedInputStream bis = new BufferedInputStream(jarStream);
                JarInputStream jis = new JarInputStream(bis)
        )
            {

            JarEntry jarEntry;
            while ((jarEntry = jis.getNextJarEntry()) != null) {
                if (logger.isDebugEnabled())
                    logger.debug(dump(jarEntry));

                if (jarEntry.isDirectory()) {
                    continue;
                }

                if (jarEntryContents.containsKey(jarEntry.getName())) {
                    if (!collisionAllowed)
                        throw new JclException("Class/Resource " + jarEntry.getName() + " already loaded");
                    else {
                        if (logger.isDebugEnabled())
                            logger.debug("Class/Resource " + jarEntry.getName()
                                    + " already loaded; ignoring entry...");
                        continue;
                    }
                }

                if (logger.isDebugEnabled())
                    logger.debug("Entry Name: " + jarEntry.getName() + ", " + "Entry Size: " + jarEntry.getSize());

                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len;
                while ((len = jis.read(b)) > 0) {
                    out.write(b, 0, len);
                }

                // add to internal resource HashMap
                JclJarEntry entry = new JclJarEntry();
                entry.setBaseUrl(argBaseUrl);
                entry.setResourceBytes(out.toByteArray());
                jarEntryContents.put(jarEntry.getName(), entry);

                if (logger.isDebugEnabled())
                    logger.debug(jarEntry.getName() + ": size=" + out.size() + " ,csize="
                            + jarEntry.getCompressedSize());

                out.close();
            }

            // JarInputStream 'consumes' the manifest. We want it available, since it fundamentally underpins how the ClassPathScanner works
            Manifest manifest = jis.getManifest();
            if (manifest != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    manifest.write(out);

                    JclJarEntry entry = new JclJarEntry();
                    entry.setBaseUrl(argBaseUrl);
                    entry.setResourceBytes(out.toByteArray());
                    jarEntryContents.put(JarFile.MANIFEST_NAME, entry);
                }
            }

        } catch (IOException e) {
            throw new JclException(e);
        } catch (NullPointerException e) {
            if (logger.isDebugEnabled())
                logger.debug("Done loading.");
        }
    }

    /**
     * For debugging
     */
    private String dump(JarEntry je) {
        StringBuilder sb = new StringBuilder();
        if (je.isDirectory()) {
            sb.append("d ");
        } else {
            sb.append("f ");
        }

        if (je.getMethod() == JarEntry.STORED) {
            sb.append("stored   ");
        } else {
            sb.append("defalted ");
        }

        sb.append(je.getName());
        sb.append("\t");
        sb.append("").append(je.getSize());
        if (je.getMethod() == JarEntry.DEFLATED) {
            sb.append("/").append(je.getCompressedSize());
        }

        return (sb.toString());
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
