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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * AbstractClassLoaderTest test case on AbstractClassLoader class.
 *
 * @author <a href="mailto:jguibert@intelligents-ia.com" >Jerome Guibert</a>
 */
public class AbstractClassLoaderTest {

    @Test
    public void checkInitializationOfDefaultProxyClassLoader() {
        AbstractClassLoader classLoader = new AbstractClassLoader() {
        };

        assertNotNull("SystemLoader should not be null", classLoader.getSystemLoader());
        assertNotNull("ThreadLoader should not be null", classLoader.getThreadLoader());
        assertNotNull("ParentLoader should not be null", classLoader.getParentLoader());
        assertNotNull("CurrentLoader should not be null", classLoader.getCurrentLoader());
        assertNotNull("OsgiBootLoader should not be null", classLoader.getOsgiBootLoader());

        assertEquals("SystemLoader order should be 50", 50, classLoader.getSystemLoader().getOrder());
        assertEquals("ThreadLoader order should be 40", 40, classLoader.getThreadLoader().getOrder());
        assertEquals("ParentLoader order should be 30", 30, classLoader.getParentLoader().getOrder());
        assertEquals("CurrentLoader order should be 20", 20, classLoader.getCurrentLoader().getOrder());
        assertEquals("OsgiBootLoader order should be 0", 0, classLoader.getOsgiBootLoader().getOrder());

    }

    @Test
    public void checkDefaultEnabledProxy() {
        AbstractClassLoader classLoader = new AbstractClassLoader() {
        };

        assertEquals(Configuration.isCurrentLoaderEnabled(), classLoader.getCurrentLoader().isEnabled());
        assertEquals(Configuration.isParentLoaderEnabled(), classLoader.getParentLoader().isEnabled());
        assertEquals(Configuration.isThreadContextLoaderEnabled(), classLoader.getThreadLoader().isEnabled());
        assertEquals(Configuration.isSystemLoaderEnabled(), classLoader.getSystemLoader().isEnabled());
        assertEquals(Configuration.isOsgiBootDelegationEnabled(), classLoader.getOsgiBootLoader().isEnabled());

    }
}
