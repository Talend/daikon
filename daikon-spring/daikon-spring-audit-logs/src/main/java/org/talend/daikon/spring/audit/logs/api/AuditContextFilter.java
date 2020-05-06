package org.talend.daikon.spring.audit.logs.api;

import org.talend.daikon.spring.audit.logs.service.AuditLogContextBuilder;

public interface AuditContextFilter {

    AuditLogContextBuilder filter(AuditLogContextBuilder auditLogContextBuilder, Object requestBody, Object responseObject);

}
