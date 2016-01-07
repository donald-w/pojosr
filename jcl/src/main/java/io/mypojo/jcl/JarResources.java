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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * JarResources reads jar files and loads the class content/bytes in a HashMap
 *
 * @author Kamran Zafar
 */
public class JarResources {

    private static Logger logger = LoggerFactory.getLogger(JarResources.class);
    protected Map<String, JclJarEntry> jarEntryContents;
    protected boolean collisionAllowed;

    /**
     * Default constructor
     */
    public JarResources() {
        jarEntryContents = new HashMap<>();
        collisionAllowed = Configuration.suppressCollisionException();
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
}
