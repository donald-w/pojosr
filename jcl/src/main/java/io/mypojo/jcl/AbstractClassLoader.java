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

import io.mypojo.jcl.proxyclassloader.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Abstract class loader that can load classes from different resources
 *
 * @author Kamran Zafar
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

    // we could use concurrent sorted set like ConcurrentSkipListSet here instead, which would be automatically sorted
    // and wouldn't require the lock.
    // But that was added in 1.6, and according to Maven we're targeting 1.5+.
    /**
     * Note that all iterations over this list *must* synchronize on it first!
     */
    protected final List<ProxyClassLoader> loaders = Collections.synchronizedList(new ArrayList<ProxyClassLoader>());

    private final ProxyClassLoader systemLoader = new SystemLoader(this);
    private final ProxyClassLoader parentLoader = new ParentLoader(this);
    private final ProxyClassLoader currentLoader = new CurrentLoader();
    private final ProxyClassLoader threadLoader = new ThreadContextLoader();
    private final ProxyClassLoader osgiBootLoader = new OsgiBootLoader(this);

    /**
     * Build a new instance of AbstractClassLoader.java.
     *
     * @param parent parent class loader
     */
    public AbstractClassLoader(ClassLoader parent) {
        super(parent);
        addDefaultLoader();
    }

    /**
     * No arguments constructor
     */
    public AbstractClassLoader() {
        super();
        addDefaultLoader();
    }

    protected void addDefaultLoader() {
        synchronized (loaders) {
            loaders.add(systemLoader);
            loaders.add(parentLoader);
            loaders.add(currentLoader);
            loaders.add(threadLoader);
            Collections.sort(loaders);
        }
    }

    public void addLoader(ProxyClassLoader loader) {
        synchronized (loaders) {
            loaders.add(loader);
            Collections.sort(loaders);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }

    /**
     * Overrides the loadClass method to load classes from other resources,
     * JarClassLoader is the only subclass in this project that loads classes
     * from jar files
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    public Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
        if (className == null || className.trim().equals(""))
            return null;

        Class clazz = null;

        // Check osgi boot delegation
        if (osgiBootLoader.isEnabled()) {
            clazz = osgiBootLoader.loadClass(className, resolveIt);
        }

        if (clazz == null) {
            synchronized (loaders) {
                for (ProxyClassLoader l : loaders) {
                    if (l.isEnabled()) {
                        clazz = l.loadClass(className, resolveIt);
                        if (clazz != null)
                            break;
                    }
                }
            }
        }

        if (clazz == null)
            throw new ClassNotFoundException(className);

        return clazz;
    }

    /**
     * Overrides the getResource method to load non-class resources from other
     * sources, JarClassLoader is the only subclass in this project that loads
     * non-class resources from jar files
     *
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String name) {
        if (name == null || name.trim().equals(""))
            return null;

        URL url = null;

        // Check osgi boot delegation
        if (osgiBootLoader.isEnabled()) {
            url = osgiBootLoader.findResource(name);
        }

        if (url == null) {
            synchronized (loaders) {
                for (ProxyClassLoader l : loaders) {
                    if (l.isEnabled()) {
                        url = l.findResource(name);
                        if (url != null)
                            break;
                    }
                }
            }
        }

        return url;

    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (name == null || name.trim().equals("")) {
            return Collections.emptyEnumeration();
        }

        Vector<URL> urlVector = new Vector<>();
        URL url = null;

        // Check osgi boot delegation
        if (osgiBootLoader.isEnabled()) {
            url = osgiBootLoader.findResource(name);

            if (url != null) {
                urlVector.add(url);
            }
        }

        if (url == null) {
            synchronized (loaders) {
                for (ProxyClassLoader l : loaders) {
                    if (l.isEnabled()) {
                        url = l.findResource(name);
                        if (url != null) {
                            urlVector.add(url);
                        }
                    }
                }
            }
        }

        return urlVector.elements();
    }

    public final Class<?> internalFindSystemClass(String name)
            throws ClassNotFoundException {
        return findSystemClass(name);
    }

    /**
     * Overrides the getResourceAsStream method to load non-class resources from
     * other sources, JarClassLoader is the only subclass in this project that
     * loads non-class resources from jar files
     *
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        if (name == null || name.trim().equals(""))
            return null;

        InputStream is = null;

        // Check osgi boot delegation
        if (osgiBootLoader.isEnabled()) {
            is = osgiBootLoader.loadResource(name);
        }

        if (is == null) {
            synchronized (loaders) {
                for (ProxyClassLoader l : loaders) {
                    if (l.isEnabled()) {
                        is = l.loadResource(name);
                        if (is != null)
                            break;
                    }
                }
            }
        }

        return is;

    }

    public ProxyClassLoader getSystemLoader() {
        return systemLoader;
    }

    public ProxyClassLoader getParentLoader() {
        return parentLoader;
    }

    public ProxyClassLoader getCurrentLoader() {
        return currentLoader;
    }

    public ProxyClassLoader getThreadLoader() {
        return threadLoader;
    }

    public ProxyClassLoader getOsgiBootLoader() {
        return osgiBootLoader;
    }

}
