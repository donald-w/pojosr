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

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test handling resources inside and outside jars
 */
public class ClasspathResourcesTest {

    @Test
    public void testLoadResource() throws Exception {
        final String name = "test";
        ClasspathResources jarResources = getClasspathResources(name);

        URL resourceURL = jarResources.getResourceURL("test.properties");
        Properties props = getProperties(resourceURL);
        assertEquals("testval", props.getProperty("testkey"));
    }

    @Test
    public void testLoadResourcesFromJar() throws Exception {
        final String name = "test.jar";
        ClasspathResources jarResources = getClasspathResources(name);

        URL resourceURL = jarResources.getResourceURL("test.properties");
        Properties props = getProperties(resourceURL);
        assertEquals("testval in jar", props.getProperty("testkey"));

        resourceURL = jarResources.getResourceURL("test/subdir.properties");
        props = getProperties(resourceURL);
        assertEquals("testval in jar in subdirectory", props.getProperty("testkey"));
    }

    private ClasspathResources getClasspathResources(String name) {
        final URL testJar = ClassLoader.getSystemClassLoader().getResource(name);
        assertNotNull("Could not find file or directory named '" + name + "'. It should be in the test resources directory", testJar);
        ClasspathResources jarResources = new ClasspathResources();
        jarResources.loadResource(testJar);
        return jarResources;
    }

    private Properties getProperties(URL resourceURL) throws IOException {
        assertNotNull(resourceURL);
        Properties props = new Properties();
        props.load(resourceURL.openStream());
        return props;
    }
}
