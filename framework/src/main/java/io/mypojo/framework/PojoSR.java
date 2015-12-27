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

import io.mypojo.framework.launch.BundleDescriptor;
import io.mypojo.framework.launch.ClasspathScanner;
import io.mypojo.framework.launch.PojoServiceRegistry;
import io.mypojo.framework.launch.PojoServiceRegistryFactory;
import io.mypojo.framework.launch.impl.PojoServiceRegistryFactoryImpl;
import io.mypojo.framework.revision.DirRevision;
import io.mypojo.framework.revision.JarRevision;
import io.mypojo.framework.revision.Revision;
import io.mypojo.framework.revision.URLRevision;
import io.mypojo.framework.services.*;
import org.osgi.framework.*;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;

@SuppressWarnings("PackageAccessibility")
public class PojoSR implements PojoServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PojoSR.class);

    private final PojoSRInternals internals = new PojoSRInternals();

    public PojoSR(Map config) throws Exception {
        logger.info("Initialising PojoSR");

        internals.bundleConfig.putAll(config);

        Bundle b = PojoSRCoreBundle.newPojoSRCoreBundle(internals, 1); // we must be bundle 1, as bundle 0 should be osgi.core
        internals.m_symbolicNameToBundle.put(b.getSymbolicName(), b);

        b.start();

        b.getBundleContext().registerService(StartLevel.class.getName(), new StartLevelImpl(), null);
        b.getBundleContext().registerService(PackageAdmin.class.getName(), new PackageAdminImpl(this.internals, b), null);
        b.getBundleContext().registerService(BundleStartLevel.class.getName(), new BundleStartLevelImpl(), null);
        b.getBundleContext().registerService(FrameworkStartLevel.class.getName(), new FrameworkStartLevelImpl(), null);
        b.getBundleContext().registerService(LogService.class.getName(), new LogServiceImpl(), null);
        b.getBundleContext().registerService(ConfigurationAdmin.class.getName(), new ConfigurationAdminImpl(this.internals), null);

        internals.m_context = b.getBundleContext();

        @SuppressWarnings("unchecked")
        List<BundleDescriptor> scan = (List<BundleDescriptor>) config.get(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS);

        if (scan != null) {
            logger.info("Starting bundles");
            startBundles(scan);
        }
    }

    public static void main(String[] args) throws Exception {
        Filter filter = null;
        Class main = null;
        for (int i = 0; (args != null) && (i < args.length) && (i < 2); i++) {
            try {
                filter = FrameworkUtil.createFilter(args[i]);
            } catch (InvalidSyntaxException ie) {
                try {
                    main = PojoSR.class.getClassLoader().loadClass(args[i]);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Argument is neither a filter nor a class: " + args[i]);
                }
            }
        }
        Map config = new HashMap();
        config.put(
                PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS,
                (filter != null) ? new ClasspathScanner()
                        .scanForBundles(filter.toString()) : new ClasspathScanner()
                        .scanForBundles());
        new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry(config);
        if (main != null) {
            int count = 0;
            if (filter != null) {
                count++;
            }
            if (main != null) {
                count++;
            }
            String[] newArgs = args;
            if (count > 0) {
                newArgs = new String[args.length - count];
                System.arraycopy(args, count, newArgs, 0, newArgs.length);
            }
            main.getMethod("main", String[].class).invoke(null, (Object) newArgs);
        }
    }

    public void startBundles(List<BundleDescriptor> scan) throws Exception {
        for (BundleDescriptor desc : scan) {
            URL u = new URL(desc.getUrl().toExternalForm() + "META-INF/MANIFEST.MF");
            Revision r;
            if (u.toExternalForm().startsWith("file:")) {
                File root = new File(URLDecoder.decode(desc.getUrl().getFile(), "UTF-8"));
                u = root.toURL();
                r = new DirRevision(root);
            } else {
                URLConnection uc = u.openConnection();
                if (uc instanceof JarURLConnection) {
                    String target = ((JarURLConnection) uc).getJarFileURL().toExternalForm();
                    String prefix = null;
                    if (!("jar:" + target + "!/").equals(desc.getUrl().toExternalForm())) {
                        prefix = desc.getUrl().toExternalForm().substring(("jar:" + target + "!/").length());
                    }
                    r = new JarRevision(
                            ((JarURLConnection) uc).getJarFile(),
                            ((JarURLConnection) uc).getJarFileURL(),
                            prefix,
                            uc.getLastModified());
                } else {
                    r = new URLRevision(desc.getUrl(), desc.getUrl().openConnection().getLastModified());
                }
            }
            Map<String, String> bundleHeaders = desc.getHeaders();
            Version osgiVersion = null;
            try {
                osgiVersion = Version.parseVersion(bundleHeaders.get(Constants.BUNDLE_VERSION));
            } catch (Exception ex) {
                logger.error("Error parsing version: " + bundleHeaders.get(Constants.BUNDLE_VERSION), ex);
                osgiVersion = Version.emptyVersion;
            }
            String sym = bundleHeaders.get(Constants.BUNDLE_SYMBOLICNAME);
            if (sym != null) {
                int idx = sym.indexOf(';');
                if (idx > 0) {
                    sym = sym.substring(0, idx);
                }
                sym = sym.trim();
            }

            if ((sym == null)
                    || !internals.m_symbolicNameToBundle.containsKey(sym)) {
                // TODO: framework - support multiple versions

                // special for osgi.core as it must be bundle id 0
                boolean core = "osgi.core".equals(sym);
                int id = core ? 0 : internals.m_bundles.size();

                Bundle bundle = new PojoSRBundle(r, bundleHeaders,
                        osgiVersion, desc.getUrl().toExternalForm(), internals.m_reg,
                        internals.m_dispatcher,
                        bundleHeaders.get(Constants.BUNDLE_ACTIVATOR),
                        id,
                        sym,
                        internals.m_bundles, desc.getClassLoader(), internals.bundleConfig);
                if (sym != null) {
                    internals.m_symbolicNameToBundle.put(bundle.getSymbolicName(),
                            bundle);
                }
            }

        }

        logger.info("Will start {} bundles", internals.m_bundles.size());
        for (long i = 0; i < internals.m_bundles.size(); i++) {
            try {
                logger.info("Starting {}: {} ", internals.m_bundles.get(i).getBundleId(), internals.m_bundles.get(i).getLocation());
                internals.m_bundles.get(i).start();
            } catch (Throwable e) {
                logger.error("Unable to start bundle: " + i, e);
            }
        }

    }

    public BundleContext getBundleContext() {
        return internals.m_context;
    }

    public void addServiceListener(ServiceListener listener, String filter)
            throws InvalidSyntaxException {
        internals.m_context.addServiceListener(listener, filter);
    }

    public void addServiceListener(ServiceListener listener) {
        internals.m_context.addServiceListener(listener);
    }

    public void removeServiceListener(ServiceListener listener) {
        internals.m_context.removeServiceListener(listener);
    }

    public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
        return internals.m_context.registerService(clazzes, service, properties);
    }

    public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
        return internals.m_context.registerService(clazz, service, properties);
    }

    public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return internals.m_context.getServiceReferences(clazz, filter);
    }

    public ServiceReference getServiceReference(String clazz) {
        return internals.m_context.getServiceReference(clazz);
    }

    public Object getService(ServiceReference reference) {
        return internals.m_context.getService(reference);
    }

    public boolean ungetService(ServiceReference reference) {
        return internals.m_context.ungetService(reference);
    }

}
