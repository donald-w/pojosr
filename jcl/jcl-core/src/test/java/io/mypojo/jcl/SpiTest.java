/*
 * Copyright 2016 Donald W - github@donaldw.com
 * Copyright 2015 Kamran Zafar
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by kamran on 23/11/15.
 */
@RunWith(JUnit4.class)
public class SpiTest {
    @Test
    public void spiTest() throws Exception {
        JarClassLoader jcl = new JarClassLoader();
        jcl.add("./target/test-classes/lucene-core-5.3.1.jar");

        Class codecClass = jcl.loadClass("org.apache.lucene.codecs.Codec");

        ServiceLoader serviceLoader = ServiceLoader.load(codecClass, jcl);

        Iterator itr = serviceLoader.iterator();

        Assert.assertTrue(itr.hasNext());

//        while (itr.hasNext()) {
//            System.out.println(itr.next());
//        }
    }
}
