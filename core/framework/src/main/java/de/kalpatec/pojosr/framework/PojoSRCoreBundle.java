package de.kalpatec.pojosr.framework;

import de.kalpatec.pojosr.framework.revision.Revision;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

class PojoSRCoreBundle extends PojoSRBundle {
    private final Logger logger = LoggerFactory.getLogger(PojoSRCoreBundle.class);

    private final PojoSRInternals internals;

    public static PojoSRCoreBundle newPojoSRCoreBundle(PojoSRInternals internals, int pojoSRBundleId) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.BUNDLE_SYMBOLICNAME,"de.kalpatec.pojosr.framework");
        headers.put(Constants.BUNDLE_VERSION, "0.0.1-SNAPSHOT");
        headers.put(Constants.BUNDLE_NAME, "System Bundle");
        headers.put(Constants.BUNDLE_MANIFESTVERSION, "2");
        headers.put(Constants.BUNDLE_VENDOR, "kalpatec");

        return new PojoSRCoreBundle(PojoSRCoreBundle.class.getClassLoader(), internals, headers, pojoSRBundleId);
    }

    private PojoSRCoreBundle(ClassLoader classLoader, PojoSRInternals internals, Map<String, String> headers, int pojoSRBundleId) {
            super(new Revision() {
                @Override
                public long getLastModified() {
                    return System.currentTimeMillis();
                }

                @Override
                public Enumeration getEntries() {
                    return Collections.emptyEnumeration();
                }

                @Override
                public URL getEntry(String entryName) {
                    return getClass().getClassLoader().getResource(entryName);
                }
            },
                headers,
                new Version(0, 0, 1),
                "file:pojosr",
                internals.m_reg,
                internals.m_dispatcher,
                null,
                pojoSRBundleId,
                "de.kalpatec.pojosr.framework",
                internals.m_bundles,
                classLoader,
                internals.bundleConfig);

        this.internals = internals;
    }

    @Override
    public synchronized void start() throws BundleException {
        if (m_state != Bundle.RESOLVED) {
            return;
        }
        internals.m_dispatcher.startDispatching();
        m_state = Bundle.STARTING;

        internals.m_dispatcher.fireBundleEvent(new BundleEvent(BundleEvent.STARTING,
                this));
        m_context = new PojoSRBundleContext(this, internals.m_reg, internals.m_dispatcher,
                internals.m_bundles, internals.bundleConfig);
        int i = 0;
        for (Bundle b : internals.m_bundles.values()) {
            i++;
            try {
                if (b != this) {
                    b.start();
                }
            } catch (Throwable t) {
                logger.error("Unable to start bundle: " + i, t);
            }
        }
        m_state = Bundle.ACTIVE;
        internals.m_dispatcher.fireBundleEvent(new BundleEvent(BundleEvent.STARTED,
                this));

        internals.m_dispatcher.fireFrameworkEvent(new FrameworkEvent(FrameworkEvent.STARTED, this, null));
        super.start();
    }

    ;

    @Override
    public synchronized void stop() throws BundleException {
        if ((m_state == Bundle.STOPPING) || m_state == Bundle.RESOLVED) {
            return;

        } else if (m_state != Bundle.ACTIVE) {
            throw new BundleException("Can't stop pojosr because it is not ACTIVE");
        }
        final Bundle systemBundle = this;
        Runnable r = new Runnable() {

            public void run() {
                internals.m_dispatcher.fireBundleEvent(new BundleEvent(BundleEvent.STOPPING,
                        systemBundle));
                for (Bundle b : internals.m_bundles.values()) {
                    try {
                        if (b != systemBundle) {
                            b.stop();
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                internals.m_dispatcher.fireBundleEvent(new BundleEvent(BundleEvent.STOPPED,
                        systemBundle));
                m_state = Bundle.RESOLVED;
                internals.m_dispatcher.stopDispatching();
            }
        };
        m_state = Bundle.STOPPING;
        if ("true".equalsIgnoreCase(System.getProperty("de.kalpatec.pojosr.framework.events.sync"))) {
            r.run();
        } else {
            new Thread(r).start();
        }
    }
}
