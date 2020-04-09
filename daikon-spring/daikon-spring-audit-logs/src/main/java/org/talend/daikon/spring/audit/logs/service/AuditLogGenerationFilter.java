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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.spring.audit.logs.api.AuditContextFilter;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
public class AuditLogGenerationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogGenerationFilter.class);

    private final ObjectMapper objectMapper;

    private final AuditUserProvider auditUserProvider;

    private final AuditLogger auditLogger;

    public AuditLogGenerationFilter(ObjectMapper objectMapper, AuditUserProvider auditUserProvider, AuditLogger auditLogger) {
        this.objectMapper = objectMapper;
        this.auditUserProvider = auditUserProvider;
        this.auditLogger = auditLogger;
    }

    @Around("@annotation(org.talend.daikon.spring.audit.logs.api.GenerateAuditLog)")
    public Object auditLogGeneration(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        GenerateAuditLog auditLogAnnotation = method.getAnnotation(GenerateAuditLog.class);

        // Retrieve HTTP request & response
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        Annotation[][] parameterAnnotations = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod()
                .getParameterAnnotations();
        AtomicReference<Integer> argumentIndex = new AtomicReference<>();
        AtomicInteger index = new AtomicInteger();
        Arrays.asList(parameterAnnotations).forEach(annotations -> {
            if (Arrays.stream(annotations)
                    .anyMatch(annotation -> annotation.annotationType().getName().equals(RequestBody.class.getName()))) {
                argumentIndex.set(index.intValue());
            }
            index.getAndIncrement();
        });
        Object requestBody = null;
        if (argumentIndex.get() != null) {
            requestBody = proceedingJoinPoint.getArgs()[argumentIndex.get()];
        }
        int responseCode = response != null ? response.getStatus() : 0;

        // Run original method
        try {
            Object responseObject = proceedingJoinPoint.proceed();
            Object auditLogResponseObject = responseObject;
            if (responseObject instanceof ResponseEntity) {
                responseCode = ((ResponseEntity) responseObject).getStatusCode().value();
                auditLogResponseObject = ((ResponseEntity) responseObject).getBody();
            }
            sendAuditLog(request, requestBody, responseCode, auditLogResponseObject, auditLogAnnotation);
            return responseObject;
        } catch (Throwable throwable) {
            sendAuditLog(request, requestBody, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, auditLogAnnotation);
            throw throwable;
        }

    }

    private void sendAuditLog(HttpServletRequest request, Object requestBody, int responseCode, Object responseObject,
            GenerateAuditLog auditLogAnnotation) throws JsonProcessingException {
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
            AuditContextFilter filter = auditLogAnnotation.filter().getDeclaredConstructor().newInstance();
            auditLogContextBuilder = filter.filter(auditLogContextBuilder, requestBody);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        auditLogger.sendAuditLog(auditLogContextBuilder.build());

        LOGGER.info("audit log generated with metadata {}", auditLogAnnotation);
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }
}
