package org.talend.logging.audit.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogEnricherTest {

    private static final List<AuditConfigEntry> AUDIT_ENTRIES = new ArrayList<>();

    @BeforeClass
    public static void setupSuite() {
        AUDIT_ENTRIES.clear();
        for (AuditConfiguration c : AuditConfiguration.values()) {
            AuditConfigEntry e = new AuditConfigEntry(c, c.getValue(), c.getAlreadySet());
            AUDIT_ENTRIES.add(e);
        }
    }

    @AfterClass
    public static void cleanupSuite() {
        for (AuditConfigEntry e : AUDIT_ENTRIES) {
            e.entry.setAlreadySet(false);
            e.entry.setValue(e.value, e.entry.getClz());
            e.entry.setAlreadySet(e.alreadySet);
        }
    }

    @Before
    public void setupTest() {
        for (AuditConfiguration c : AuditConfiguration.values()) {
            c.setAlreadySet(false);
        }
    }

    @Test
    public void testEnrich() {
        String category = "cat";
        String application = "app";
        String service = "srv";
        String instance = "inst";

        AuditConfiguration.APPLICATION_NAME.setValue(application, String.class);
        AuditConfiguration.SERVICE_NAME.setValue(service, String.class);
        AuditConfiguration.INSTANCE_NAME.setValue(instance, String.class);

        Map<String, String> initialData = new LinkedHashMap<>();

        initialData.put("key", "value");

        Map<String, String> expectedData = new LinkedHashMap<>(initialData);

        expectedData.put("audit", "true");
        expectedData.put("application", application);
        expectedData.put("instance", instance);
        expectedData.put("service", service);
        expectedData.put("category", category);

        Map<String, String> enrichedData = new LogEnricher().enrich(category, initialData);

        assertEquals(expectedData, enrichedData);
    }
}
