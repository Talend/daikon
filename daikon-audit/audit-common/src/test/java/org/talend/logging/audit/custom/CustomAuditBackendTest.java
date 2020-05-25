package org.talend.logging.audit.custom;

import org.junit.Test;
import org.talend.logging.audit.AuditLogger;
import org.talend.logging.audit.AuditLoggerFactory;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.DefaultAuditLoggerBase;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomAuditBackendTest {

    @Test
    public void customAuditBackendWithoutBackendClassNameFails() {
        final Properties properties = newCustomBackendProperties();

        final AuditConfigurationMap auditConfigurationMap = AuditConfiguration.loadFromProperties(properties);

        try {
            new TestableAuditLoggerBase(auditConfigurationMap);
            fail("Custom backend instanciation should have failed because backend.custom.class property is missing");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().endsWith("no backend class name has been specified"));
        }
    }

    @Test
    public void customAuditBackendWithBackendClassNameNotInClasspath() {
        final String dummyBackendClass = "org.foo.bar.not.found.CustomAuditBackend";
        final Properties properties = newCustomBackendProperties();
        properties.setProperty("backend.class.name", dummyBackendClass);

        final AuditConfigurationMap auditConfigurationMap = AuditConfiguration.loadFromProperties(properties);

        try {
            new TestableAuditLoggerBase(auditConfigurationMap);
            fail("Custom backend instanciation should have failed because backend.custom.class property is not in the classpath");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().endsWith("is not available on classpath"));
            assertTrue(e.getMessage().contains(dummyBackendClass));
        }
    }

    @Test
    public void customAuditBackendInstanciation() {
        final String customAuditBackendClass = "org.talend.logging.audit.custom.CustomAuditBackend";

        final Properties properties = newCustomBackendProperties();
        properties.setProperty("backend.class.name", customAuditBackendClass);

        final AuditConfigurationMap auditConfigurationMap = AuditConfiguration.loadFromProperties(properties);
        final TestableAuditLoggerBase auditLoggerBase = new TestableAuditLoggerBase(auditConfigurationMap);

        assertTrue(auditLoggerBase.logger() instanceof CustomAuditBackend);

        CustomAuditBackend customAuditBackend = (CustomAuditBackend) auditLoggerBase.logger();

        final AuditLogger auditLogger = AuditLoggerFactory.getAuditLogger(AuditLogger.class, auditLoggerBase);

        auditLogger.info("hello", "world");

        assertEquals(customAuditBackend.getLoggedMessages().size(), 1);
        assertEquals(customAuditBackend.getLoggedMessages().get(0).getCategory(), "hello");
        assertEquals(customAuditBackend.getLoggedMessages().get(0).getMessage(), "world");
    }

    private Properties newCustomBackendProperties() {
        final Properties properties = new Properties();
        properties.setProperty("application.name", "custom-audit-backend-test");
        properties.setProperty("log.appender", "none");
        properties.setProperty("backend", "custom");
        return properties;
    }

    private static class TestableAuditLoggerBase extends DefaultAuditLoggerBase {

        public TestableAuditLoggerBase(final AuditConfigurationMap externalConfig) {
            super(externalConfig);
        }

        public AbstractBackend logger() {
            return getLogger();
        }
    }
}
