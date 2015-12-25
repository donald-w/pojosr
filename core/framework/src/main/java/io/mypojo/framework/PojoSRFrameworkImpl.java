package io.mypojo.framework;

import io.mypojo.framework.launch.ClasspathScanner;
import io.mypojo.framework.launch.PojoServiceRegistry;
import org.osgi.framework.*;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

class PojoSRFrameworkImpl implements Framework {
    private final String m_filter;
    private volatile Bundle m_bundle = null;
    private volatile PojoServiceRegistry m_reg = null;

    public PojoSRFrameworkImpl(Map configuration) {
        m_filter = (String) configuration.get("pojosr.filter");
    }

    public void init() throws BundleException {
        try {
            m_reg = new PojoServiceRegistryFactoryImpl()
                    .newPojoServiceRegistry(new HashMap());
            m_bundle = m_reg.getBundleContext()
                    .getBundle();
        } catch (Exception ex) {
            throw new BundleException("Unable to scan classpath", ex);
        }
    }

    public int getState() {
        return (m_bundle == null) ? Bundle.INSTALLED : m_bundle.getState();
    }

    public void start(int options) throws BundleException {
        start();
    }

    public void start() throws BundleException {
        try {
            m_reg.startBundles((m_filter != null) ? new ClasspathScanner()
                    .scanForBundles(m_filter)
                    : new ClasspathScanner().scanForBundles());
        } catch (Exception e) {
            throw new BundleException("Error starting framework", e);
        }
    }

    public void stop(int options) throws BundleException {
        m_bundle.stop(options);
    }

    public void stop() throws BundleException {
        m_bundle.stop();
    }

    public void update(InputStream input) throws BundleException {
        m_bundle.update(input);
    }

    public void update() throws BundleException {
        m_bundle.update();
    }

    public void uninstall() throws BundleException {
        m_bundle.uninstall();
    }

    public Dictionary getHeaders() {
        return m_bundle.getHeaders();
    }

    public long getBundleId() {
        return m_bundle.getBundleId();
    }

    public String getLocation() {
        return m_bundle.getLocation();
    }

    public ServiceReference[] getRegisteredServices() {
        return m_bundle.getRegisteredServices();
    }

    public ServiceReference[] getServicesInUse() {
        return m_bundle.getServicesInUse();
    }

    public boolean hasPermission(Object permission) {
        return m_bundle.hasPermission(permission);
    }

    public URL getResource(String name) {
        return m_bundle.getResource(name);
    }

    public Dictionary getHeaders(String locale) {
        return m_bundle.getHeaders(locale);
    }

    public String getSymbolicName() {
        return m_bundle.getSymbolicName();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return m_bundle.loadClass(name);
    }

    public Enumeration getResources(String name) throws IOException {
        return m_bundle.getResources(name);
    }

    public Enumeration getEntryPaths(String path) {
        return m_bundle.getEntryPaths(path);
    }

    public URL getEntry(String path) {
        return m_bundle.getEntry(path);
    }

    public long getLastModified() {
        return m_bundle.getLastModified();
    }

    public Enumeration findEntries(String path, String filePattern,
                                   boolean recurse) {
        return m_bundle.findEntries(path, filePattern, recurse);
    }

    public BundleContext getBundleContext() {
        return m_bundle.getBundleContext();
    }

    public Map getSignerCertificates(int signersType) {
        return m_bundle.getSignerCertificates(signersType);
    }

    public Version getVersion() {
        return m_bundle.getVersion();
    }

    public FrameworkEvent waitForStop(long timeout)
            throws InterruptedException {
        final Object lock = new Object();

        m_bundle.getBundleContext().addBundleListener(new SynchronousBundleListener() {

            public void bundleChanged(BundleEvent event) {
                if ((event.getBundle() == m_bundle) && (event.getType() == BundleEvent.STOPPED)) {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        synchronized (lock) {
            while (m_bundle.getState() != Bundle.RESOLVED) {
                if (m_bundle.getState() == Bundle.STOPPING) {
                    lock.wait(100);
                } else {
                    lock.wait();
                }
            }
        }
        return new FrameworkEvent(FrameworkEvent.STOPPED, m_bundle, null);
    }

    public File getDataFile(String filename) {
        return m_bundle.getDataFile(filename);
    }

    public int compareTo(Bundle o) {
        if (o == this) {
            return 0;
        }
        return m_bundle.compareTo(o);
    }

    public <A> A adapt(Class<A> type) {
        return m_bundle.adapt(type);
    }

}
