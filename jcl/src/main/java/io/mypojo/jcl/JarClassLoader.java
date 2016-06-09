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
import io.mypojo.jcl.proxyclassloader.*;
import io.mypojo.jcl.resources.ClasspathResources;
import io.mypojo.jcl.resources.IClasspathResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableMap;

/**
 * Abstract class loader that can load classes from different resources
 *
 * @author Kamran Zafar
 */
@SuppressWarnings("unchecked")
public class JarClassLoader extends ClassLoader {

    // we could use concurrent sorted set like ConcurrentSkipListSet here instead, which would be automatically sorted
    // and wouldn't require the lock.
    // But that was added in 1.6, and according to Maven we're targeting 1.5+.
    protected static Logger logger = LoggerFactory.getLogger(JarClassLoader.class);
    /**
     * Note that all iterations over this list *must* synchronize on it first!
     */
    protected final List<ProxyClassLoader> loaders = Collections.synchronizedList(new ArrayList<ProxyClassLoader>());
    /**
     * Class cache
     */
    protected final Map<String, Class> classes = synchronizedMap(new HashMap<String, Class>());
    protected final IClasspathResources classpathResources = new ClasspathResources();
    protected final ProxyClassLoader localLoader = new LocalLoader(this);
    private final ProxyClassLoader systemLoader = new SystemLoader(this);
    private final ProxyClassLoader parentLoader = new ParentLoader(this);
    private final ProxyClassLoader currentLoader = new CurrentLoader();
    private final ProxyClassLoader threadLoader = new ThreadContextLoader();
    private final ProxyClassLoader osgiBootLoader = new OsgiBootLoader(this);
    protected char classNameReplacementChar;

    /**
     * Build a new instance of AbstractClassLoader.java.
     *
     * @param parent parent class loader
     */
    public JarClassLoader(ClassLoader parent) {
        super(parent);
        addDefaultLoader();
    }

    /**
     * No arguments constructor
     */
    public JarClassLoader() {
        super();
        addDefaultLoader();
        addLoader(localLoader);
    }


    /**
     * Loads classes from different sources
     */
    public JarClassLoader(Object[] sources) {
        this();
        addAll(sources);
    }

    /**
     * Loads classes from different sources
     */
    public JarClassLoader(List sources) {
        this();
        addAll(sources);
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

    /**
     * Add all jar/class sources
     */
    public void addAll(Object[] sources) {
        for (Object source : sources) {
            add(source);
        }
    }

    /**
     * Add all jar/class sources
     */
    public void addAll(List sources) {
        for (Object source : sources) {
            add(source);
        }
    }

    public Map<String, Class> getClasses() {
        return classes;
    }

    public IClasspathResources getClasspathResources() {
        return classpathResources;
    }

    /**
     * Loads local/remote source
     */
    public void add(Object source) {
        if (source instanceof InputStream)
            add((InputStream) source);
        else if (source instanceof URL)
            add((URL) source);
        else if (source instanceof String)
            add((String) source);
        else
            throw new JclException("Unknown Resource type");

    }

    /**
     * Loads local/remote resource
     */
    public void add(String resourceName) {
        classpathResources.loadResource(resourceName);
    }

    /**
     * Loads classes from InputStream
     */
    public void add(InputStream jarStream) {
        classpathResources.loadJar(null, jarStream);
    }

    /**
     * Loads local/remote resource
     */
    public void add(URL url) {
        classpathResources.loadResource(url);
    }

    public Class<?> internalDefineClass(String name, byte[] b, int off, int len)
            throws ClassFormatError {
        return defineClass(name, b, off, len);
    }

    public Package internalDefinePackage(String name, String specTitle,
                                         String specVersion, String specVendor,
                                         String implTitle, String implVersion,
                                         String implVendor, URL sealBase)
            throws IllegalArgumentException {
        return definePackage(name, specTitle,
                specVersion, specVendor,
                implTitle, implVersion,
                implVendor, sealBase);
    }

    public void internalResolveClass(Class<?> c) {
        resolveClass(c);
    }

    /**
     * Reads the class bytes from different local and remote resources using
     * ClasspathResources
     */
    public byte[] loadClassBytes(String className) {
        className = formatClassName(className);

        return classpathResources.getResource(className);
    }

    /**
     * Attempts to unload class, it only unloads the locally loaded classes by
     * JCL
     */
    public void unloadClass(String className) {
        if (logger.isDebugEnabled())
            logger.debug("Unloading class " + className);

        if (classes.containsKey(className)) {
            if (logger.isDebugEnabled())
                logger.debug("Removing loaded class " + className);
            classes.remove(className);
            try {
                classpathResources.unload(formatClassName(className));
            } catch (ResourceNotFoundException e) {
                throw new JclException("Something is very wrong!!!"
                        + "The locally loaded classes must be in synch with ClasspathResources", e);
            }
        } else {
            try {
                classpathResources.unload(formatClassName(className));
            } catch (ResourceNotFoundException e) {
                throw new JclException("Class could not be unloaded "
                        + "[Possible reason: Class belongs to the system]", e);
            }
        }
    }

    /**
     */
    protected String formatClassName(String className) {
        className = className.replace('/', '~');

        if (classNameReplacementChar == '\u0000') {
            // '/' is used to map the package to the path
            className = className.replace('.', '/') + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            className = className.replace('.', classNameReplacementChar) + ".class";
        }

        className = className.replace('~', '/');
        return className;
    }

    public char getClassNameReplacementChar() {
        return classNameReplacementChar;
    }

    public void setClassNameReplacementChar(char classNameReplacementChar) {
        this.classNameReplacementChar = classNameReplacementChar;
    }

    /**
     * Returns all loaded classes and resources
     *
     * @return Map
     */
    public Map<String, byte[]> getLoadedResources() {
        return classpathResources.getResources();
    }

    /**
     * @return Local JCL ProxyClassLoader
     */
    public ProxyClassLoader getLocalLoader() {
        return localLoader;
    }

    /**
     * Returns all JCL-loaded classes as an immutable Map
     *
     * @return Map
     */
    public Map<String, Class> getLoadedClasses() {
        return unmodifiableMap(classes);
    }


}
