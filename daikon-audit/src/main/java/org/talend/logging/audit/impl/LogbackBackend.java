package org.talend.logging.audit.impl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.MDC;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.LogbackConfigurer;

import java.util.Map;

/**
 * Backend for both slf4j and Logback.
 */
class LogbackBackend extends AbstractBackend {

    private final LoggerContext loggerContext;

    public LogbackBackend(AuditConfigurationMap config) {
        super(config);

        this.loggerContext = new LoggerContext();

        LogbackConfigurer.configure(config, loggerContext);
    }

    @Override
    public void log(String category, LogLevel level, String message, Throwable throwable) {
        final Logger logger = this.loggerContext.getLogger(loggerPrefix + category);

        switch (level) {
        case INFO:
            logger.info("{}", message, throwable);
            break;

        case WARNING:
            logger.warn("{}", message, throwable);
            break;

        case ERROR:
            logger.error("{}", message, throwable);
            break;

        default:
            throw new IllegalArgumentException("Unsupported audit log level " + level);
        }
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }

    @Override
    public void setContextMap(Map<String, String> newContext) {
        MDC.setContextMap(newContext);
    }
}
