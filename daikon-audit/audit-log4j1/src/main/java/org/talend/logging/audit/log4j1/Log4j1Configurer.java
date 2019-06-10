package org.talend.logging.audit.log4j1;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SocketAppender;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;
import org.talend.logging.audit.AuditLoggingException;
import org.talend.logging.audit.LogAppenders;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.EventFields;
import org.talend.logging.audit.impl.LogAppendersSet;
import org.talend.logging.audit.impl.LogTarget;
import org.talend.logging.audit.impl.PropagateExceptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class Log4j1Configurer {

    private Log4j1Configurer() {
    }

    public static void configure(AuditConfigurationMap config) {
        final LogAppendersSet appendersSet = AuditConfiguration.LOG_APPENDER.getValue(config, LogAppendersSet.class);

        if (appendersSet == null || appendersSet.isEmpty()) {
            throw new AuditLoggingException("No audit appenders configured.");
        }

        if (appendersSet.size() > 1 && appendersSet.contains(LogAppenders.NONE)) {
            throw new AuditLoggingException("Invalid configuration: none appender is used with other simultaneously.");
        }

        final Logger logger = Logger.getLogger(AuditConfiguration.ROOT_LOGGER.getString(config));

        logger.setAdditivity(false);

        for (LogAppenders appender : appendersSet) {
            switch (appender) {
            case FILE:
                logger.addAppender(rollingFileAppender(config));
                break;

            case SOCKET:
                logger.addAppender(socketAppender(config));
                break;

            case CONSOLE:
                logger.addAppender(consoleAppender(config));
                break;

            case HTTP:
                logger.addAppender(httpAppender(config));
                break;

            case NONE:
                logger.setLevel(Level.OFF);
                break;

            default:
                throw new AuditLoggingException("Unknown appender " + appender);
            }
        }
    }

    private static Appender rollingFileAppender(AuditConfigurationMap config) {
        final RollingFileAppender appender = new RollingFileAppender();

        appender.setName("auditFileAppender");
        appender.setMaxBackupIndex(AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger(config));
        appender.setMaximumFileSize(AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong(config));
        appender.setEncoding(AuditConfiguration.ENCODING.getString(config));
        appender.setImmediateFlush(true);
        appender.setLayout(logstashLayout(config));

        try {
            appender.setFile(AuditConfiguration.APPENDER_FILE_PATH.getString(config), true, false, 8 * 1024);
        } catch (IOException e) {
            throw new AuditLoggingException(e);
        }

        return appender;
    }

    private static Appender socketAppender(AuditConfigurationMap config) {
        final SocketAppender appender = new SocketAppender(AuditConfiguration.APPENDER_SOCKET_HOST.getString(config),
                AuditConfiguration.APPENDER_SOCKET_PORT.getInteger(config));

        appender.setName("auditSocketAppender");
        appender.setLocationInfo(AuditConfiguration.LOCATION.getBoolean(config));

        return appender;
    }

    private static Appender consoleAppender(AuditConfigurationMap config) {
        final LogTarget target = AuditConfiguration.APPENDER_CONSOLE_TARGET.getValue(config, LogTarget.class);

        final ConsoleAppender appender = new ConsoleAppender();

        appender.setName("auditConsoleAppender");
        appender.setEncoding(AuditConfiguration.ENCODING.getString(config));
        appender.setTarget(target.getTarget());
        appender.setLayout(new PatternLayout(AuditConfiguration.APPENDER_CONSOLE_PATTERN.getString(config)));

        appender.activateOptions();

        return appender;
    }

    private static Appender httpAppender(AuditConfigurationMap config) {
        final Log4j1HttpAppender appender = new Log4j1HttpAppender();

        appender.setName("auditHttpAppender");
        appender.setLayout(logstashLayout(config));
        appender.setUrl(AuditConfiguration.APPENDER_HTTP_URL.getString(config));
        if (!AuditConfiguration.APPENDER_HTTP_USERNAME.getString(config).trim().isEmpty()) {
            appender.setUsername(AuditConfiguration.APPENDER_HTTP_USERNAME.getString(config));
        }
        if (!AuditConfiguration.APPENDER_HTTP_PASSWORD.getString(config).trim().isEmpty()) {
            appender.setPassword(AuditConfiguration.APPENDER_HTTP_PASSWORD.getString(config));
        }
        appender.setAsync(AuditConfiguration.APPENDER_HTTP_ASYNC.getBoolean(config));

        appender.setConnectTimeout(AuditConfiguration.APPENDER_HTTP_CONNECT_TIMEOUT.getInteger(config));
        appender.setReadTimeout(AuditConfiguration.APPENDER_HTTP_READ_TIMEOUT.getInteger(config));
        appender.setEncoding(AuditConfiguration.ENCODING.getString(config));

        switch (AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(config, PropagateExceptions.class)) {
        case ALL:
            appender.setPropagateExceptions(true);
            break;

        case NONE:
            appender.setPropagateExceptions(false);
            break;

        default:
            throw new AuditLoggingException("Unknown propagate exception value: "
                    + AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(config, PropagateExceptions.class));
        }

        return appender;
    }

    private static Layout logstashLayout(AuditConfigurationMap config) {
        Map<String, String> metaFields = new HashMap<>();
        metaFields.put(EventFields.MDC_ID, EventFields.ID);
        metaFields.put(EventFields.MDC_CATEGORY, EventFields.CATEGORY);
        metaFields.put(EventFields.MDC_AUDIT, EventFields.AUDIT);
        metaFields.put(EventFields.MDC_APPLICATION, EventFields.APPLICATION);
        metaFields.put(EventFields.MDC_SERVICE, EventFields.SERVICE);
        metaFields.put(EventFields.MDC_INSTANCE, EventFields.INSTANCE);

        Log4jJSONLayout layout = new Log4jJSONLayout();

        layout.setLocationInfo(AuditConfiguration.LOCATION.getBoolean(config));
        layout.setHostInfo(AuditConfiguration.HOST.getBoolean(config));
        layout.setMetaFields(metaFields);

        return layout;
    }
}
