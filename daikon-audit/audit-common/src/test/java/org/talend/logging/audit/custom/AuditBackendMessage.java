package org.talend.logging.audit.custom;

import org.talend.logging.audit.LogLevel;

import java.util.Map;

class AuditBackendMessage {

    private final String category;

    private final LogLevel logLevel;

    private final String message;

    private final Throwable throwable;

    private final Map<String, String> context;

    public AuditBackendMessage(final String category, final LogLevel logLevel, final String message, final Throwable throwable,
            final Map<String, String> context) {
        this.category = category;
        this.logLevel = logLevel;
        this.message = message;
        this.throwable = throwable;
        this.context = context;
    }

    public String getCategory() {
        return category;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Map<String, String> getContext() {
        return context;
    }
}
