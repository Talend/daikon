package org.talend.daikon.spring.audit.logs.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AuditLogGeneratorInterceptor extends HandlerInterceptorAdapter {

    private final AuditLogSender auditLogSender;

    public AuditLogGeneratorInterceptor(AuditLogSender auditLogSender) {
        this.auditLogSender = auditLogSender;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // Retrieve @GenerateAuditLog annotation from method if any
        Optional<GenerateAuditLog> generateAuditLog = Optional.of(handler).filter(h -> h instanceof HandlerMethod)
                .map(HandlerMethod.class::cast).map(HandlerMethod::getMethod).map(m -> m.getAnnotation(GenerateAuditLog.class));
        if (!generateAuditLog.isPresent()) {
            super.afterCompletion(request, response, handler, ex);
        } else {
            int responseCode = response.getStatus();
            // In some cases AccessDeniedException or AuthenticationException are thrown
            // while response code is 200
            if (ex != null & ex instanceof AccessDeniedException) {
                responseCode = HttpStatus.FORBIDDEN.value();
            }
            if (ex != null & ex instanceof AuthenticationException) {
                responseCode = HttpStatus.UNAUTHORIZED.value();
            }
            // Read request content from cached http request
            String requestBody = Optional.ofNullable(request).filter(r -> r instanceof ContentCachingRequestWrapper)
                    .map(ContentCachingRequestWrapper.class::cast).map(this::extractContent).orElse(null);
            // Only log if code is not successful
            if (!HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
                this.auditLogSender.sendAuditLog(request, requestBody, responseCode, null, generateAuditLog.get());
            }
        }
    }

    private String extractContent(ContentCachingRequestWrapper requestWrapper) {
        String content;
        try {
            content = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            content = "";
        }
        return content.isEmpty() ? null : content;
    }
}
