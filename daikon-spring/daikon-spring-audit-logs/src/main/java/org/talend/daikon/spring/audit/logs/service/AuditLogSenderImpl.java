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

public class AuditLogSenderImpl implements AuditLogSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogSenderImpl.class);

    private final AuditUserProvider auditUserProvider;

    private final AuditLogger auditLogger;

    private final AuditLogIpExtractor auditLogIpExtractor;

    public AuditLogSenderImpl(AuditUserProvider auditUserProvider, AuditLogger auditLogger,
            AuditLogIpExtractor auditLogIpExtractor) {
        this.auditUserProvider = auditUserProvider;
        this.auditLogger = auditLogger;
        this.auditLogIpExtractor = auditLogIpExtractor;
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
                    .withResponse(responseCode, auditLogAnnotation.includeBodyResponse() ? responseObject : null)
                    .withIpExtractor(this.auditLogIpExtractor);

            // Filter the context if needed
            AuditContextFilter filter = auditLogAnnotation.filter().getDeclaredConstructor().newInstance();
            auditLogContextBuilder = filter.filter(auditLogContextBuilder, requestBody, responseObject);

            // Finally send the log
            this.sendAuditLog(auditLogContextBuilder.build());
            LOGGER.info("audit log generated with metadata {}", auditLogAnnotation);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("audit log with metadata {} has not been generated", auditLogAnnotation, e);
        } catch (AuditLogException e) {
            LOGGER.debug("audit log with metadata {} has not been generated", auditLogAnnotation, e);
        }
    }

    /**
     * Build a context from a context builder and send the generated context
     */
    public void sendAuditLog(AuditLogContextBuilder builder) {
        auditLogger.sendAuditLog(builder.withIpExtractor(this.auditLogIpExtractor).build());
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
