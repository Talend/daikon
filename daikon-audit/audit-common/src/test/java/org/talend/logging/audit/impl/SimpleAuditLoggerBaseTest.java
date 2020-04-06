package org.talend.logging.audit.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SimpleAuditLoggerBaseTest {

    private SimpleAuditLoggerBase simpleAuditLoggerBase;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testInitAutoNoKafkaBackendNotFound() {
        AuditConfigurationMap config = new AuditConfigurationMapImpl();
        config.setValue(AuditConfiguration.BACKEND, Backends.AUTO, Backends.class);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Unable to load backend org.talend.logging.audit.kafka.KafkaBackend");

        simpleAuditLoggerBase = new SimpleAuditLoggerBase(config);
    }

    @Test
    public void testInitKafkaBackendNotFound() {
        AuditConfigurationMap config = new AuditConfigurationMapImpl();
        config.setValue(AuditConfiguration.BACKEND, Backends.KAFKA, Backends.class);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Unable to load backend org.talend.logging.audit.kafka.KafkaBackend");

        simpleAuditLoggerBase = new SimpleAuditLoggerBase(config);
    }

    @Test
    public void testInitNotSupportedBackend() {
        AuditConfigurationMap config = new AuditConfigurationMapImpl();
        config.setValue(AuditConfiguration.BACKEND, Backends.LOGBACK, Backends.class);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unsupported backend LOGBACK");

        simpleAuditLoggerBase = new SimpleAuditLoggerBase(config);
    }
}