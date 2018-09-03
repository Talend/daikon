package org.talend.logging.audit.logback;

import ch.qos.logback.classic.LoggerContext;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

import java.util.Map;

/**
 * Backend for Logback.
 */
public class LogbackBackend extends AbstractBackend {

    private final ILoggerFactory loggerContext;

    public LogbackBackend(AuditConfigurationMap config) {
        this(config, new LoggerContext());
    }

    protected LogbackBackend(AuditConfigurationMap config, LoggerContext loggerContext) {
        super(AuditConfiguration.ROOT_LOGGER.getString(config));

        this.loggerContext = loggerContext;

        LogbackConfigurer.configure(config, loggerContext);
    }

    protected LogbackBackend(String rootLogger, ILoggerFactory loggerFactory) {
        super(rootLogger);
        this.loggerContext = loggerFactory;
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
