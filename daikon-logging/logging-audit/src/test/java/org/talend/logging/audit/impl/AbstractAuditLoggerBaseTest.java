package org.talend.logging.audit.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.talend.logging.audit.AuditLoggingException;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

public class AbstractAuditLoggerBaseTest {

    @Test
    public void testLog() {
        String category = "testCat";
        Context ctx = ContextBuilder.emptyContext();
        Throwable thr = new AuditLoggingException("TestMsg");

        Logger logger = mock(Logger.class);
        logger.info(thr.getMessage(), thr);
        replay(logger);

        TestAuditLoggerBaseTest base = new TestAuditLoggerBaseTest(logger, category.toLowerCase());

        base.log(LogLevel.INFO, category, ctx, thr, null);

        verify(logger);
    }

    private static class TestAuditLoggerBaseTest extends AbstractAuditLoggerBase {

        private final Logger logger;

        private final String expectedCategory;

        private TestAuditLoggerBaseTest(Logger logger, String expectedCategory) {
            this.logger = logger;
            this.expectedCategory = expectedCategory;
        }

        @Override
        protected Logger getLogger(String category) {
            assertEquals(expectedCategory, category);
            return logger;
        }
    }
}
