package org.talend.logging.audit.impl;

import static org.easymock.EasyMock.*;

import java.util.*;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;

public class Log4j1EnricherPolicyTest {

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
    public void testLog4j1Enricher() {
        AuditConfiguration.ROOT_LOGGER.setValue("logger", String.class);

        String category = "tcat";
        Map<String, String> logData = new LinkedHashMap<>();
        Map<Object, Object> answerData = new LinkedHashMap<>();
        answerData.put("test", "Test");

        LogEnricher enricher = mock(LogEnricher.class);
        expect(enricher.enrich(category, logData)).andReturn(answerData);

        LoggingEvent loggingEvent = mock(LoggingEvent.class);
        expect(loggingEvent.getLoggerName()).andReturn("logger." + category).anyTimes();
        expect(loggingEvent.getProperties()).andReturn(logData);
        expect(loggingEvent.getFQNOfLoggerClass()).andReturn(null);
        expect(loggingEvent.getLogger()).andReturn(null);
        expect(loggingEvent.getLevel()).andReturn(null);
        expect(loggingEvent.getMessage()).andReturn(null);
        expect(loggingEvent.getThreadName()).andReturn(null);
        expect(loggingEvent.getThrowableInformation()).andReturn(null);
        expect(loggingEvent.getNDC()).andReturn(null);

        replay(enricher, loggingEvent);

        Log4j1EnricherPolicy log4j1EnricherPolicy = new Log4j1EnricherPolicy();
        log4j1EnricherPolicy.setLogEnricher(enricher);

        LoggingEvent result = log4j1EnricherPolicy.rewrite(loggingEvent);

        Assert.assertEquals(answerData, result.getProperties());

        verify(enricher, loggingEvent);
    }
}
