package org.talend.logging.audit.impl;

import java.io.IOException;

import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.rewrite.RewriteAppender;
import org.apache.log4j.varia.DenyAllFilter;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;
import org.talend.logging.audit.AuditLoggingException;

/**
 *
 */
final class Log4j1Configurer {

    private Log4j1Configurer() {
    }

    static void configure() {
        final LogAppenders appender = AuditConfiguration.LOG_APPENDER.getValue(LogAppenders.class);

        final RewriteAppender auditAppender = new RewriteAppender();
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

        case NONE:
            auditAppender.addFilter(new DenyAllFilter());
            break;

        default:
            throw new IllegalArgumentException("Unknown appender " + appender);
        }

        auditAppender.setRewritePolicy(new Log4j1EnricherPolicy());

        final Logger logger = Logger.getLogger(AuditConfiguration.ROOT_LOGGER.getString());

        logger.addAppender(auditAppender);
        logger.setAdditivity(false);
    }

    private static Appender rollingFileAppender() {
        final RollingFileAppender appender;

        try {
            appender = new RollingFileAppender(logstashLayout(), AuditConfiguration.APPENDER_FILE_PATH.getString(), true);
        } catch (IOException e) {
            throw new AuditLoggingException(e);
        }

        appender.setName("auditFileAppender");
        appender.setMaxBackupIndex(AuditConfiguration.APPENDER_FILE_MAXBACKUP.getInteger());
        appender.setMaximumFileSize(AuditConfiguration.APPENDER_FILE_MAXSIZE.getLong());
        appender.setEncoding("UTF-8");
        appender.setImmediateFlush(true);
        appender.setLayout(logstashLayout());

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

        appender.setName("consoleAppender");

        return appender;
    }

    private static Layout logstashLayout() {
        return new Log4jJSONLayout(AuditConfiguration.LOCATION.getBoolean());
    }
}
