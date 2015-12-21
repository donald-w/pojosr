package com.donaldw.pojosr.test.bundles.bundle2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author donald-w
 */
public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Bundle 2");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Bundle 2");
    }
}
