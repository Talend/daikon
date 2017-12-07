package org.talend.logging.audit.impl;

import org.apache.log4j.ConsoleAppender;

/**
 *
 */
public enum LogTarget {
    OUTPUT(ConsoleAppender.SYSTEM_OUT),
    ERROR(ConsoleAppender.SYSTEM_ERR);

    private final String target;

    LogTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
}
