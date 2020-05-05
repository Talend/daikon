package org.talend.daikon.spring.audit.logs.service;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

@Aspect
public class AuditLogGeneratorAspect {

    private final AuditLogSender auditLogSender;

    public AuditLogGeneratorAspect(AuditLogSender auditLogSender) {
        this.auditLogSender = auditLogSender;
    }

    /**
     * This aspect will be ran around all method with the @GenerateAuditLog annotation
     */
    @Around("@annotation(org.talend.daikon.spring.audit.logs.api.GenerateAuditLog)")
    public Object auditLogGeneration(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // Retrieve @GenerateAuditLog annotation
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        GenerateAuditLog auditLogAnnotation = method.getAnnotation(GenerateAuditLog.class);
        ResponseStatus responseStatusAnnotation = method.getAnnotation(ResponseStatus.class);

        // Retrieve HTTP request & response
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        /**
         * ----------------------
         * Determine Response info
         * ----------------------
         */

        // Response code is deducted from HttpServletResponse if possible
        // Otherwise let's use a default value (0)
        int responseCode = response != null ? response.getStatus() : 0;
        if (responseStatusAnnotation != null) {
            responseCode = responseStatusAnnotation.value().value();
        }
        // Run original method and retrieve the result
        Object responseObject = proceedingJoinPoint.proceed();
        // This result will be used as Response body
        Object auditLogResponseObject = responseObject;
        if (responseObject instanceof ResponseEntity) {
            // In case of ResponseEntity, body and status code can be retrieved directly
            responseCode = ((ResponseEntity) responseObject).getStatusCode().value();
            auditLogResponseObject = ((ResponseEntity) responseObject).getBody();
        }

        if (responseCode == 0 || HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
            // Finally send the audit log
            auditLogSender.sendAuditLog(request, responseCode, auditLogResponseObject, auditLogAnnotation);
        }
        return responseObject;
    }
}
