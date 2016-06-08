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

package io.mypojo.test;

import io.mypojo.framework.launch.ClasspathScanner;
import io.mypojo.framework.launch.PojoServiceRegistry;
import io.mypojo.framework.launch.PojoServiceRegistryFactory;
import io.mypojo.jcl.JarClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author donald-w
 */
public class FeaturesTest {
    private static final Logger logger = LoggerFactory.getLogger(FeaturesTest.class);

    private static final String bundle1Jar = "./target/features-repo/io/mypojo/test/test-artifacts/bundle1/0.0.4-SNAPSHOT/bundle1-0.0.4-SNAPSHOT.jar";
    private static final String bundle2Jar = "./target/features-repo/io/mypojo/test/test-artifacts/bundle2/0.0.4-SNAPSHOT/bundle2-0.0.4-SNAPSHOT.jar";

    @Test
    public void testMultiBundleStart() throws Exception {
        JarClassLoader jc = new JarClassLoader();

        Assert.assertTrue(new File(bundle1Jar).exists());
        Assert.assertTrue(new File(bundle2Jar).exists());

        jc.add(bundle1Jar);
        jc.add(bundle2Jar);

        Map config = new HashMap();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles(jc));

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        logger.debug("Sleeping for a bit");
        Thread.sleep(2000);
    }
}
