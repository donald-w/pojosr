/*
 * Copyright 2015 Donald W - github@donaldw.com
 * Copyright 2011 Karl Pauls karlpauls@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
