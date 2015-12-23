package de.kalpatec.pojosr.framework.services;

import de.kalpatec.pojosr.framework.PojoSR;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

/**
 * @author donald-w
 */
public class PackageAdminImpl implements PackageAdmin {

    private final Bundle b;
    private PojoSR pojoSR;

    public PackageAdminImpl(PojoSR pojoSR, Bundle b) {
        this.pojoSR = pojoSR;
        this.b = b;
    }

    public boolean resolveBundles(Bundle[] bundles) {
        // TODO Auto-generated method stub
        return true;
    }

    public void refreshPackages(Bundle[] bundles) {
        pojoSR.m_dispatcher.fireFrameworkEvent(new FrameworkEvent(
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
        Bundle result = pojoSR.m_symbolicNameToBundle.get((symbolicName != null) ? symbolicName.trim() : symbolicName);
        if (result != null) {
            return new Bundle[]{result};
        }
        return null;
    }

    public int getBundleType(Bundle bundle) {
        return 0;
    }

    public Bundle getBundle(Class clazz) {
        return pojoSR.m_context.getBundle();
    }
}
