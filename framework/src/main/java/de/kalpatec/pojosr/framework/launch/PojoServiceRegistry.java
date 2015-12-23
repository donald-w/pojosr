/* 
 * Copyright 2011 Karl Pauls karlpauls@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kalpatec.pojosr.framework.launch;

import org.osgi.framework.*;

import java.util.Dictionary;
import java.util.List;

@SuppressWarnings("unused")
public interface PojoServiceRegistry {
    BundleContext getBundleContext();

    void startBundles(List<BundleDescriptor> bundles) throws Exception;

    void addServiceListener(ServiceListener listener, String filter)
            throws InvalidSyntaxException;

    void addServiceListener(ServiceListener listener);

    void removeServiceListener(ServiceListener listener);

    ServiceRegistration registerService(String[] clazzes, Object service, @SuppressWarnings("rawtypes") Dictionary properties);

    ServiceRegistration registerService(String clazz, Object service, @SuppressWarnings("rawtypes") Dictionary properties);

    ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException;

    ServiceReference getServiceReference(String clazz);

    Object getService(ServiceReference reference);

    boolean ungetService(ServiceReference reference);
}
