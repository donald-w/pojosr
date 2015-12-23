package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.Bundle;
import org.osgi.service.startlevel.StartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
public class StartLevelImpl implements StartLevel {
    private static final Logger logger = LoggerFactory.getLogger(StartLevelImpl.class);

    public void setBundleStartLevel(Bundle bundle,
                                    int startlevel) {
        // TODO Auto-generated method stub

    }

    public boolean isBundlePersistentlyStarted(Bundle bundle) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isBundleActivationPolicyUsed(Bundle bundle) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getStartLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    public void setStartLevel(int startlevel) {
        // TODO Auto-generated method stub

    }

    public int getInitialBundleStartLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    public void setInitialBundleStartLevel(int startlevel) {
        // TODO Auto-generated method stub

    }

    public int getBundleStartLevel(Bundle bundle) {
        // TODO Auto-generated method stub
        return 1;
    }
}
