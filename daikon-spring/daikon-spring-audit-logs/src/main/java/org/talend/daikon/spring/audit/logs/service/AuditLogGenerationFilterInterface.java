package org.talend.daikon.spring.audit.logs.service;

import org.talend.logging.audit.Context;

public interface AuditLogGenerationFilterInterface {

    void sendAuditLog(Context context);
}
