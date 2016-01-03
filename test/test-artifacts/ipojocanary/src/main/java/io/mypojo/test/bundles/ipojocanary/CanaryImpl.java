/*
 * Copyright 2016 Donald W - github@donaldw.com
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

package io.mypojo.test.bundles.ipojocanary;

import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
@Instantiate()
@Provides(specifications = Canary.class)
@Component(name = "canary")
public class CanaryImpl implements Canary {

    @ServiceProperty(name = "osgi.command.scope", value = "canary")
    String scope;

    @ServiceProperty(name = "osgi.command.function", value = "{doubleMessage}")
    String[] function = new String[]{"doubleMessage"};

    Logger logger = LoggerFactory.getLogger(CanaryImpl.class);

    @Validate
    public void validate() {
        logger.info("Validating");
    }

    @Invalidate
    public void invalidate() {
        logger.info("Invalidating");
    }

    @Override
    public String doubleMessage(String msg) {
        return msg + " " + msg;
    }
}
