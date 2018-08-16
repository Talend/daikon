package org.talend.logging.audit.impl;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class ContextEnricherTest {

    @Test
    public void testEnrich() {
        String category = "cat";
        String application = "app";
        String service = "srv";
        String instance = "inst";

        AuditConfigurationMap config = new AuditConfigurationMapImpl();

        AuditConfiguration.APPLICATION_NAME.setValue(config, application, String.class);
        AuditConfiguration.SERVICE_NAME.setValue(config, service, String.class);
        AuditConfiguration.INSTANCE_NAME.setValue(config, instance, String.class);

        Map<String, String> initialData = new LinkedHashMap<>();

        initialData.put("key", "value");

        Map<String, String> expectedData = new LinkedHashMap<>(initialData);

        expectedData.put("talend.meta.audit", "true");
        expectedData.put("talend.meta.application", application);
        expectedData.put("talend.meta.instance", instance);
        expectedData.put("talend.meta.service", service);
        expectedData.put("talend.meta.category", category);

        Map<String, String> enrichedData = new ContextEnricher(config).enrich(category, initialData);

        String id = enrichedData.remove("talend.meta.eventid");
        assertNotNull(id);

        assertEquals(expectedData, enrichedData);
    }
}
