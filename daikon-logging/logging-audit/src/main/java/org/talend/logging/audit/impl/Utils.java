package org.talend.logging.audit.impl;

import java.lang.reflect.Method;
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

    static <T> T getSpecificClassParam(Method method, Object[] args, Class<T> clz) {
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (clz.equals(paramTypes[i])) {
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
