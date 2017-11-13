package org.talend.daikon.multitenant.context;

/**
 * Similar to GlobalSecurityContextHolderStrategy
 */
public class GlobalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static TenancyContext contextHolder;

    public void clearContext() {
        contextHolder = null;
    }

    public TenancyContext getContext() {
        if (contextHolder == null) {
            contextHolder = new DefaultTenancyContext();
        }

        return contextHolder;
    }

    public void setContext(TenancyContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Only non-null TenancyContext instances are permitted");
        }
        contextHolder = context;
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
