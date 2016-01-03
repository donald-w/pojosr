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

package io.mypojo.test.bundles.diagnostic;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @author donald-w
 */
public class DiagnosticUtils {
    public static void dumpBundles(Logger logger, BundleContext bc) {
        Bundle[] bundles = bc.getBundles();
        logger.info("**** Listing bundles ****");
        for (Bundle bundle : bundles) {
            logger.info(String.format("[%s] %s %s", bundle.getBundleId(), bundle.getSymbolicName(), bundle.getState()));
        }
        logger.info("**** Done Listing bundles ****");
    }

    public static void dumpBundleHeaders(Logger logger, Bundle bundle) {
        logger.info("**** Listing bundle headers ****");
        Dictionary<String, String> headersDictionary = bundle.getHeaders();
        Enumeration<String> headerNames = headersDictionary.keys();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info(String.format("%s : %s", headerName, headersDictionary.get(headerName)));
        }
        logger.info("**** Done listing bundle headers ****");
    }

    public static void twiddleThumbs(Logger logger, int ms) {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 10; i++) {
            sb.setLength(0);
            sb.append('[');
            for (int j = 0; j < 10; j++) {
                sb.append(j <= i ? '*' : ' ');
            }
            sb.append(']');
            try {
                TimeUnit.MILLISECONDS.sleep(ms / 10);
            } catch (InterruptedException e) {
                //
            }
            logger.info(sb.toString());
        }
    }
}
