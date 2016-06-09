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
import io.mypojo.jcl.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * System class loader
 */
public class SystemLoader extends ProxyClassLoader {

    private final Logger logger = LoggerFactory.getLogger(SystemLoader.class);
    private JarClassLoader abstractClassLoader;

    public SystemLoader(JarClassLoader abstractClassLoader) {
        this.abstractClassLoader = abstractClassLoader;
        order = 50;
        enabled = Configuration.isSystemLoaderEnabled();
    }

    @Override
    public Class loadClass(String className, boolean resolveIt) {
        Class result;

        try {
            result = abstractClassLoader.internalFindSystemClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        if (logger.isDebugEnabled())
            logger.debug("Returning system class " + className);

        return result;
    }

    @Override
    public InputStream loadResource(String name) {
        InputStream is = ClassLoader.getSystemResourceAsStream(name);

        if (is != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning system resource " + name);

            return is;
        }

        return null;
    }

    @Override
    public URL findResource(String name) {
        URL url = ClassLoader.getSystemResource(name);

        if (url != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning system resource " + name);

            return url;
        }

        return null;
    }
}
