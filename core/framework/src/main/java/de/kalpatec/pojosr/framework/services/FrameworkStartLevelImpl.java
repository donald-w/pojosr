package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
public class FrameworkStartLevelImpl implements FrameworkStartLevel {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkStartLevelImpl.class);

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
