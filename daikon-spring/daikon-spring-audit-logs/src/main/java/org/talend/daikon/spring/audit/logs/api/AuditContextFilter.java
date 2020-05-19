package org.talend.daikon.spring.audit.logs.api;

import org.talend.daikon.spring.audit.logs.service.AuditLogContextBuilder;

/**
 * Audit log context filter interface
 */
public interface AuditContextFilter {

    /**
     * Filter the audit log context before sending it
     * 
     * @param auditLogContextBuilder audit log context builder
     * @param requestBody HTTP request body to filter
     * @param responseObject HTTP response body to filter
     * @return the filtered audit log context
     */
    AuditLogContextBuilder filter(AuditLogContextBuilder auditLogContextBuilder, Object requestBody, Object responseObject);

}
