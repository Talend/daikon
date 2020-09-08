package org.talend.logging.audit.impl;

import java.util.Map;

import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

/**
 *
 */
public abstract class AbstractAuditLoggerBase implements AuditLoggerBase {

    /**
     * <p>
     * A MDC key to use to customize whether audit logger should output message in audit events. This is interesting
     * in case only when MDC (Context) contains all the interesting information for the audit ingestion and no message
     * is required.
     * </p>
     * <p>
     * When set to "false", message in audit event will be replaced by an empty string. It defaults to <code>true</code>
     * for backward compatibility issues (and also because audit system are generally interested in message).
     * </p>
     */
    public static final String MDC_OUTPUT_MESSAGE = "__output-message__";

    private static String formatMessage(String message, Map<String, String> mdcContext) {
        if (mdcContext == null) {
            return message;
        }

        String formattedMessage = message;
        for (Map.Entry<String, String> entry : mdcContext.entrySet()) {
            formattedMessage = formattedMessage.replace('{' + entry.getKey() + '}', entry.getValue());
        }
        return formattedMessage;
    }

    public void log(LogLevel level, String category, Context context, Throwable throwable, String message) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }

        String categoryNormalized = category.trim().toLowerCase();
        if (categoryNormalized.isEmpty()) {
            throw new IllegalArgumentException("category cannot be empty or blank");
        }

        String actualMessage = message == null && throwable != null ? throwable.getMessage() : message;
        if (actualMessage == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        logInternal(level, categoryNormalized, context, throwable, actualMessage);
    }

    private void logInternal(LogLevel level, String category, Context context, Throwable throwable, String message) {
        // creating copy of passed context to be able to modify it
        Context actualContext = context == null ? ContextBuilder.emptyContext() : ContextBuilder.create(context).build();
        Map<String, String> enrichedContext = getEnricher().enrich(category, actualContext);

        final AbstractBackend logger = getLogger();
        final Map<String, String> oldContext = logger.getCopyOfContextMap();
        final Map<String, String> completeContext = logger.setNewContext(oldContext, enrichedContext);
        try {
            if (Boolean.parseBoolean(completeContext.getOrDefault(MDC_OUTPUT_MESSAGE, Boolean.TRUE.toString()))) {
                message = formatMessage(message, completeContext);
                logger.log(category, level, message, throwable);
            } else {
                logger.log(category, level, "", throwable);
            }
        } finally {
            logger.resetContext(oldContext);
        }
    }

    protected abstract AbstractBackend getLogger();

    protected abstract ContextEnricher getEnricher();
}
