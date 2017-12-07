package org.talend.logging.audit.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

public class AuditConfigurationTest {

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
    public void testLoadConfiguration() {
        System.setProperty("test.file.path.property", "/tmp/testSysPropValue");
        final String javaHome = System.getenv("JAVA_HOME");

        AuditConfiguration.loadFromClasspath("/test.audit.properties");

        for (AuditConfiguration c : AuditConfiguration.values()) {
            if (!c.getAlreadySet()) {
                throw new IllegalStateException("Value for configuration option '" + c.getProperty() + "' is not set.");
            }
        }

        final LogAppendersSet expectedAppenders = new LogAppendersSet(Arrays.asList(LogAppenders.FILE, LogAppenders.SOCKET));

        assertEquals("testLogger", AuditConfiguration.ROOT_LOGGER.getString());
        assertEquals("TestApplicationName", AuditConfiguration.APPLICATION_NAME.getString());
        assertEquals("DefaultServiceName", AuditConfiguration.SERVICE_NAME.getString());
        assertEquals(javaHome, AuditConfiguration.INSTANCE_NAME.getString());
        assertEquals("/tmp/testSysPropValue/test.json", AuditConfiguration.APPENDER_FILE_PATH.getString());
        assertEquals((Long) 30L, AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong());
        assertEquals((Integer) 100, AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger());
        assertEquals(Boolean.TRUE, AuditConfiguration.LOCATION.getBoolean());
        assertEquals("serverHost", AuditConfiguration.APPENDER_SOCKET_HOST.getString());
        assertEquals((Integer) 8056, AuditConfiguration.APPENDER_SOCKET_PORT.getInteger());
        assertEquals("ConsolePattern", AuditConfiguration.APPENDER_CONSOLE_PATTERN.getString());
        assertEquals(LogTarget.ERROR, AuditConfiguration.APPENDER_CONSOLE_TARGET.getValue(LogTarget.class));
        assertEquals(expectedAppenders, AuditConfiguration.LOG_APPENDER.getValue(LogAppendersSet.class));
    }
}
