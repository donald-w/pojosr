package de.kalpatec.pojosr.framework.services;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donaldw
 */
public class LogServiceImpl implements LogService {

    private Logger logger = LoggerFactory.getLogger(LogService.class);

        @Override
        public void log(int level, String message) {
        switch (level) {
            case LogService.LOG_ERROR:
                logger.error(message);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message);
                break;
            case LogService.LOG_INFO:
                logger.info(message);
                break;
            case LogService.LOG_DEBUG:
                logger.debug(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

        @Override
        public void log(int level, String message, Throwable exception) {
        switch (level) {
            case LogService.LOG_ERROR:
                logger.error(message,exception);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message,exception);
                break;
            case LogService.LOG_INFO:
                logger.info(message,exception);
                break;
            case LogService.LOG_DEBUG:
                logger.debug(message,exception);
                break;
            default:
                logger.info(message,exception);
                break;
        }
    }

        @Override
        public void log(ServiceReference sr, int level, String message) {
        switch (level) {
            case LogService.LOG_ERROR:
                logger.error(message);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message);
                break;
            case LogService.LOG_INFO:
                logger.info(message);
                break;
            case LogService.LOG_DEBUG:
                logger.debug(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

        @Override
        public void log(ServiceReference sr, int level, String message, Throwable exception){
        switch (level) {
            case LogService.LOG_ERROR:
                logger.error(message, exception);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message, exception);
                break;
            case LogService.LOG_INFO:
                logger.info(message, exception);
                break;
            case LogService.LOG_DEBUG:
                logger.debug(message, exception);
                break;
            default:
                logger.info(message, exception);
                break;
        }
    }
}
