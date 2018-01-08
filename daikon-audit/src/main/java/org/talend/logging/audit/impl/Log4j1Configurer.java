package org.talend.logging.audit.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.rewrite.RewriteAppender;
import org.apache.log4j.varia.DenyAllFilter;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;
import org.talend.logging.audit.AuditLoggingException;
import org.talend.logging.audit.LogAppenders;

/**
 *
 */
final class Log4j1Configurer {

    private Log4j1Configurer() {
    }

    static void configure() {
        final LogAppendersSet appendersSet = AuditConfiguration.LOG_APPENDER.getValue(LogAppendersSet.class);

        if (appendersSet == null || appendersSet.isEmpty()) {
            throw new AuditLoggingException("No audit appenders configured.");
        }

        if (appendersSet.size() > 1 && appendersSet.contains(LogAppenders.NONE)) {
            throw new AuditLoggingException("Invalid configuration: none appender is used with other simultaneously.");
        }

        final RewriteAppender auditAppender = new RewriteAppender();

        for (LogAppenders appender : appendersSet) {
            switch (appender) {
            case FILE:
                auditAppender.addAppender(rollingFileAppender());
                break;

            case SOCKET:
                auditAppender.addAppender(socketAppender());
                break;

            case CONSOLE:
                auditAppender.addAppender(consoleAppender());
                break;

            case HTTP:
                auditAppender.addAppender(httpAppender());
                break;

            case NONE:
                auditAppender.addFilter(new DenyAllFilter());
                break;

            default:
                throw new AuditLoggingException("Unknown appender " + appender);
            }
        }

        auditAppender.setRewritePolicy(new Log4j1EnricherPolicy());

        final Logger logger = Logger.getLogger(AuditConfiguration.ROOT_LOGGER.getString());

        logger.addAppender(auditAppender);
        logger.setAdditivity(false);
    }

    private static Appender rollingFileAppender() {
        final RollingFileAppender appender = new RollingFileAppender();

        appender.setName("auditFileAppender");
        appender.setMaxBackupIndex(AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger());
        appender.setMaximumFileSize(AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong());
        appender.setEncoding(AuditConfiguration.ENCODING.getString());
        appender.setImmediateFlush(true);
        appender.setLayout(logstashLayout());

        try {
            appender.setFile(AuditConfiguration.APPENDER_FILE_PATH.getString(), true, false, 8 * 1024);
        } catch (IOException e) {
            throw new AuditLoggingException(e);
        }

        return appender;
    }

    private static Appender socketAppender() {
        final SocketAppender appender = new SocketAppender(AuditConfiguration.APPENDER_SOCKET_HOST.getString(),
                AuditConfiguration.APPENDER_SOCKET_PORT.getInteger());

        appender.setName("auditSocketAppender");
        appender.setLocationInfo(AuditConfiguration.LOCATION.getBoolean());

        return appender;
    }

    private static Appender consoleAppender() {
        final LogTarget target = AuditConfiguration.APPENDER_CONSOLE_TARGET.getValue(LogTarget.class);

        final ConsoleAppender appender = new ConsoleAppender(
                new PatternLayout(AuditConfiguration.APPENDER_CONSOLE_PATTERN.getString()), target.getTarget());

        appender.setName("auditConsoleAppender");
        appender.setEncoding(AuditConfiguration.ENCODING.getString());

        return appender;
    }

    private static Appender httpAppender() {
        final Log4j1HttpAppender appender = new Log4j1HttpAppender();

        appender.setName("auditHttpAppender");
        appender.setLayout(logstashLayout());
        appender.setUrl(AuditConfiguration.APPENDER_HTTP_URL.getString());
        if (!AuditConfiguration.APPENDER_HTTP_USERNAME.getString().trim().isEmpty()) {
            appender.setUsername(AuditConfiguration.APPENDER_HTTP_USERNAME.getString());
        }
        if (!AuditConfiguration.APPENDER_HTTP_PASSWORD.getString().trim().isEmpty()) {
            appender.setPassword(AuditConfiguration.APPENDER_HTTP_PASSWORD.getString());
        }
        appender.setAsync(AuditConfiguration.APPENDER_HTTP_ASYNC.getBoolean());

        appender.setConnectTimeout(AuditConfiguration.APPENDER_HTTP_CONNECT_TIMEOUT.getInteger());
        appender.setReadTimeout(AuditConfiguration.APPENDER_HTTP_READ_TIMEOUT.getInteger());
        appender.setPropagateExceptions(AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(PropagateExceptions.class));

        return appender;
    }

    private static Layout logstashLayout() {
        Map<String, String> metaFields = new HashMap<>();
        metaFields.put(EventFields.MDC_ID, EventFields.ID);
        metaFields.put(EventFields.MDC_CATEGORY, EventFields.CATEGORY);
        metaFields.put(EventFields.MDC_AUDIT, EventFields.AUDIT);
        metaFields.put(EventFields.MDC_APPLICATION, EventFields.APPLICATION);
        metaFields.put(EventFields.MDC_SERVICE, EventFields.SERVICE);
        metaFields.put(EventFields.MDC_INSTANCE, EventFields.INSTANCE);
        //Optional common MDC apps attributes
        metaFields.put(EventFields.MDC_USER, EventFields.USER);
        metaFields.put(EventFields.MDC_OPERATION, EventFields.OPERATION);
        metaFields.put(EventFields.MDC_RESOURCE, EventFields.RESOURCE);
        metaFields.put(EventFields.MDC_RESULT, EventFields.RESULT);

        Log4jJSONLayout layout = new Log4jJSONLayout();

        layout.setLocationInfo(AuditConfiguration.LOCATION.getBoolean());
        layout.setMetaFields(metaFields);

        return layout;
    }
}
