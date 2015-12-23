package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
public class BundleStartLevelImpl implements BundleStartLevel {
    private static final Logger logger = LoggerFactory.getLogger(BundleStartLevelImpl.class);

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public void setStartLevel(int startlevel) {

    }

    @Override
    public boolean isPersistentlyStarted() {
        return false;
    }

    @Override
    public boolean isActivationPolicyUsed() {
        return false;
    }

    @Override
    public Bundle getBundle() {
        return null;
    }
}
