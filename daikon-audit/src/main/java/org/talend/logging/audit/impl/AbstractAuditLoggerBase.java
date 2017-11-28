package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

/**
 *
 */
public abstract class AbstractAuditLoggerBase implements AuditLoggerBase {

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

        final Map<String, String> oldContext = setNewContext(actualContext);
        try {
            final String formattedMessage = formatMessage(message);
            final Logger logger = getLogger(category);

            level.log(logger, formattedMessage, throwable);
        } finally {
            resetContext(oldContext);
        }
    }

    protected Logger getLogger(String category) {
        return LoggerFactory.getLogger(AuditConfiguration.ROOT_LOGGER.getString() + '.' + category);
    }

    private static Map<String, String> setNewContext(Context newContext) {
        Map<String, String> oldContext = MDC.getCopyOfContextMap();
        ContextBuilder builder = ContextBuilder.create();
        if (oldContext != null) {
            builder.with(oldContext);
        }
        Context completeContext = builder.with(newContext).build();

        MDC.setContextMap(completeContext);
        return oldContext;
    }

    private static void resetContext(Map<String, String> oldContext) {
        MDC.setContextMap(oldContext == null ? new LinkedHashMap<String, String>() : oldContext);
    }

    private static String formatMessage(String message) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext == null) {
            return message;
        }

        String formattedMessage = message;
        for (Map.Entry<String, String> entry : mdcContext.entrySet()) {
            formattedMessage = formattedMessage.replace('{' + entry.getKey() + '}', entry.getValue());
        }
        return formattedMessage;
    }
}
