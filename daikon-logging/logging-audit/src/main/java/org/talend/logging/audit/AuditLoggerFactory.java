package org.talend.logging.audit;

import java.lang.reflect.Proxy;

import org.talend.logging.audit.impl.AuditLoggerBase;
import org.talend.logging.audit.impl.DefaultAuditLoggerBase;
import org.talend.logging.audit.impl.ProxyAuditLogger;
import org.talend.logging.audit.impl.ProxyEventAuditLogger;

/**
 * Factory for audit logging interfaces. It can create both simple and event-based API instances.
 */
public final class AuditLoggerFactory {

    private AuditLoggerFactory() {
    }

    /**
     * Creates default simple API instance.
     */
    public static AuditLogger getAuditLogger() {
        return getAuditLogger(AuditLogger.class);
    }

    /**
     * Creates customized simple API instance.
     *
     * @param clz interface which extends {@link AuditLogger} with additional methods.
     * @return instance of customized interface.
     * @see AuditLogger
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends AuditLogger> T getAuditLogger(Class<T> clz) {
        return (T) Proxy.newProxyInstance(AuditLoggerFactory.class.getClassLoader(), new Class<?>[] { clz },
                new ProxyAuditLogger(AuditLoggerBaseHolder.BASE_LOGGER));
    }

    /**
     * Creates event-based API instance from given interface.
     *
     * @param clz interface which extends {@link EventAuditLogger} with event declarinig methods.
     * @return instance of event-based API interface
     * @see EventAuditLogger
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends EventAuditLogger> T getEventAuditLogger(Class<T> clz) {
        return (T) Proxy.newProxyInstance(AuditLoggerFactory.class.getClassLoader(), new Class<?>[] { clz },
                new ProxyEventAuditLogger(AuditLoggerBaseHolder.BASE_LOGGER));
    }

    private static class AuditLoggerBaseHolder {

        static final AuditLoggerBase BASE_LOGGER = new DefaultAuditLoggerBase();
    }
}
