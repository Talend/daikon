package org.talend.logging.audit.impl;

import static org.easymock.EasyMock.*;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.talend.logging.audit.*;

public class ProxyAuditLoggerTest {

    @Test
    public void testAuditLogger() {
        String category = "testcat";
        String message = "testmsg";
        Context ctx = ContextBuilder.emptyContext();
        Throwable thr = new IllegalStateException();

        AuditLoggerBase base = mock(AuditLoggerBase.class);
        base.log(LogLevel.ERROR, category, ctx, thr, message);
        replay(base);

        AuditLogger auditLogger = getAuditLogger(base);

        auditLogger.error(category, ctx, thr, message);

        verify(base);
    }

    private static AuditLogger getAuditLogger(AuditLoggerBase loggerBase) {
        return (AuditLogger) Proxy.newProxyInstance(AuditLoggerFactory.class.getClassLoader(),
                new Class<?>[] { AuditLogger.class }, new ProxyAuditLogger(loggerBase));
    }
}
