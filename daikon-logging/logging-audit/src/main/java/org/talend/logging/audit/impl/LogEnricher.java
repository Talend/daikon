package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class LogEnricher {

    public Map<? extends Object, ? extends Object> enrich(String category, Map<?, ?> logData) {
        Map<Object, Object> answer = new LinkedHashMap<>(logData);

        answer.put(EventFields.AUDIT, "true");
        answer.put(EventFields.APPLICATION, AuditConfiguration.APPLICATION_NAME.getString());
        answer.put(EventFields.INSTANCE, AuditConfiguration.INSTANCE_NAME.getString());
        answer.put(EventFields.CATEGORY, category);

        return answer;
    }
}
