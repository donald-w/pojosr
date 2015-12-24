package io.mypojo.test.bundles.ipojocanary;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
 */
@Instantiate()
@Component(name = "canary")
public class Canary {
    Logger logger = LoggerFactory.getLogger(Canary.class);

    @Validate
    public void validate() {
        logger.info("Validating");
    }

    @Invalidate
    public void invalidate() {
        logger.info("Invalidating");
    }
}
