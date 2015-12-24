package io.mypojo.launch;


import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ClasspathScannerTest {

    @Test
    public void testClassPathScannerTest() throws Exception {
        ClasspathScanner scanner = new ClasspathScanner();

        List<BundleDescriptor> bundles = scanner.scanForBundles();

        Assert.assertTrue(bundles.size() > 0);
    }
}
