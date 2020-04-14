package org.talend.daikon.spring.audit.logs.service;

import org.talend.logging.audit.AuditEvent;
import org.talend.logging.audit.EventAuditLogger;

public interface AuditLogger extends EventAuditLogger {

    @AuditEvent(category = "audit log")
    void sendAuditLog(Object... args);
}
