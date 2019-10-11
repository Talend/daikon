package org.talend.logging.audit.log4j2;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;
import org.talend.logging.audit.AuditLoggingException;
import org.talend.logging.audit.LogAppenders;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.EventFields;
import org.talend.logging.audit.impl.LogAppendersSet;
import org.talend.logging.audit.impl.LogTarget;
import org.talend.logging.audit.impl.PropagateExceptions;

/**
 *
 */
public final class Log4j2Configurer {

    private Log4j2Configurer() {
    }

    public static void configure(AuditConfigurationMap config) {
        final LogAppendersSet appendersSet = AuditConfiguration.LOG_APPENDER.getValue(config, LogAppendersSet.class);

        if (appendersSet == null || appendersSet.isEmpty()) {
            throw new AuditLoggingException("No audit appenders configured.");
        }

        if (appendersSet.size() > 1 && appendersSet.contains(LogAppenders.NONE)) {
            throw new AuditLoggingException("Invalid configuration: none appender is used with other simultaneously.");
        }

        String root_logger_name = AuditConfiguration.ROOT_LOGGER.getString(config);
        final Logger logger = LogManager.getLogger(root_logger_name);

        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig(root_logger_name);
        loggerConfig.setAdditive(false);

        for (LogAppenders appender : appendersSet) {
            switch (appender) {
            case FILE:
                Appender fa = rollingFileAppender(config);
                configuration.addAppender(fa);
                loggerConfig.addAppender(fa, logger.getLevel(), null);
                configuration.addLogger(root_logger_name, loggerConfig);
                loggerContext.updateLoggers();
                break;

            case SOCKET:
                Appender sa = socketAppender(config);
                configuration.addAppender(sa);
                loggerConfig.addAppender(sa, logger.getLevel(), null);
                configuration.addLogger(root_logger_name, loggerConfig);
                loggerContext.updateLoggers();
                break;

            case CONSOLE:
                Appender ca = consoleAppender(config);
                configuration.addAppender(ca);
                loggerConfig.addAppender(ca, logger.getLevel(), null);
                configuration.addLogger(root_logger_name, loggerConfig);
                loggerContext.updateLoggers();
                break;

            case HTTP:
                Appender ha = httpAppender(config);
                configuration.addAppender(ha);
                loggerConfig.addAppender(ha, logger.getLevel(), null);
                configuration.addLogger(root_logger_name, loggerConfig);
                loggerContext.updateLoggers();
                break;

            case NONE:
                loggerConfig.setLevel(Level.OFF);
                loggerContext.updateLoggers();
                break;

            default:
                throw new AuditLoggingException("Unknown appender " + appender);
            }
        }
    }

    private static Appender rollingFileAppender(AuditConfigurationMap config) {
        DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
                .withMax("" + AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger(config)).build();
        SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy
                .createPolicy("" + AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong(config));

        String logfilepath = AuditConfiguration.APPENDER_FILE_PATH.getString(config);
        String parent_dir = "";
        int index = logfilepath.lastIndexOf('/');
        if (index > 0) {
            parent_dir = logfilepath.substring(0, index + 1);
        }

        String archived_log_file_pattern = parent_dir + "$${date:yyyy-MM}/audit-%d{yyyy-MM-dd}-%i.log.gz";

        Appender appender = RollingFileAppender.newBuilder().setName("auditFileAppender").withStrategy(strategy)
                .withPolicy(policy).withImmediateFlush(true).withAppend(true).withBufferedIo(false).withBufferSize(8 * 1024)
                .withFileName(logfilepath).withFilePattern(archived_log_file_pattern).setLayout(logstashLayout(config)).build();

        appender.start();
        return appender;
    }

    private static Appender socketAppender(AuditConfigurationMap config) {
        Appender appender = SocketAppender.newBuilder().setName("auditSocketAppender")
                .withHost(AuditConfiguration.APPENDER_SOCKET_HOST.getString(config))
                .withPort(AuditConfiguration.APPENDER_SOCKET_PORT.getInteger(config)).setLayout(logstashLayout(config)).build();
        appender.start();
        return appender;
    }

    private static Appender consoleAppender(AuditConfigurationMap config) {
        final LogTarget target = AuditConfiguration.APPENDER_CONSOLE_TARGET.getValue(config, LogTarget.class);

        Target tg = Target.SYSTEM_OUT;
        if (target == LogTarget.ERROR) {
            tg = Target.SYSTEM_ERR;
        }
        Appender appender = ConsoleAppender.newBuilder().setName("auditConsoleAppender").setTarget(tg).setLayout(
                PatternLayout.newBuilder().withPattern(AuditConfiguration.APPENDER_CONSOLE_PATTERN.getString(config)).build())
                .build();
        appender.start();
        return appender;
    }

    private static Appender httpAppender(AuditConfigurationMap config) {
        boolean ignoreExceptions = false;
        switch (AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(config, PropagateExceptions.class)) {
        case ALL:
            ignoreExceptions = false;
            break;

        case NONE:
            ignoreExceptions = true;
            break;

        default:
            throw new AuditLoggingException("Unknown propagate exception value: "
                    + AuditConfiguration.PROPAGATE_APPENDER_EXCEPTIONS.getValue(config, PropagateExceptions.class));
        }
        final Log4j2HttpAppender appender = new Log4j2HttpAppender("auditHttpAppender", null, logstashLayout(config),
                ignoreExceptions, null);
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

        appender.start();

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

        Charset charset = Charset.forName(AuditConfiguration.ENCODING.getString(config));

        Log4j2JSONLayout layout = new Log4j2JSONLayout(AuditConfiguration.LOCATION.getBoolean(config),
                AuditConfiguration.HOST.getBoolean(config), charset, new HashMap<>()) {

        };
        layout.setMetaFields(metaFields);

        return layout;
    }
}
