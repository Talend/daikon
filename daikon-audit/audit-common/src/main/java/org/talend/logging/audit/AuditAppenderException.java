package org.talend.logging.audit;

/**
 *
 */
public class AuditAppenderException extends AuditLoggingException {

    private final LogAppenders appender;

    public AuditAppenderException(LogAppenders appender, String message) {
        super(message);
        this.appender = appender;
    }

    public AuditAppenderException(LogAppenders appender, Throwable cause) {
        super(cause);
        this.appender = appender;
    }

    public LogAppenders getAppender() {
        return appender;
    }
}
