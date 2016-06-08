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

import io.mypojo.jcl.context.DefaultContextLoader;
import io.mypojo.jcl.context.JclContextLoader;
import io.mypojo.jcl.context.XmlContextLoader;
import io.mypojo.jcl.exception.JclContextException;
import io.mypojo.jcl.proxy.CglibProxyProvider;
import io.mypojo.jcl.proxyclassloader.OsgiBootLoader;
import io.mypojo.jcl.test.TestInterface;
import io.mypojo.jcl.utils.JclObjectFactory;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import static io.mypojo.jcl.context.JclContext.destroy;
import static io.mypojo.jcl.context.JclContext.get;
import static io.mypojo.jcl.proxy.ProxyProviderFactory.setDefaultProxyProvider;
import static io.mypojo.jcl.utils.JclObjectFactory.getInstance;
import static io.mypojo.jcl.utils.JclUtils.*;

@SuppressWarnings("all")
@RunWith(JUnit4.class)
public class LoadTest extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(LoadTest.class);

    @Test
    public void testWithResourceName() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        // New class
        Object testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);

        // Locally loaded
        testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
    }

    @Test
    public void testPackagedResource() {
        ClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        InputStream is = jc.getResourceAsStream("test/test.properties");

        logger.info(is.toString());

        assertNotNull(is);
    }

    @Test
    public void testPackagedResourceURL() {
        ClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        URL url = jc.getResource("test/test.properties");

        assertNotNull(url);
    }

    @Test
    public void testMissingResourceURL() {
        ClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        URL url = jc.getResource("asdf/adsf");

        assertNull(url);
    }

    @Test
    public void testWithClassFolder() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        Object testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
    }

    @Test
    public void testWithUrl() throws MalformedURLException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        // URL url = new URL("http://localhost:8080/blank/test-jcl.jar");
        File f = new File("./target/test-jcl.jar");

        ClassLoader jc = new JarClassLoader(new URL[]{f.toURI().toURL()});
        Object testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
    }

    @Test
    public void testWithInputStream() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            IOException {
        FileInputStream fis = new FileInputStream("./target/test-jcl.jar");
        ClassLoader jc = new JarClassLoader(new FileInputStream[]{fis});
        Object testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
        fis.close();
    }

    @Test
    public void testAddingClassSources() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        JarClassLoader jc = new JarClassLoader();
        jc.add("./target/test-jcl.jar");

        ClassLoader cl = jc;

        Object testObj = cl.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
    }

    @Test
    public void testChangeClassLoadingOrder() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        JarClassLoader jc = new JarClassLoader();
        jc.getSystemLoader().setOrder(1);
        jc.getParentLoader().setOrder(3);
        jc.getLocalLoader().setOrder(2);

        jc.add("./target/test-classes");

        ClassLoader cl = jc;

        // Should be loaded from system
        Object testObj = cl.loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
    }

    @Test
    public void testInterfaceCast() {
        JarClassLoader jc = new JarClassLoader();
        jc.add("./target/test-jcl.jar");

        JclObjectFactory factory = getInstance();
        Object testObj = factory.create(jc, "io.mypojo.jcl.test.Test");

        TestInterface ti = cast(testObj, TestInterface.class);

        assertNotNull(ti);

        // ti = JclUtils.cast( testObj );
        //
        // assertNotNull( ti );
        //
        // ti = (TestInterface) JclUtils.toCastable( testObj );
        //
        // assertNotNull( ti );

        ti = (TestInterface) toCastable(testObj, TestInterface.class);

        assertNotNull(ti);

        ti = (TestInterface) shallowClone(testObj);

        assertNotNull(ti);

        // Deep clone.
        ti = (TestInterface) deepClone(testObj);

        assertNotNull(ti);
    }

    @Test
    public void testAutoProxy() {
        JarClassLoader jc = new JarClassLoader();
        jc.add("./target/test-jcl.jar");

        // Set default to cglib
        setDefaultProxyProvider(new CglibProxyProvider());

        // Create auto proxies
        JclObjectFactory factory = getInstance(true);
        TestInterface test = (TestInterface) factory.create(jc, "io.mypojo.jcl.test.Test");

        assertNotNull(test);
    }

    @Test
    public void testUnloading() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException {
        JarClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        Object testObj = null;
        jc.loadClass("io.mypojo.jcl.test.Test");
        jc.unloadClass("io.mypojo.jcl.test.Test");

        try {
            testObj = jc.loadClass("io.mypojo.jcl.test.Test").newInstance();

            // Must have been loaded by a CL other than JCL-Local
            assertFalse(testObj.getClass().getClassLoader().equals("io.mypojo.jcl.JarClassLoader"));
            return;
        } catch (ClassNotFoundException cnfe) {
            // expected if not found
        }

        assertNull(testObj);
    }

    @Test
    public void testEnabledFlag() {
        JarClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});
        jc.getLocalLoader().setEnabled(false);
        jc.getCurrentLoader().setEnabled(false);
        jc.getParentLoader().setEnabled(false);
        jc.getSystemLoader().setEnabled(false);
        jc.getThreadLoader().setEnabled(false);

        String cls = "io.mypojo.jcl.test.Test";
        try {
            jc.loadClass(cls);
        } catch (ClassNotFoundException e) {
            // expected
            return;
        }

        throw new AssertionError("Expected: ClassNotFoundException " + cls);
    }

    @Test
    public void testOsgiBootLoading() throws ClassNotFoundException {
        JarClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        OsgiBootLoader obl = (OsgiBootLoader) jc.getOsgiBootLoader();
        obl.setEnabled(true);
        obl.setStrictLoading(true);

        // Load with parent among all java core classes
        obl.setBootDelagation(new String[]{"io.mypojo.jcl.test.*"});

        assertEquals("sun.misc.Launcher$AppClassLoader", jc.loadClass("io.mypojo.jcl.test.Test")
                .getClassLoader().getClass().getName());
    }

    @Test
    public void testXmlContextLoader() throws ClassNotFoundException {
        XmlContextLoader cl = new XmlContextLoader("classpath:jcl.xml");
        cl.loadContext();

        get("jcl1").loadClass("io.mypojo.jcl.test.Test");

        try {
            get("jcl2").loadClass("io.mypojo.jcl.test.Test");
            throw new AssertionFailedError("expected ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            // expected
        }

        assertEquals("sun.misc.Launcher$AppClassLoader",
                get("jcl3").loadClass("io.mypojo.jcl.test.Test").getClassLoader().getClass()
                        .getName());
    }

    //@Test
    public void testDefaultContextLoader() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        JarClassLoader jc = new JarClassLoader(new String[]{"./target/test-jcl.jar"});

        try {
            JclContextLoader contextLoader = new DefaultContextLoader(jc);

            throw new AssertionFailedError("Expected JclContextException");
        } catch (JclContextException e) {
            // Expected because the context is already loaded by the previous
            // test "testXmlContextLoader()"
        }

        // Destroy existing context loaded by testXmlContextLoader()
        destroy();

        JclContextLoader contextLoader = new DefaultContextLoader(jc);
        contextLoader.loadContext();

        // Test context
        Object testObj = get().loadClass("io.mypojo.jcl.test.Test").newInstance();
        assertNotNull(testObj);
        assertEquals("io.mypojo.jcl.JarClassLoader", testObj.getClass().getClassLoader().getClass()
                .getName());
    }
}
