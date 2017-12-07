package org.talend.logging.audit.impl;

import java.util.List;

/**
 *
 */
final class Utils {

    private Utils() {
    }

    static <T> T getSpecificClassParam(List<Object> args, Class<T> clz) {
        for (Object obj : args) {
            if (clz.isInstance(obj)) {
                return clz.cast(obj);
            }
        }
        return null;
    }

    static <T> T getSpecificClassParam(Object[] args, Class<T> clz) {
        for (int i = 0; i < args.length; i++) {
            if (clz.isInstance(args[i])) {
                return clz.cast(args[i]);
            }
        }
        return null;
    }

    static String getCategoryFromLoggerName(String loggerName) {
        final String loggerPrefix = AuditConfiguration.ROOT_LOGGER.getString() + '.';
        if (!loggerName.startsWith(loggerPrefix)) {
            return null;
        }
        return loggerName.substring(loggerPrefix.length());
    }
}
