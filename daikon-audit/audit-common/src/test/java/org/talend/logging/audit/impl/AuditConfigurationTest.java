package org.talend.logging.audit.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.*;
import org.talend.logging.audit.LogAppenders;

public class AuditConfigurationTest {

    @Test
    public void testLoadConfiguration() {
        System.setProperty("test.file.path.property", "/tmp/testSysPropValue");
        final String pathEnv = System.getenv("PATH");

        AuditConfigurationMap config = AuditConfiguration.loadFromClasspath("/test.audit.properties");

        for (AuditConfiguration c : AuditConfiguration.values()) {
            if (!config.containsKey(c) && !c.canBeNull()) {
                throw new IllegalStateException("Value for configuration option '" + c.toString() + "' is not set.");
            }
        }

        final LogAppendersSet expectedAppenders = new LogAppendersSet(Arrays.asList(LogAppenders.FILE, LogAppenders.SOCKET));

        assertEquals("testLogger", AuditConfiguration.ROOT_LOGGER.getString(config));

        assertEquals(Boolean.TRUE, AuditConfiguration.LOCATION.getBoolean(config));
        assertEquals(Boolean.TRUE, AuditConfiguration.HOST.getBoolean(config));
        assertEquals(PropagateExceptions.ALL,
                AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(config, PropagateExceptions.class));

        assertEquals("TestApplicationName", AuditConfiguration.APPLICATION_NAME.getString(config));
        assertEquals("DefaultServiceName", AuditConfiguration.SERVICE_NAME.getString(config));
        assertEquals(pathEnv, AuditConfiguration.INSTANCE_NAME.getString(config));

        assertEquals(expectedAppenders, AuditConfiguration.LOG_APPENDER.getValue(config, LogAppendersSet.class));

        assertEquals("/tmp/testSysPropValue/test.json", AuditConfiguration.APPENDER_FILE_PATH.getString(config));
        assertEquals((Long) 30L, AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong(config));
        assertEquals((Integer) 100, AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger(config));

        assertEquals("serverHost", AuditConfiguration.APPENDER_SOCKET_HOST.getString(config));
        assertEquals((Integer) 8056, AuditConfiguration.APPENDER_SOCKET_PORT.getInteger(config));

        assertEquals("ConsolePattern", AuditConfiguration.APPENDER_CONSOLE_PATTERN.getString(config));
        assertEquals(LogTarget.ERROR, AuditConfiguration.APPENDER_CONSOLE_TARGET.getValue(config, LogTarget.class));

        assertEquals("http://localhost:8080/", AuditConfiguration.APPENDER_HTTP_URL.getString(config));
        assertEquals("httpuser", AuditConfiguration.APPENDER_HTTP_USERNAME.getString(config));
        assertEquals("httppass", AuditConfiguration.APPENDER_HTTP_PASSWORD.getString(config));
        assertEquals(Boolean.FALSE, AuditConfiguration.APPENDER_HTTP_ASYNC.getBoolean(config));
        assertEquals((Integer) 1000, AuditConfiguration.APPENDER_HTTP_CONNECT_TIMEOUT.getInteger(config));
        assertEquals((Integer) 50, AuditConfiguration.APPENDER_HTTP_READ_TIMEOUT.getInteger(config));

        assertEquals("UTF-16", AuditConfiguration.ENCODING.getString(config));
        assertEquals(Backends.LOGBACK, AuditConfiguration.BACKEND.getValue(config, Backends.class));
    }
}
