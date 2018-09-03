package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class ContextEnricher {

    private final AuditConfigurationMap config;

    public ContextEnricher(AuditConfigurationMap config) {
        this.config = config;
    }

    public Map<String, String> enrich(String category, Map<String, String> logData) {
        Map<String, String> answer = new LinkedHashMap<>(logData);

        answer.put(EventFields.MDC_ID, UUID.randomUUID().toString());
        answer.put(EventFields.MDC_CATEGORY, category);
        answer.put(EventFields.MDC_AUDIT, "true");
        answer.put(EventFields.MDC_APPLICATION, AuditConfiguration.APPLICATION_NAME.getString(config));
        answer.put(EventFields.MDC_SERVICE, AuditConfiguration.SERVICE_NAME.getString(config));
        answer.put(EventFields.MDC_INSTANCE, AuditConfiguration.INSTANCE_NAME.getString(config));

        return answer;
    }
}
