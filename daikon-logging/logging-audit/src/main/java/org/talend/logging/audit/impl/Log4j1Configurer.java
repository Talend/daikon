package org.talend.logging.audit.impl;

import java.io.IOException;

import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.rewrite.RewriteAppender;

/**
 *
 */
final class Log4j1Configurer {

    private Log4j1Configurer() {
    }

    static void configure() {
        final LogAppenders appender = AuditConfiguration.APPENDER.getValue(LogAppenders.class);

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
            appender = new RollingFileAppender(logstashLayout(), AuditConfiguration.FILE_PATH.getString(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        appender.setName("auditFileAppender");
        appender.setMaxBackupIndex(AuditConfiguration.FILE_MAX_BACKUP.getInteger());
        appender.setMaximumFileSize(AuditConfiguration.FILE_MAX_SIZE.getLong());
        appender.setEncoding("UTF-8");
        appender.setImmediateFlush(true);
        appender.setLayout(logstashLayout());

        return appender;
    }

    private static Appender socketAppender() {
        final SocketAppender appender = new SocketAppender(AuditConfiguration.SOCKET_HOST.getString(),
                AuditConfiguration.SOCKET_PORT.getInteger());

        appender.setName("auditSocketAppender");
        appender.setLocationInfo(AuditConfiguration.LOG_LOCATION.getBoolean());

        return appender;
    }

    private static Appender consoleAppender() {
        final LogTarget target = AuditConfiguration.CONSOLE_TARGET.getValue(LogTarget.class);

        final ConsoleAppender appender = new ConsoleAppender(new PatternLayout(AuditConfiguration.CONSOLE_PATTERN.getString()),
                target.getTarget());

        appender.setName("consoleAppender");

        return appender;
    }

    private static Layout logstashLayout() {
        final Log4j1AuditJSONLayout layout = new Log4j1AuditJSONLayout(AuditConfiguration.LOG_LOCATION.getBoolean());
        return layout;
    }
}
