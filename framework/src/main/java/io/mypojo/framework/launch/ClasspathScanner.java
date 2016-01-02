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

package io.mypojo.framework.launch;

import io.mypojo.felix.framework.util.MapToDictionary;
import io.mypojo.framework.launch.impl.ManifestUtil;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PackageAccessibility")
public class ClasspathScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathScanner.class);

    public List<BundleDescriptor> scanForBundles() throws Exception {
        return scanForBundles(null, null);
    }

    @SuppressWarnings("unused")
    public List<BundleDescriptor> scanForBundles(ClassLoader loader) throws Exception {
        return scanForBundles(null, loader);
    }

    public List<BundleDescriptor> scanForBundles(String filterString)
            throws Exception {
        return scanForBundles(filterString, null);
    }

    public List<BundleDescriptor> scanForBundles(String filterString, ClassLoader loader) throws Exception {
        logger.info("Starting classpath scan");
        Filter filter = (filterString != null) ? FrameworkUtil.createFilter(filterString) : null;

        List<BundleDescriptor> bundles = new ArrayList<>();

        loader = (loader != null) ? loader : getClass().getClassLoader();

        List<URL> manifestUrls = Collections.list(loader.getResources("META-INF/MANIFEST.MF"));

        for (URL manifestURL : manifestUrls) {
            Map<String, String> headers = ManifestUtil.getHeaders(manifestURL);

            if ((filter == null) || filter.match(new MapToDictionary(headers))) {
                bundles.add(new BundleDescriptor(loader, getParentURL(manifestURL), headers));
            }
        }
        logger.info("Found {} bundles", bundles.size());
        return bundles;
    }

    private URL getParentURL(URL url) throws Exception {
        String externalForm = url.toExternalForm();
        return new URL(externalForm.substring(0, externalForm.length()
                - "META-INF/MANIFEST.MF".length()));
    }
}
