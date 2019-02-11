package org.talend.daikon.logging.user;

/**
 * Tracks the atcivity Id for the current request.
 * 
 * @author sdiallo
 */
public final class RequestUserActivityContext {

    private static final ThreadLocal<RequestUserActivityContext> CONTEXT = new ThreadLocal<>();

    private String correlationId;

    public static RequestUserActivityContext getCurrent() {
        RequestUserActivityContext context = CONTEXT.get();
        if (context == null) {
            context = new RequestUserActivityContext();
            CONTEXT.set(context);
        }
        return context;
    }

    public static void clearCurrent() {
        CONTEXT.remove();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    private RequestUserActivityContext() {
        // not to be instantiated
    }
}