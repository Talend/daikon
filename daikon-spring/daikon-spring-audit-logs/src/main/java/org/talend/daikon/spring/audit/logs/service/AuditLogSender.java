package org.talend.daikon.spring.audit.logs.service;

import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;
import org.talend.logging.audit.Context;

import javax.servlet.http.HttpServletRequest;

public interface AuditLogSender {

    void sendAuditLog(Context context);

    void sendAuditLog(HttpServletRequest request, Object requestBody, int responseCode, Object responseObject,
            GenerateAuditLog auditLogAnnotation);

    AuditUserProvider getAuditUserProvider();
}
