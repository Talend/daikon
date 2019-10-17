package org.talend.logging.audit.log4j2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Test;
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

public class Log4j2ConfigurerTest {

    private static final String HTTP_APPENDER = "auditHttpAppender";

    private static final String FILE_APPENDER = "auditFileAppender";

    private static final String CONSOLE_APPENDER = "auditConsoleAppender";

    @Test
    public void testConfigurer() {
        final AuditConfigurationMap config = AuditConfiguration.loadFromClasspath("/configurer.audit.properties");

        Log4j2Configurer.configure(config);

        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig("testLogger");
        Map<String, Appender> appenders = loggerConfig.getAppenders();

        validateHttpAppender((Log4j2HttpAppender) appenders.get(HTTP_APPENDER));
        validateFileAppender((RollingFileAppender) appenders.get(FILE_APPENDER));
        validateConsoleAppender((ConsoleAppender) appenders.get(CONSOLE_APPENDER));
    }

    private static void validateConsoleAppender(ConsoleAppender appender) {
        assertNotNull(appender);

        assertEquals(Target.SYSTEM_ERR, appender.getTarget());
        assertEquals("ConsolePattern", ((PatternLayout) appender.getLayout()).getConversionPattern());
    }

    private static void validateFileAppender(RollingFileAppender appender) {
        assertNotNull(appender);

        assertEquals("/tmp/test.json", appender.getFileName());
        assertEquals(100, ((DefaultRolloverStrategy) appender.getManager().getRolloverStrategy()).getMaxIndex());
        assertEquals(30L, ((SizeBasedTriggeringPolicy) appender.getTriggeringPolicy()).getMaxFileSize());
        assertTrue(appender.getLayout() instanceof Log4j2JSONLayout);
    }

    private static void validateHttpAppender(Log4j2HttpAppender appender) {
        assertNotNull(appender);

        assertEquals("http://localhost:8080/", appender.getUrl());
        assertEquals("httpuser", appender.getUsername());
        assertEquals(1000, appender.getConnectTimeout());
        assertEquals(50, appender.getReadTimeout());
        assertEquals(false, appender.isAsync());
        assertEquals(false, appender.ignoreExceptions());
        assertEquals("UTF-16", appender.getEncoding());

        assertTrue(appender.getLayout() instanceof Log4j2JSONLayout);
    }
}
