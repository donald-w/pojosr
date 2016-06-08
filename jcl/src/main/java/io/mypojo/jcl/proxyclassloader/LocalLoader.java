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

package io.mypojo.jcl.proxyclassloader;

import io.mypojo.jcl.JarClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import static io.mypojo.jcl.config.Configuration.isLocalLoaderEnabled;

/**
 * Local class loader
 */
public class LocalLoader extends ProxyClassLoader {

    private final Logger logger = LoggerFactory.getLogger(LocalLoader.class);
    private JarClassLoader jarClassLoader;

    public LocalLoader(JarClassLoader jarClassLoader) {
        this.jarClassLoader = jarClassLoader;
        order = 10;
        enabled = isLocalLoaderEnabled();
    }

    @Override
    public Class loadClass(String className, boolean resolveIt) {
        Class result = null;
        byte[] classBytes;

        result = jarClassLoader.getClasses().get(className);
        if (result != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning local loaded class [" + className + "] from cache");
            return result;
        }

        classBytes = jarClassLoader.loadClassBytes(className);
        if (classBytes == null) {
            return null;
        }

        result = jarClassLoader.internalDefineClass(className, classBytes, 0, classBytes.length);

        if (result == null) {
            return null;
        }

        /*
         * Preserve package name.
         */
        if (result.getPackage() == null) {
            int lastDotIndex = className.lastIndexOf('.');
            String packageName = (lastDotIndex >= 0) ? className.substring(0, lastDotIndex) : "";
            jarClassLoader.internalDefinePackage(packageName, null, null, null, null, null, null, null);
        }

        if (resolveIt)
            jarClassLoader.internalResolveClass(result);

        jarClassLoader.getClasses().put(className, result);
        if (logger.isDebugEnabled())
            logger.debug("Return new local loaded class " + className);
        return result;
    }

    @Override
    public InputStream loadResource(String name) {
        byte[] arr = jarClassLoader.getClasspathResources().getResource(name);
        if (arr != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning newly loaded resource " + name);

            return new ByteArrayInputStream(arr);
        }

        return null;
    }

    @Override
    public URL findResource(String name) {
        URL url = jarClassLoader.getClasspathResources().getResourceURL(name);
        if (url != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning newly loaded resource " + name);

            return url;
        }

        return null;
    }
}
