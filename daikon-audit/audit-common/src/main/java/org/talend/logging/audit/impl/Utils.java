package org.talend.logging.audit.impl;

import java.util.List;

/**
 *
 */
public final class Utils {

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
        for (Object obj : args) {
            if (clz.isInstance(obj)) {
                return clz.cast(obj);
            }
        }
        return null;
    }

    static boolean isSlf4jPresent() {
        return isClassPresent("org.slf4j.Logger");
    }

    static boolean isLog4j1Present() {
        return isClassPresent("org.apache.log4j.Logger");
    }

    static boolean isLog4j2Present() {
        return isClassPresent("org.apache.logging.log4j.Logger");
    }

    static boolean isLogbackPresent() {
        return isClassPresent("ch.qos.logback.classic.LoggerContext");
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
