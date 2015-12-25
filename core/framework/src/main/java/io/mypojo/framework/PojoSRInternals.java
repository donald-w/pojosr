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
    public final Map<Long, Bundle> m_bundles = new HashMap<Long, Bundle>();
    public final Map bundleConfig = new HashMap();
}
