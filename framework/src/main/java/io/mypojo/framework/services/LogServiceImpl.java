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
 *
 */

package io.mypojo.framework.services;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author donald-w
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
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
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
