package org.talend.daikon.spring.audit.logs.service;

import static org.talend.daikon.spring.audit.logs.api.AuditLogScope.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

@Component
public class AuditLogGeneratorInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogGeneratorInterceptor.class);

    private final AuditLogSender auditLogSender;

    private final ObjectMapper objectMapper;

    public AuditLogGeneratorInterceptor(AuditLogSender auditLogSender, ObjectMapper objectMapper) {
        this.auditLogSender = auditLogSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // Retrieve @GenerateAuditLog annotation from method if any
        Optional<GenerateAuditLog> generateAuditLog = Optional.of(handler).filter(h -> h instanceof HandlerMethod)
                .map(HandlerMethod.class::cast).map(HandlerMethod::getMethod).map(m -> m.getAnnotation(GenerateAuditLog.class));
        if (generateAuditLog.isPresent() && generateAuditLog.get().scope().in(ALL, ERROR)) {
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
            String requestBodyString = Optional.ofNullable(request).map(this::extractContent).orElse(null);
            Object requestBody = Optional.of(handler).map(HandlerMethod.class::cast).map(HandlerMethod::getMethod)
                    .map(this::extractRequestType).map(t -> this.parse(requestBodyString, t)).orElse(null);
            String responseBodyString = Optional.ofNullable(response).map(this::extractContent).orElse(null);
            // Only log if code is not successful
            if (HttpStatus.valueOf(responseCode).isError()) {
                this.auditLogSender.sendAuditLog(request, requestBody, responseCode, responseBodyString, generateAuditLog.get());
            }
        } else {
            super.afterCompletion(request, response, handler, ex);
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
            } catch (IOException e) {
                LOGGER.warn("Wrapper content can't be read", e);
            }
        }
        return stringContent.isEmpty() ? null : stringContent;
    }

    private Class extractRequestType(Method method) {
        return Arrays.stream(method.getParameters()).filter(p -> p.getAnnotation(RequestBody.class) != null).findFirst()
                .map(Parameter::getType).orElse(null);
    }

    private Object parse(String str, Class type) {
        if (str != null && type != null && !type.isAssignableFrom(str.getClass())) {
            try {
                return objectMapper.readValue(str, type);
            } catch (Exception e) {
                return str;
            }
        }
        return str;
    }
}
