package org.talend.logging.audit.impl;

import static org.easymock.EasyMock.*;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.talend.logging.audit.*;

public class ProxyEventAuditLoggerTest {

    @Test
    public void testEventAuditLogger() {
        Context ctx = ContextBuilder.emptyContext();
        Throwable thr = new IllegalStateException();

        AuditLoggerBase base = mock(AuditLoggerBase.class);
        base.log(LogLevel.WARNING, "testcat", ctx, thr, "testmsg");
        base.log(LogLevel.INFO, "testcat2", null, null, "testmsg2");
        replay(base);

        TestEvent testEvent = getEventAuditLogger(base);

        testEvent.testWithParams(thr, ctx);
        testEvent.testWithoutParams();

        verify(base);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyArguments() {
        Context ctx = ContextBuilder.emptyContext();
        Throwable thr = new IllegalStateException();
        TestEvent testEvent = getEventAuditLogger(null);
        testEvent.testWithParams(ctx, thr, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRepeatedContext() {
        Context ctx = ContextBuilder.emptyContext();
        TestEvent testEvent = getEventAuditLogger(null);
        testEvent.testWithParams(ctx, ctx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRepeatedThrowable() {
        Throwable thr = new IllegalStateException();
        TestEvent testEvent = getEventAuditLogger(null);
        testEvent.testWithParams(thr, thr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingAnnotation() {
        TestEvent testEvent = getEventAuditLogger(null);
        testEvent.notEvent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongArgumentType() {
        TestEvent testEvent = getEventAuditLogger(null);
        testEvent.testWithParams("");
    }

    private static TestEvent getEventAuditLogger(AuditLoggerBase loggerBase) {
        return (TestEvent) Proxy.newProxyInstance(AuditLoggerFactory.class.getClassLoader(), new Class<?>[] { TestEvent.class },
                new ProxyEventAuditLogger(loggerBase));
    }

    private interface TestEvent {

        @AuditEvent(category = "testcat", message = "testmsg", level = LogLevel.WARNING)
        void testWithParams(Object... params);

        @AuditEvent(category = "testcat2", message = "testmsg2", level = LogLevel.INFO)
        void testWithoutParams();

        void notEvent();
    }
}
