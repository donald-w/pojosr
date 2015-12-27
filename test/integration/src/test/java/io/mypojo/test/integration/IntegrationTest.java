/*
 * Copyright 2015 Donald W - github@donaldw.com
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
 *
 */

package io.mypojo.test.integration;

import io.mypojo.framework.launch.BundleDescriptor;
import io.mypojo.framework.launch.ClasspathScanner;
import io.mypojo.framework.launch.PojoServiceRegistry;
import io.mypojo.framework.launch.PojoServiceRegistryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author donald-w
 */
public class IntegrationTest {

    @Test
    public void testLaunch() throws Exception {
        Map<String, List<BundleDescriptor>> config = new HashMap<>();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles());

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        Assert.assertTrue(bundles.length > 0);
    }

    @Test
    public void testLaunchFiltered() throws Exception {
        Map<String, List<BundleDescriptor>> config = new HashMap<>();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles("(!(Bundle-SymbolicName=io.mypojo.test.bundles.bundle2))"));

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        Assert.assertTrue(bundles.length > 0);
    }
}
