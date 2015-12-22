package com.donaldw.pojosr.test.ipojotest;

import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author donald-w
 */
public class IPojoTestTest {

    public static void main(String[] args) throws Exception {
        IPojoTestTest test = new IPojoTestTest();

        test.testLaunch();
    }

    @Test
    public void testLaunch() throws Exception {
        Map config = new HashMap();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles());

        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

        PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);

        Bundle[] bundles = registry.getBundleContext().getBundles();

        Assert.assertTrue(bundles.length > 0);

        Thread.sleep(5000);
    }
}
