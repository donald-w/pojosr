package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.BundleStartLevel;

/**
 * @author donald-w
 */
public class BundleStartLevelImpl implements BundleStartLevel {
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
