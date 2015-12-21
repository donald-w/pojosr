package saxactivator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	public void start(BundleContext bundleContext) throws Exception {
		bundleContext.registerService(
		        javax.xml.parsers.SAXParserFactory.class.getName(),
		          javax.xml.parsers.SAXParserFactory.newInstance(), null);	
	}

	public void stop(BundleContext context) throws Exception {
	}
}
