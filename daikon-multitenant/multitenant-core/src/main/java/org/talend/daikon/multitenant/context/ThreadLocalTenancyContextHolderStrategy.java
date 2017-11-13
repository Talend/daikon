package org.talend.daikon.multitenant.context;

/**
 * A <code>ThreadLocal</code>-based implementation of {@link TenancyContextHolderStrategy}.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 * @see ThreadLocal
 */
public class ThreadLocalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static final ThreadLocal<TenancyContext> CONTEXT_HOLDER = new ThreadLocal<TenancyContext>();

    public void clearContext() {
        CONTEXT_HOLDER.set(null);
    }

    public TenancyContext getContext() {
        TenancyContext ctx = CONTEXT_HOLDER.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }

        return ctx;
    }

    public void setContext(TenancyContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Only non-null SecurityContext instances are permitted");
        }
        CONTEXT_HOLDER.set(context);
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
