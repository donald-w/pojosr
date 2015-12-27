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

package io.mypojo.framework;

import io.mypojo.felix.framework.ServiceRegistry;
import io.mypojo.felix.framework.util.EventDispatcher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PackageAccessibility")
public class PojoSRInternals {
    public BundleContext m_context;
    public final Map<String, Bundle> m_symbolicNameToBundle = new HashMap<>();
    public final ServiceRegistry m_reg = new ServiceRegistry(
            new ServiceRegistry.ServiceRegistryCallbacks() {
                public void serviceChanged(ServiceEvent event,
                                           Dictionary oldProps) {
                    m_dispatcher.fireServiceEvent(event, oldProps, null);
                }
            });
    public final EventDispatcher m_dispatcher = new EventDispatcher(m_reg);
    public final Map<Long, Bundle> m_bundles = new HashMap<>();
    public final Map bundleConfig = new HashMap();
}
