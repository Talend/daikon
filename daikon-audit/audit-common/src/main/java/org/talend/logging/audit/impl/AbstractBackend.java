package org.talend.logging.audit.impl;

import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * Log something when you don't have a message.
     *
     * For instance, when {@link #enableMessageFormat()} return {@code #false}.
     */
    public void log(String category, LogLevel level, Throwable throwable) {
        log(category, level, "", throwable);
    }

    public abstract Map<String, String> getCopyOfContextMap();

    public abstract void setContextMap(Map<String, String> newContext);

    public Map<String, String> setNewContext(Map<String, String> oldContext, Map<String, String> newContext) {
        ContextBuilder builder = ContextBuilder.create();
        if (oldContext != null) {
            builder.with(oldContext);
        }
        if (newContext != null) {
            builder.with(newContext);
        }
        Context completeContext = builder.build();

        this.setContextMap(completeContext);
        return completeContext;
    }

    public Map<String, String> setNewContext(Map<String, String> oldContext) {
        ContextBuilder builder = ContextBuilder.create();
        if (oldContext != null) {
            builder.with(oldContext);
        }
        Context completeContext = builder.build();

        this.setContextMap(completeContext);
        return completeContext;
    }

    public void resetContext(Map<String, String> oldContext) {
        this.setContextMap(oldContext == null ? new LinkedHashMap<>() : oldContext);
    }

    /**
     * Indicate to the {@link org.talend.logging.audit.AuditLogger} that a message useful for you backend.
     *
     * Remark: default value is {@code true} (backward compatibility)
     *
     * @return - {@code true} indicate to the {@link org.talend.logging.audit.AuditLogger} that this {@link AbstractBackend} use
     * log
     * message to produce a trace.
     * - {@code false} indicate to the {@link org.talend.logging.audit.AuditLogger} that this {@link AbstractBackend} doesn't use
     * message to produce a trace and so
     * that is not necessary to compute one.
     */
    protected boolean enableMessageFormat() {
        return true;
    }

}
