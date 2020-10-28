package org.talend.logging.audit.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.MockType.STRICT;
import static org.talend.logging.audit.LogLevel.INFO;

import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SimpleAuditLoggerBaseTest {

    private SimpleAuditLoggerBase simpleAuditLoggerBase;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock(STRICT)
    public AbstractBackend backend;

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

    @Test
    public void testContextIsRestored() {
        simpleAuditLoggerBase = new SimpleAuditLoggerBase(backend);
        Map<String, String> currentContext = Collections.singletonMap("k", "v");

        expect(backend.getCopyOfContextMap()).andReturn(currentContext);
        expect(backend.setNewContext(anyObject())).andReturn(null);
        backend.log(anyObject(), anyObject(), anyObject(), anyObject());
        backend.resetContext(currentContext);

        replay(backend);
        simpleAuditLoggerBase.log(INFO, "category", null, null, "msg");
        verify(backend);
    }
}
