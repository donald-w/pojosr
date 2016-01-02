/*
 * Copyright 2016 Donald W - github@donaldw.com
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
