package org.talend.daikon.multitenant.context;

/**
 * Allow a {@link Runnable} to executed in the tenancy context available when {@link #wrap(Runnable)} is called.
 * <p>
 * <code>
 * <br/>
 * &nbsp;// Tenancy context contains "tenant1"<br/>
 * &nbsp;Runnable runnable = () -> {<br/>
 * &nbsp;&nbsp;// Tenancy context contains "tenant1"<br/>
 * &nbsp;};<br/>
 * &nbsp;Thread thread = new Thread(MultiTenantRunnable.wrap(runnable));
 * <br/>
 * </code>
 * </p>
 * Without wrap call, tenancy contexts would be:
 * <p>
 * <code>
 * <br/>
 * &nbsp;// Tenancy context contains "tenant1"<br/>
 * &nbsp;Runnable runnable = () -> {<br/>
 * &nbsp;&nbsp;// Tenancy context contains nothing<br/>
 * &nbsp;};<br/>
 * &nbsp;Thread thread = new Thread(runnable);
 * <br/>
 * </code>
 * </p>
 */
public class MultiTenantRunnable implements Runnable {

    private final Runnable wrapped;

    private final TenancyContext context;

    private MultiTenantRunnable(Runnable r) {
        wrapped = r;
        context = TenancyContextHolder.getContext();
    }

    /**
     * Wraps a {@link Runnable} so the {@link Runnable#run()} gets executed using the {@link TenancyContext} available
     * when this method is called.
     *
     * @param r The {@link Runnable} to be wrapped.
     * @return A {@link Runnable} to run in available tenancy context.
     */
    public static Runnable wrap(Runnable r) {
        return new MultiTenantRunnable(r);
    }

    @Override
    public void run() {
        final TenancyContext previousContext = TenancyContextHolder.getContext();
        try {
            TenancyContextHolder.setContext(context);
            wrapped.run();
        } finally {
            TenancyContextHolder.setContext(previousContext);
        }
    }
}
