package org.talend.daikon.spring.audit.logs.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuditLogGeneratorInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogGeneratorInterceptor.class);

    private final AuditLogSender auditLogSender;

    public AuditLogGeneratorInterceptor(AuditLogSender auditLogSender) {
        this.auditLogSender = auditLogSender;
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
            // Read request & response content from cached http request & response
            String requestBody = Optional.ofNullable(request).map(this::extractContent).orElse(null);
            String responseBody = Optional.ofNullable(response).map(this::extractContent).orElse(null);
            // Only log if code is not successful
            if (!HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
                this.auditLogSender.sendAuditLog(request, requestBody, responseCode, responseBody, generateAuditLog.get());
            }
        }
    }

    private String extractContent(Object wrapper) {
        byte[] rawContent = null;
        String stringContent = "";
        if (wrapper instanceof ContentCachingRequestWrapper) {
            rawContent = ((ContentCachingRequestWrapper) wrapper).getContentAsByteArray();
        } else if (wrapper instanceof ContentCachingResponseWrapper) {
            rawContent = ((ContentCachingResponseWrapper) wrapper).getContentAsByteArray();
        }
        if (rawContent != null) {
            try {
                stringContent = IOUtils.toString(rawContent, StandardCharsets.UTF_8.toString());
                if (wrapper instanceof ContentCachingResponseWrapper) {
                    ((ContentCachingResponseWrapper) wrapper).copyBodyToResponse();
                }
            } catch (IOException e) {
                LOGGER.warn("Wrapper content can't be read", e);
            }
        }
        return stringContent.isEmpty() ? null : stringContent;
    }
}
