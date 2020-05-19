package org.talend.daikon.spring.audit.logs.service;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.spring.audit.logs.api.AuditContextFilter;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;
import org.talend.daikon.spring.audit.logs.exception.AuditLogException;
import org.talend.logging.audit.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuditLogSenderImpl implements AuditLogSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogSenderImpl.class);

    private final ObjectMapper objectMapper;

    private final AuditUserProvider auditUserProvider;

    private final AuditLogger auditLogger;

    public AuditLogSenderImpl(ObjectMapper objectMapper, AuditUserProvider auditUserProvider, AuditLogger auditLogger) {
        this.objectMapper = objectMapper;
        this.auditUserProvider = auditUserProvider;
        this.auditLogger = auditLogger;
    }

    /**
     * Build the context and send the audit log
     */
    @Override
    public void sendAuditLog(HttpServletRequest request, Object requestBody, int responseCode, Object responseObject,
            GenerateAuditLog auditLogAnnotation) {
        try {
            // Build context from request, response & annotation info
            AuditLogContextBuilder auditLogContextBuilder = AuditLogContextBuilder.create() //
                    .withTimestamp(OffsetDateTime.now().toString()) //
                    .withLogId(UUID.randomUUID()) //
                    .withRequestId(UUID.randomUUID()) //
                    .withApplicationId(auditLogAnnotation.application()) //
                    .withEventType(auditLogAnnotation.eventType()) //
                    .withEventCategory(auditLogAnnotation.eventCategory()) //
                    .withEventOperation(auditLogAnnotation.eventOperation()) //
                    .withUserId(auditUserProvider.getUserId()) //
                    .withUsername(auditUserProvider.getUsername()) //
                    .withEmail(auditUserProvider.getUserEmail()) //
                    .withAccountId(auditUserProvider.getAccountId()) //
                    .withRequest(request, requestBody) //
                    .withResponse(responseCode, auditLogAnnotation.includeBodyResponse() ? responseObject : null);

            // Filter the context if needed
            AuditContextFilter filter = auditLogAnnotation.filter().getDeclaredConstructor().newInstance();
            auditLogContextBuilder = filter.filter(auditLogContextBuilder, requestBody, responseObject);

            // Finally send the log
            this.sendAuditLog(auditLogContextBuilder.build());
            LOGGER.info("audit log generated with metadata {}", auditLogAnnotation);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | AuditLogException e) {
            LOGGER.error("audit log with metadata {} has not been generated", auditLogAnnotation, e);
        }
    }

    /**
     * Send the audit log
     */
    @Override
    public void sendAuditLog(Context context) {
        auditLogger.sendAuditLog(context);
    }

    @Override
    public AuditUserProvider getAuditUserProvider() {
        return this.auditUserProvider;
    }
}
