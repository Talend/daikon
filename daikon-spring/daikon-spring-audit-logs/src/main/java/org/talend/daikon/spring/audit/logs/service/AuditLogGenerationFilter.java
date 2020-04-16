package org.talend.daikon.spring.audit.logs.service;

import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.logging.audit.Context;

public interface AuditLogGenerationFilter {

    void sendAuditLog(Context context);

    AuditUserProvider getAuditUserProvider();
}
