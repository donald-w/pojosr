package io.mypojo.test.ipojotest;

import io.mypojo.test.bundles.diagnostic.DiagnosticUtils;
import io.mypojo.framework.launch.ClasspathScanner;
import io.mypojo.framework.launch.PojoServiceRegistry;
import io.mypojo.framework.launch.PojoServiceRegistryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author donald-w
 */
public class IPojoTest {
    private Logger logger = LoggerFactory.getLogger(IPojoTest.class);

    public static void main(String[] args) throws Exception {
        IPojoTest test = new IPojoTest();
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

        logger.debug("Sleeping for a bit");
        Thread.sleep(2000);

        DiagnosticUtils.dumpBundles(logger,registry.getBundleContext());
    }
}
