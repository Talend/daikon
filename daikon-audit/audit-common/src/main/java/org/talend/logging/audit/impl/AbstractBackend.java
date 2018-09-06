package org.talend.logging.audit.impl;

import java.util.Map;

import org.talend.logging.audit.LogLevel;

/**
 *
 */
public abstract class AbstractBackend {

    private static final char LOGGER_DELIM = '.';

    protected final String loggerPrefix;

    public AbstractBackend(String rootLogger) {
        this.loggerPrefix = rootLogger + LOGGER_DELIM;
    }

    public abstract void log(String category, LogLevel level, String message, Throwable throwable);

    public abstract Map<String, String> getCopyOfContextMap();

    public abstract void setContextMap(Map<String, String> newContext);

}
