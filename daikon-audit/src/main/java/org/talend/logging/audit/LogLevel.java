package org.talend.logging.audit;

import org.slf4j.Logger;

/**
 * Log levels for logging audit events.
 */
public enum LogLevel {
    INFO {

        @Override
        public void log(Logger logger, String message, Throwable throwable) {
            logger.info(message, throwable);
        }
    },

    WARNING {

        @Override
        public void log(Logger logger, String message, Throwable throwable) {
            logger.warn(message, throwable);
        }
    },

    ERROR {

        @Override
        public void log(Logger logger, String message, Throwable throwable) {
            logger.error(message, throwable);
        }
    };

    public void log(Logger logger, String message, Throwable throwable) {
        throw new UnsupportedOperationException("This method should be overridden");
    }
}