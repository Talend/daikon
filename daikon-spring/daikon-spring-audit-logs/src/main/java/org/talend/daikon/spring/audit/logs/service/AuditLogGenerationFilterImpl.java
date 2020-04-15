package org.talend.daikon.spring.audit.logs.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.spring.audit.logs.api.AuditContextFilter;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.talend.logging.audit.Context;

@Aspect
public class AuditLogGenerationFilterImpl implements AuditLogGenerationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogGenerationFilterImpl.class);

    private final ObjectMapper objectMapper;

    private final AuditUserProvider auditUserProvider;

    private final AuditLogger auditLogger;

    public AuditLogGenerationFilterImpl(ObjectMapper objectMapper, AuditUserProvider auditUserProvider, AuditLogger auditLogger) {
        this.objectMapper = objectMapper;
        this.auditUserProvider = auditUserProvider;
        this.auditLogger = auditLogger;
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
         * ---------------------
         * Determine Request info
         * ---------------------
         */

        // Retrieve @RequestBody annotation index (if used in original method)
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        AtomicReference<Integer> argumentIndex = new AtomicReference<>();
        AtomicInteger index = new AtomicInteger();
        Arrays.asList(parameterAnnotations).forEach(annotations -> {
            if (Arrays.stream(annotations)
                    .anyMatch(annotation -> annotation.annotationType().getName().equals(RequestBody.class.getName()))) {
                argumentIndex.set(index.intValue());
            }
            index.getAndIncrement();
        });
        // If @RequestBody arg annotation exists, retrieve the associated argument
        Object requestBody = null;
        if (argumentIndex.get() != null) {
            requestBody = proceedingJoinPoint.getArgs()[argumentIndex.get()];
        }

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

        // Run original method
        try {
            // Run original method and retrieve the result
            Object responseObject = proceedingJoinPoint.proceed();
            // This result will be used as Response body
            Object auditLogResponseObject = responseObject;
            if (responseObject instanceof ResponseEntity) {
                // In case of ResponseEntity, body and status code can be retrieved directly
                responseCode = ((ResponseEntity) responseObject).getStatusCode().value();
                auditLogResponseObject = ((ResponseEntity) responseObject).getBody();
            }
            // Finally send the audit log
            sendAuditLog(request, requestBody, responseCode, auditLogResponseObject, auditLogAnnotation);
            return responseObject;
        } catch (Throwable throwable) {
            // In case of exception, the audit log must be send anyway
            sendAuditLog(request, requestBody, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, auditLogAnnotation);
            throw throwable;
        }

    }

    /**
     * Build the context and send the audit log
     */
    private void sendAuditLog(HttpServletRequest request, Object requestBody, int responseCode, Object responseObject,
            GenerateAuditLog auditLogAnnotation) throws JsonProcessingException {
        // Build context from request, response & annotation info
        AuditLogContextBuilder auditLogContextBuilder = AuditLogContextBuilder.create()
                .withTimestamp(OffsetDateTime.now().toString()).withLogId(UUID.randomUUID()).withRequestId(UUID.randomUUID())
                .withApplicationId(auditLogAnnotation.application()).withEventType(auditLogAnnotation.eventType())
                .withEventCategory(auditLogAnnotation.eventCategory()).withEventOperation(auditLogAnnotation.eventOperation())
                .withUserId(auditUserProvider.getUserId()).withUsername(auditUserProvider.getUsername())
                .withEmail(auditUserProvider.getUserEmail()).withAccountId(auditUserProvider.getAccountId())
                .withRequest(request, requestBody).withResponse(responseCode,
                        (auditLogAnnotation.includeBodyResponse() && responseObject != null)
                                ? objectMapper.writeValueAsString(responseObject)
                                : null);

        try {
            // Filter the context if needed
            AuditContextFilter filter = auditLogAnnotation.filter().getDeclaredConstructor().newInstance();
            auditLogContextBuilder = filter.filter(auditLogContextBuilder, requestBody, responseObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // Finally send the log
        auditLogger.sendAuditLog(auditLogContextBuilder.build());

        LOGGER.info("audit log generated with metadata {}", auditLogAnnotation);
    }

    public void sendAuditLog(Context context) {
        auditLogger.sendAuditLog(context);
    }

    public AuditUserProvider getAuditUserProvider() {
        return auditUserProvider;
    }
}
