package org.talend.daikon.spring.audit.logs.api;

import org.talend.daikon.spring.audit.logs.service.AuditLogContextBuilder;

public class NoOpAuditContextFilter implements AuditContextFilter {

    @Override
    public AuditLogContextBuilder filter(AuditLogContextBuilder auditLogContextBuilder, Object requestBody,
            Object responseObject) {
        return auditLogContextBuilder;
    }
}
