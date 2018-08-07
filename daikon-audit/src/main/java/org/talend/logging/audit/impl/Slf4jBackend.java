package org.talend.logging.audit.impl;

import java.util.Map;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.logging.audit.LogLevel;

/**
 * Backend for both slf4j and Logback.
 */
public class Slf4jBackend extends AbstractBackend {

    public Slf4jBackend(AuditConfigurationMap config) {
        super(config);

        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        String logFactoryClz = loggerFactory.getClass().getName();
        if ("org.slf4j.impl.Log4jLoggerFactory".equals(logFactoryClz)) {
            Log4j1Configurer.configure(config);
        } else if ("ch.qos.logback.classic.LoggerContext".equals(logFactoryClz)) {
            LogbackConfigurer.configure(config, (LoggerContext) loggerFactory);
        } else if ("org.apache.logging.slf4j.Log4jLoggerFactory".equals(logFactoryClz)) {
            throw new IllegalArgumentException("Log4j 2.x is not supported.");
        } else {
            throw new IllegalArgumentException(
                    "Only log4j 1.x and Logback are supported. Current logger: " + LoggerFactory.getILoggerFactory().getClass());
        }
    }

    @Override
    public void log(String category, LogLevel level, String message, Throwable throwable) {
        final Logger logger = LoggerFactory.getLogger(loggerPrefix + category);

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
