package com.donaldw.pojosr.test.integration;

import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
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
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles("(!(Bundle-SymbolicName=com.donaldw.pojosr.test.bundles.bundle2))"));

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        Assert.assertTrue(bundles.length > 0);
    }
}
