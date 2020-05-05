package org.talend.daikon.spring.audit.logs.exception;

public class AuditLogException extends Exception {

    public AuditLogException(String message) {
        super(message);
    }

    public AuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
