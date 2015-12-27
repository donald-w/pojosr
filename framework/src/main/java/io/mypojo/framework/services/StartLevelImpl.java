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
