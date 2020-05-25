package org.talend.logging.audit.custom;

import org.slf4j.MDC;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfigurationMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This test backend is used to test the dynamic instantiation of audit backends
 * using the {@link org.talend.logging.audit.impl.Backends#CUSTOM} backend type.
 *
 * <pre>{@code
 * final Properties auditProperties = new Properties();
 *
 * // ... set other daikon-audit properties
 * auditProperties.setProperty("backend", "custom");
 * auditProperties.setProperty("backend.class.name", "org.talend.logging.audit.custom.CustomAuditBackend");
 *
 * // Dynamically instantiate custom backend
 * final AuditConfigurationMap auditConfigurationMap = AuditConfiguration.loadFromProperties(auditProperties);
 * }</pre>
 */
public class CustomAuditBackend extends AbstractBackend {

    private final List<AuditBackendMessage> loggedMessages = new ArrayList<>();

    private final AuditConfigurationMap config;

    public CustomAuditBackend(AuditConfigurationMap config) {
        super(null);
        this.config = config;
    }

    @Override
    public void log(final String category, final LogLevel level, final String message, final Throwable throwable) {
        loggedMessages.add(new AuditBackendMessage(category, level, message, throwable, getCopyOfContextMap()));
    }

    public List<AuditBackendMessage> getLoggedMessages() {
        return loggedMessages;
    }

    public AuditConfigurationMap getConfig() {
        return config;
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }

    @Override
    public void setContextMap(final Map<String, String> newContext) {
        MDC.setContextMap(newContext);
    }
}
