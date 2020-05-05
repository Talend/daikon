package org.talend.daikon.spring.audit.logs.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuditLogGeneratorInterceptor extends HandlerInterceptorAdapter {

    private final AuditLogSender auditLogSender;

    public AuditLogGeneratorInterceptor(AuditLogSender auditLogSender) {
        this.auditLogSender = auditLogSender;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    private boolean generatesAuditLog(Object handler) {
        return true;
    }
}
