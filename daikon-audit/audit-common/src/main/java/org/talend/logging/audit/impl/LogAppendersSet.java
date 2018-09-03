package org.talend.logging.audit.impl;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.talend.logging.audit.LogAppenders;

/**
 *
 */
public class LogAppendersSet extends LinkedHashSet<LogAppenders> {

    LogAppendersSet() {
        super();
    }

    LogAppendersSet(Collection<LogAppenders> appenders) {
        super(appenders);
    }
}
