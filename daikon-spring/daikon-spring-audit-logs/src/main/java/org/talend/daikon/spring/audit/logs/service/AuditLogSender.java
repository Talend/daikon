package org.talend.daikon.spring.audit.logs.service;

import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;
import org.talend.logging.audit.Context;

import javax.servlet.http.HttpServletRequest;

/**
 * Audit log sender interface
 */
public interface AuditLogSender {

    /**
     * Send the audit log from a context given as parameter
     *
     * @param context audit log context
     */
    void sendAuditLog(Context context);

    /**
     * Build a context from a context builder and send the generated context
     *
     * @param builder audit log context builder
     */
    void sendAuditLog(AuditLogContextBuilder builder);

    /**
     * Build a context from given parameters and send the corresponding audit log
     *
     * @param request HTTP request
     * @param requestBody request body if different than HTTP request content
     * @param responseCode response code
     * @param responseObject response body object
     * @param auditLogAnnotation generate audit log annotation containing basic properties
     */
    void sendAuditLog(HttpServletRequest request, Object requestBody, int responseCode, Object responseObject,
            GenerateAuditLog auditLogAnnotation);

    /**
     * Audit user provider
     *
     * @return the audit user provider containing information about current user
     */
    AuditUserProvider getAuditUserProvider();
}
