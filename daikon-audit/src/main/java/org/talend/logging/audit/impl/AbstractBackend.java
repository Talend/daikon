package org.talend.logging.audit.impl;

import org.talend.logging.audit.LogLevel;

import java.util.Map;

/**
 *
 */
public abstract class AbstractBackend {

    private static final char LOGGER_DELIM = '.';

    protected final AuditConfigurationMap config;

    protected final String loggerPrefix;

    public AbstractBackend(AuditConfigurationMap config) {
        this.config = config;
        this.loggerPrefix = AuditConfiguration.ROOT_LOGGER.getString(config) + LOGGER_DELIM;
    }

    public abstract void log(String category, LogLevel level, String message, Throwable throwable);

    public abstract Map<String, String> getCopyOfContextMap();

    public abstract void setContextMap(Map<String, String> newContext);

}
