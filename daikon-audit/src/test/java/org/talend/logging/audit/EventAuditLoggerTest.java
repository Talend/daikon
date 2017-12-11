package org.talend.logging.audit;

import org.junit.Test;
import org.slf4j.MDC;

/**
 *
 */
public class EventAuditLoggerTest {

    @Test
    public void testEventAuditLogger() {

        StandardEventAuditLogger auditLogger = AuditLoggerFactory.getEventAuditLogger(StandardEventAuditLogger.class);

        MDC.put("userId", "someUser");
        MDC.put("tenantId", "someAccount");

        auditLogger.loginSuccess();

        auditLogger.passwordChanged();
    }
}
