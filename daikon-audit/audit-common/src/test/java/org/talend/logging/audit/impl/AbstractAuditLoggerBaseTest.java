package org.talend.logging.audit.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.logging.audit.AuditLoggingException;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class AbstractAuditLoggerBaseTest {

    private static final AuditConfigurationMap CONFIG = new AuditConfigurationMapImpl();

    @BeforeClass
    public static void setupSuite() {
        AuditConfiguration.APPLICATION_NAME.setValue(CONFIG, "app", String.class);
        AuditConfiguration.SERVICE_NAME.setValue(CONFIG, "svc", String.class);
        AuditConfiguration.INSTANCE_NAME.setValue(CONFIG, "inst", String.class);
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void testLog() {
        String category = "testCat";
        Context ctx = ContextBuilder.emptyContext();
        Throwable thr = new AuditLoggingException("TestMsg");

        AbstractBackend logger = mock(AbstractBackend.class);
        // Init backend so that it has the messages
        expect(logger.enableMessageFormat()).andReturn(true);

        logger.log(category.toLowerCase(), LogLevel.INFO, thr.getMessage(), thr);
        expect(logger.getCopyOfContextMap()).andReturn(new LinkedHashMap<>());
        expect(logger.setNewContext(anyObject(Map.class), anyObject(Map.class))).andReturn(ctx);
        logger.resetContext(anyObject(Map.class));
        expectLastCall();
        replay(logger);

        TestAuditLoggerBaseTest base = new TestAuditLoggerBaseTest(logger);

        base.log(LogLevel.INFO, category, ctx, thr, null);

        verify(logger);
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void testNoMessageNeededByTheBackend() {
        // Given
        String category = "testCat";
        Context ctx = ContextBuilder.emptyContext();

        AbstractBackend logger = mock(AbstractBackend.class);
        // Init backend so that it hasn't the messages
        expect(logger.enableMessageFormat()).andReturn(false);

        logger.log(category.toLowerCase(), LogLevel.INFO, null);
        expect(logger.getCopyOfContextMap()).andReturn(new LinkedHashMap<>());
        expect(logger.setNewContext(anyObject(Map.class), anyObject(Map.class))).andReturn(ctx);
        logger.resetContext(anyObject(Map.class));
        expectLastCall();
        replay(logger);

        TestAuditLoggerBaseTest base = new TestAuditLoggerBaseTest(logger);

        // When
        base.log(LogLevel.INFO, category, ctx, null, "A discarded message");

        // Then
        verify(logger);
    }

    private static class TestAuditLoggerBaseTest extends AbstractAuditLoggerBase {

        private final AbstractBackend logger;

        private final ContextEnricher enricher = new ContextEnricher(CONFIG);

        private TestAuditLoggerBaseTest(AbstractBackend logger) {
            this.logger = logger;
        }

        @Override
        protected AbstractBackend getLogger() {
            return logger;
        }

        @Override
        protected ContextEnricher getEnricher() {
            return enricher;
        }
    }
}
