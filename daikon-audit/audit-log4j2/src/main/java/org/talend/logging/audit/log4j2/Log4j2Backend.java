package org.talend.logging.audit.log4j2;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AbstractBackend;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;

/**
 *
 */
public class Log4j2Backend extends AbstractBackend {

    public Log4j2Backend(AuditConfigurationMap config) {
        super(AuditConfiguration.ROOT_LOGGER.getString(config));

        Log4j2Configurer.configure(config);
    }

    public Log4j2Backend(String rootLogger) {
        super(rootLogger);
    }

    @Override
    public void log(String category, LogLevel level, String message, Throwable throwable) {
        Logger logger = LogManager.getLogger(loggerPrefix + category);

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
        return ThreadContext.getContext();
    }

    @Override
    public void setContextMap(Map<String, String> newContext) {
        ThreadContext.clearMap();
        for (Map.Entry<String, String> e : newContext.entrySet()) {
            ThreadContext.put(e.getKey(), e.getValue());
        }
    }
}
