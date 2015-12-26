package io.mypojo.framework.services;

import io.mypojo.framework.PojoSRInternals;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author donald-w
 */
public class ConfigurationAdminImpl implements ConfigurationAdmin {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationAdminImpl.class);

    private final PojoSRInternals internals;

    public ConfigurationAdminImpl(PojoSRInternals internals) {
        this.internals = internals;
    }

    @Override
    public Configuration createFactoryConfiguration(String factoryPid) throws IOException {
        return null;
    }

    @Override
    public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException {
        return null;
    }

    @Override
    public Configuration getConfiguration(String pid, String location) throws IOException {
        return null;
    }

    @Override
    public Configuration getConfiguration(String pid) throws IOException {
        return null;
    }

    @Override
    public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException {
        return new Configuration[0];
    }
}
