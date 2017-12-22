package org.talend.daikon.multitenant.context;

/**
 * Exception thrown when there's no tenant set in the current tenancyContext.
 */
public class NoCurrentTenantException extends RuntimeException {

    public NoCurrentTenantException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCurrentTenantException(String message) {
        super(message);
    }
}
