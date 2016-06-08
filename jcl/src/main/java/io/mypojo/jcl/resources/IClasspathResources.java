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

package io.mypojo.jcl.resources;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * @author donald-w
 */
public interface IClasspathResources {
    void loadResource(String resourceName);

    void loadResource(URL url);

    void loadJar(String o, InputStream jarStream);

    byte[] getResource(String className);

    URL getResourceURL(String name);

    Map<String, byte[]> getResources();

    void unload(String s);
}
