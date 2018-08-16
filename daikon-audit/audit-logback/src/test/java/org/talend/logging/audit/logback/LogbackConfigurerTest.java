package org.talend.logging.audit.logback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.daikon.logging.event.layout.LogbackJSONLayout;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

public class LogbackConfigurerTest {

    private static final String HTTP_APPENDER = "auditHttpAppender";

    private static final String FILE_APPENDER = "auditFileAppender";

    private static final String CONSOLE_APPENDER = "auditConsoleAppender";

    @Test
    public void testConfigurer() {
        final LoggerContext context = new LoggerContext();
        final AuditConfigurationMap config = AuditConfiguration.loadFromClasspath("/configurer.audit.properties");

        LogbackConfigurer.configure(config, context);

        final Logger logger = context.getLogger("testLogger");

        validateHttpAppender((LogbackHttpAppender) logger.getAppender(HTTP_APPENDER));
        validateFileAppender((RollingFileAppender<ILoggingEvent>) logger.getAppender(FILE_APPENDER));
        validateConsoleAppender((ConsoleAppender<ILoggingEvent>) logger.getAppender(CONSOLE_APPENDER));
    }

    private static void validateConsoleAppender(ConsoleAppender<ILoggingEvent> appender) {
        assertNotNull(appender);

        assertEquals("System.err", appender.getTarget());

        PatternLayout layout = getLayout(appender.getEncoder(), PatternLayout.class);

        assertEquals("ConsolePattern", layout.getPattern());
    }

    private static void validateFileAppender(RollingFileAppender<ILoggingEvent> appender) {
        assertNotNull(appender);

        final FlexibleWindowRollingPolicy rollingPolicy = (FlexibleWindowRollingPolicy) appender.getRollingPolicy();

        assertEquals("/tmp/test.json", appender.getFile());
        assertEquals(100, rollingPolicy.getMaxIndex());
        assertTrue(appender.getTriggeringPolicy() instanceof SizeBasedTriggeringPolicy);
        assertNotNull(getLayout(appender.getEncoder(), LogbackJSONLayout.class));
    }

    private static void validateHttpAppender(LogbackHttpAppender appender) {
        assertNotNull(appender);

        assertEquals("http://localhost:8080/", appender.getUrl());
        assertEquals("httpuser", appender.getUsername());
        assertEquals(1000, appender.getConnectTimeout());
        assertEquals(50, appender.getReadTimeout());
        assertEquals(false, appender.isAsync());
        assertEquals(true, appender.isPropagateExceptions());
        assertEquals("UTF-16", appender.getEncoding());

        assertTrue(appender.getLayout() instanceof LogbackJSONLayout);
    }

    private static <T extends Layout<ILoggingEvent>> T getLayout(Encoder<ILoggingEvent> encoder, Class<T> clz) {
        return clz.cast(((LayoutWrappingEncoder<ILoggingEvent>) encoder).getLayout());
    }
}
