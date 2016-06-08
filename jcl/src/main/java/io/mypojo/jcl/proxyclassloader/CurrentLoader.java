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

import io.mypojo.jcl.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * Current class loader
 */
public class CurrentLoader extends ProxyClassLoader {
    private final Logger logger = LoggerFactory.getLogger(CurrentLoader.class.getName());

    public CurrentLoader() {
        order = 20;
        enabled = Configuration.isCurrentLoaderEnabled();
    }

    @Override
    public Class loadClass(String className, boolean resolveIt) {
        Class result;

        try {
            result = getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        if (logger.isDebugEnabled())
            logger.debug("Returning class " + className + " loaded with current classloader");

        return result;
    }

    @Override
    public InputStream loadResource(String name) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);

        if (is != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning resource " + name + " loaded with current classloader");

            return is;
        }

        return null;
    }


    @Override
    public URL findResource(String name) {
        URL url = getClass().getClassLoader().getResource(name);

        if (url != null) {
            if (logger.isDebugEnabled())
                logger.debug("Returning resource " + name + " loaded with current classloader");

            return url;
        }

        return null;
    }
}
