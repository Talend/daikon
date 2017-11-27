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

        category = category.trim().toLowerCase();
        if (category.isEmpty()) {
            throw new IllegalArgumentException("category cannot be empty or blank");
        }

        if (message == null && throwable != null) {
            message = throwable.getMessage();
        }

        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        // creating copy of passed context to be able to modify it
        context = context == null ? ContextBuilder.emptyContext() : ContextBuilder.create(context).build();

        final Map<String, String> oldContext = setNewContext(context);
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
        String formattedMessage = message;
        for (Map.Entry<String, String> entry : MDC.getCopyOfContextMap().entrySet()) {
            formattedMessage = formattedMessage.replace('{' + entry.getKey() + '}', entry.getValue());
        }
        return formattedMessage;
    }
}
