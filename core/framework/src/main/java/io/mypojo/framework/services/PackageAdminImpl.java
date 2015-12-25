package io.mypojo.framework.services;

import io.mypojo.framework.PojoSRInternals;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
public class PackageAdminImpl implements PackageAdmin {
    private static final Logger logger = LoggerFactory.getLogger(PackageAdminImpl.class);

    private final Bundle b;
    private PojoSRInternals pojoSRInternals;

    public PackageAdminImpl(PojoSRInternals pojoSRInternals, Bundle b) {
        this.pojoSRInternals = pojoSRInternals;
        this.b = b;
    }

    public boolean resolveBundles(Bundle[] bundles) {
        // TODO Auto-generated method stub
        return true;
    }

    public void refreshPackages(Bundle[] bundles) {
        pojoSRInternals.m_dispatcher.fireFrameworkEvent(new FrameworkEvent(
                FrameworkEvent.PACKAGES_REFRESHED, b, null));
    }

    public RequiredBundle[] getRequiredBundles(
            String symbolicName) {
        return null;
    }

    public Bundle[] getHosts(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle[] getFragments(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    public ExportedPackage[] getExportedPackages(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public ExportedPackage[] getExportedPackages(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    public ExportedPackage getExportedPackage(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle[] getBundles(String symbolicName,
                               String versionRange) {
        Bundle result = pojoSRInternals.m_symbolicNameToBundle.get((symbolicName != null) ? symbolicName.trim() : symbolicName);
        if (result != null) {
            return new Bundle[]{result};
        }
        return null;
    }

    public int getBundleType(Bundle bundle) {
        return 0;
    }

    public Bundle getBundle(Class clazz) {
        return pojoSRInternals.m_context.getBundle();
    }
}
