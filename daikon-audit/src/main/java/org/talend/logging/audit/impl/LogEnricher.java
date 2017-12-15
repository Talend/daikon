package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class LogEnricher {

    @SuppressWarnings({ "unchecked" })
    public <K, V> Map<K, V> enrich(String category, Map<?, ?> logData) {
        Map<Object, Object> answer = new LinkedHashMap<>(logData);

        answer.put(EventFields.MDC_ID, UUID.randomUUID().toString());
        answer.put(EventFields.MDC_CATEGORY, category);
        answer.put(EventFields.MDC_AUDIT, "true");
        answer.put(EventFields.MDC_APPLICATION, AuditConfiguration.APPLICATION_NAME.getString());
        answer.put(EventFields.MDC_SERVICE, AuditConfiguration.SERVICE_NAME.getString());
        answer.put(EventFields.MDC_INSTANCE, AuditConfiguration.INSTANCE_NAME.getString());

        return (Map<K, V>) answer;
    }
}
