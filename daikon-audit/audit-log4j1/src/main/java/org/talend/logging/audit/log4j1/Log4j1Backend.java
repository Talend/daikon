package org.talend.logging.audit.log4j1;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

import java.util.Map;

/**
 *
 */
public class Log4j1Backend extends AbstractBackend {

    public Log4j1Backend(AuditConfigurationMap config) {
        super(AuditConfiguration.ROOT_LOGGER.getString(config));

        Log4j1Configurer.configure(config);
    }

    public Log4j1Backend(String rootLogger) {
        super(rootLogger);
    }

    @Override
    public void log(String category, LogLevel level, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(loggerPrefix + category);

        switch (level) {
        case INFO:
            logger.info(message, throwable);
            break;

        case WARNING:
            logger.warn(message, throwable);
            break;

        case ERROR:
            logger.error(message, throwable);
            break;

        default:
            throw new IllegalArgumentException("Unsupported audit log level " + level);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getContext();
    }

    @Override
    public void setContextMap(Map<String, String> newContext) {
        MDC.clear();
        for (Map.Entry<String, String> e : newContext.entrySet()) {
            MDC.put(e.getKey(), e.getValue());
        }
    }
}
