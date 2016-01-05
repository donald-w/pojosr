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
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author donald-w
 */
public class FeaturesTest {
    private static Logger logger = LoggerFactory.getLogger(FeaturesTest.class);

    public static void main(String[] args) throws Exception {

        JarClassLoader jc = new JarClassLoader();

        jc.add("./target/features-repo/io/mypojo/test/test-artifacts/bundle1/0.0.4-SNAPSHOT/bundle1-0.0.4-SNAPSHOT.jar");
        jc.add("./target/features-repo/io/mypojo/test/test-artifacts/bundle2/0.0.4-SNAPSHOT/bundle2-0.0.4-SNAPSHOT.jar");
//        jc.add("./target/features-repo/io/mypojo/test/bundles/bundle3/0.0.4-SNAPSHOT/bundle3-0.0.4-SNAPSHOT.jar");

        Map config = new HashMap();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles(jc));

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        logger.debug("Sleeping for a bit");
        Thread.sleep(2000);
    }
}
