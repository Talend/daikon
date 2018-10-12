package org.talend.logging.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.MDC;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.DefaultAuditLoggerBase;
import org.talend.logging.audit.logback.LogbackBackend;

/**
 *
 */
public class EventAuditLoggerTest {

    @Test
    public void testEventAuditLogger() {

        final AuditConfigurationMap config = AuditConfiguration.loadFromClasspath("/audit.properties");

        final LogbackBackend testBackend = new LogbackBackend(config);
        final DefaultAuditLoggerBase loggerBase = new DefaultAuditLoggerBase(testBackend, config);

        final StandardEventAuditLogger auditLogger = AuditLoggerFactory.getEventAuditLogger(StandardEventAuditLogger.class,
                loggerBase);

        final Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put("userId", "someUser");
        mdc.put("tenantId", "someAccount");
        populateMDC(mdc);

        auditLogger.loginSuccess();

        checkLogEntry(testBackend.getEntries(), "security", LogLevel.INFO, "User has logged in successfully.", null);
        assertEquals(mdc, testBackend.getCopyOfContextMap());

        final RuntimeException exception = new RuntimeException("Test");

        auditLogger.systemException(exception);

        checkLogEntry(testBackend.getEntries(), "failure", LogLevel.INFO, "Unexpected exception.", exception);
        assertEquals(mdc, testBackend.getCopyOfContextMap());
    }

    private static void populateMDC(Map<String, String> mdc) {
        for (Map.Entry<String, String> e : mdc.entrySet()) {
            MDC.put(e.getKey(), e.getValue());
        }
    }

    private static void checkLogEntry(List<LogbackBackend.LogEntry> entries, String category, LogLevel level, String message,
            Throwable throwable) {
        assertNotNull(entries);
        assertEquals(1, entries.size());

        final LogbackBackend.LogEntry entry = entries.get(0);

        assertEquals(category, entry.category);
        assertEquals(level, entry.level);
        assertEquals(message, entry.message);
        assertEquals(throwable, entry.throwable);
    }
}
