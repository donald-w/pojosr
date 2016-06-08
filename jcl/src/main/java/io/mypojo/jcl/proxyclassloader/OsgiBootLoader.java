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

import io.mypojo.jcl.AbstractClassLoader;
import io.mypojo.jcl.config.Configuration;
import io.mypojo.jcl.exception.JclException;
import io.mypojo.jcl.exception.ResourceNotFoundException;
import io.mypojo.jcl.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Osgi boot loader
 */
public final class OsgiBootLoader extends ProxyClassLoader {
    private static final String JAVA_PACKAGE = "java.";
    private final Logger logger = LoggerFactory.getLogger(OsgiBootLoader.class.getName());
    private AbstractClassLoader abstractClassLoader;
    private boolean strictLoading;
    private String[] bootDelagation;

    public OsgiBootLoader(AbstractClassLoader abstractClassLoader) {
        this.abstractClassLoader = abstractClassLoader;
        enabled = Configuration.isOsgiBootDelegationEnabled();
        strictLoading = Configuration.isOsgiBootDelegationStrict();
        bootDelagation = Configuration.getOsgiBootDelegation();
        order = 0;
    }

    @Override
    public Class loadClass(String className, boolean resolveIt) {
        Class clazz = null;

        if (enabled && isPartOfOsgiBootDelegation(className)) {
            clazz = abstractClassLoader.getParentLoader().loadClass(className, resolveIt);

            if (clazz == null && strictLoading) {
                throw new JclException(new ClassNotFoundException("JCL OSGi Boot Delegation: Class " + className + " not found."));
            }

            if (logger.isDebugEnabled())
                logger.debug("Class " + className + " loaded via OSGi boot delegation.");
        }

        return clazz;
    }

    @Override
    public InputStream loadResource(String name) {
        InputStream is = null;

        if (enabled && isPartOfOsgiBootDelegation(name)) {
            is = abstractClassLoader.getParentLoader().loadResource(name);

            if (is == null && strictLoading) {
                throw new ResourceNotFoundException("JCL OSGi Boot Delegation: Resource " + name + " not found.");
            }

            if (logger.isDebugEnabled())
                logger.debug("Resource " + name + " loaded via OSGi boot delegation.");
        }

        return is;
    }

    @Override
    public URL findResource(String name) {
        URL url = null;

        if (enabled && isPartOfOsgiBootDelegation(name)) {
            url = abstractClassLoader.getParentLoader().findResource(name);

            if (url == null && strictLoading) {
                throw new ResourceNotFoundException("JCL OSGi Boot Delegation: Resource " + name + " not found.");
            }

            if (logger.isDebugEnabled())
                logger.debug("Resource " + name + " loaded via OSGi boot delegation.");
        }

        return url;
    }

    /**
     * Check if the class/resource is part of OSGi boot delegation
     */
    private boolean isPartOfOsgiBootDelegation(String resourceName) {
        if (resourceName.startsWith(JAVA_PACKAGE))
            return true;

        String[] bootPkgs = bootDelagation;

        if (bootPkgs != null) {
            for (String bc : bootPkgs) {
                Pattern pat = Pattern.compile(Utils.wildcardToRegex(bc), Pattern.CASE_INSENSITIVE);

                Matcher matcher = pat.matcher(resourceName);
                if (matcher.find()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isStrictLoading() {
        return strictLoading;
    }

    public void setStrictLoading(boolean strictLoading) {
        this.strictLoading = strictLoading;
    }

    public String[] getBootDelagation() {
        return bootDelagation;
    }

    public void setBootDelagation(String[] bootDelagation) {
        this.bootDelagation = bootDelagation;
    }
}
