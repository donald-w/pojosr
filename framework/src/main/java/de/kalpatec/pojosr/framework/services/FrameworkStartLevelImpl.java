package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.FrameworkStartLevel;

/**
 * @author donald-w
 */
public class FrameworkStartLevelImpl implements FrameworkStartLevel {
    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public void setStartLevel(int startlevel, FrameworkListener... listeners) {

    }

    @Override
    public int getInitialBundleStartLevel() {
        return 0;
    }

    @Override
    public void setInitialBundleStartLevel(int startlevel) {

    }

    @Override
    public Bundle getBundle() {
        return null;
    }
}
