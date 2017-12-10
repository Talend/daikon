package org.talend.logging.audit;

import org.junit.Test;

/**
 *
 */
public class EventAuditLoggerTest {

    @Test
    public void testEventAuditLogger() {

        StandardEventAuditLogger auditLogger = AuditLoggerFactory.getEventAuditLogger(StandardEventAuditLogger.class);

        auditLogger.loginSuccess();

        auditLogger.passwordChanged();
    }
}
