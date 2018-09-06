package org.talend.logging.audit.log4j1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.junit.Test;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

public class Log4j1ConfigurerTest {

    private static final String HTTP_APPENDER = "auditHttpAppender";

    private static final String FILE_APPENDER = "auditFileAppender";

    private static final String CONSOLE_APPENDER = "auditConsoleAppender";

    @Test
    public void testConfigurer() {
        final AuditConfigurationMap config = AuditConfiguration.loadFromClasspath("/configurer.audit.properties");

        Log4j1Configurer.configure(config);

        final Logger logger = Logger.getLogger("testLogger");

        validateHttpAppender((Log4j1HttpAppender) logger.getAppender(HTTP_APPENDER));
        validateFileAppender((RollingFileAppender) logger.getAppender(FILE_APPENDER));
        validateConsoleAppender((ConsoleAppender) logger.getAppender(CONSOLE_APPENDER));
    }

    private static void validateConsoleAppender(ConsoleAppender appender) {
        assertNotNull(appender);

        assertEquals("System.err", appender.getTarget());
        assertEquals("ConsolePattern", ((PatternLayout) appender.getLayout()).getConversionPattern());
    }

    private static void validateFileAppender(RollingFileAppender appender) {
        assertNotNull(appender);

        assertEquals("/tmp/test.json", appender.getFile());
        assertEquals(100, appender.getMaxBackupIndex());
        assertEquals(30L, appender.getMaximumFileSize());
        assertTrue(appender.getLayout() instanceof Log4jJSONLayout);
    }

    private static void validateHttpAppender(Log4j1HttpAppender appender) {
        assertNotNull(appender);

        assertEquals("http://localhost:8080/", appender.getUrl());
        assertEquals("httpuser", appender.getUsername());
        assertEquals(1000, appender.getConnectTimeout());
        assertEquals(50, appender.getReadTimeout());
        assertEquals(false, appender.isAsync());
        assertEquals(true, appender.isPropagateExceptions());
        assertEquals("UTF-16", appender.getEncoding());

        assertTrue(appender.getLayout() instanceof Log4jJSONLayout);
    }
}
