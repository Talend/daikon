package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class LogEnricher {

    @SuppressWarnings({ "unchecked" })
    public <K, V> Map<K, V> enrich(String category, Map<?, ?> logData) {
        Map<Object, Object> answer = new LinkedHashMap<>(logData);

        answer.put(EventFields.AUDIT, "true");
        answer.put(EventFields.APPLICATION, AuditConfiguration.APPLICATION_NAME.getString());
        answer.put(EventFields.SERVICE, AuditConfiguration.SERVICE_NAME.getString());
        answer.put(EventFields.INSTANCE, AuditConfiguration.INSTANCE_NAME.getString());
        answer.put(EventFields.CATEGORY, category);

        return (Map<K, V>) answer;
    }
}
