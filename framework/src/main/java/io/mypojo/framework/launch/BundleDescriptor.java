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
package io.mypojo.framework.launch;

import java.net.URL;
import java.util.Map;

public class BundleDescriptor {
    private final ClassLoader classLoader;
    private final URL url;
    private final Map<String, String> headers;

    public BundleDescriptor(ClassLoader classLoader, URL url, Map<String, String> headers) {
        this.classLoader = classLoader;
        this.url = url;
        this.headers = headers;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public URL getUrl() {
        return this.url;
    }

    public String toString() {
        return this.url.toExternalForm();
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
