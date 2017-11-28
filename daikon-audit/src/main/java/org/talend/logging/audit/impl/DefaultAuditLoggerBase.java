package org.talend.logging.audit.impl;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

/**
 *
 */
public class DefaultAuditLoggerBase extends AbstractAuditLoggerBase {

    private static final String SYSPROP_CONFIG_FILE = "talend.logging.audit.config";

    static {
        final String confPath = System.getProperty(SYSPROP_CONFIG_FILE);
        if (confPath != null) {
            AuditConfiguration.loadFromFile(confPath);
        } else {
            AuditConfiguration.loadFromClasspath("/audit.properties");
        }
    }

    public DefaultAuditLoggerBase() {
        if (LoggerFactory.getILoggerFactory() instanceof Log4jLoggerFactory) {
            Log4j1Configurer.configure();
        } else {
            throw new IllegalArgumentException("Only log4j 1.x is currently supported");
        }
    }
}
