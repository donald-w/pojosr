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

import io.mypojo.jcl.sample.Test1;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * DelegateProxyClassLoaderTest test with DelegateProxyClassLoader.
 *
 * @author <a href="mailto:jguibert@intelligents-ia.com" >Jerome Guibert</a>
 */
public class DelegateProxyClassLoaderTest {

    @Test
    public void checkDelegateProxyClassLoader() throws ClassNotFoundException {
        /**
         * First classLoader without Test1
         */
        JarClassLoader classLoader = new JarClassLoader();
        doIsolated(classLoader);
        assertTrue(classLoader.getLocalLoader().isEnabled());
        try {
            classLoader.loadClass(Test1.class.getName());
            fail("Should obtain java.lang.ClassNotFoundException: io.mypojo.jcl.sample.Test1");
        } catch (ClassNotFoundException e) {
        }
        /**
         * target  classLoader with Test1
         */
        JarClassLoader target = new JarClassLoader();
        doIsolated(classLoader);
        assertTrue(classLoader.getLocalLoader().isEnabled());
        target.add(Test1.class.getName());
        target.loadClass(Test1.class.getName());
        /**
         * Add delegate
         */
        classLoader.addLoader(new DelegateProxyClassLoader(target));
        classLoader.loadClass(Test1.class.getName());
    }

    /**
     * Only local loader.
     *
     * @param classLoader
     */
    protected void doIsolated(JarClassLoader classLoader) {
        classLoader.getCurrentLoader().setEnabled(false);
        classLoader.getParentLoader().setEnabled(false);
        classLoader.getThreadLoader().setEnabled(false);
        classLoader.getSystemLoader().setEnabled(false);
        classLoader.getOsgiBootLoader().setEnabled(false);
    }
}
