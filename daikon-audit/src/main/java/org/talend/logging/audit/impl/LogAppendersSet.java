package org.talend.logging.audit.impl;

import org.talend.logging.audit.LogAppenders;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *
 */
class LogAppendersSet extends LinkedHashSet<LogAppenders> {

    LogAppendersSet() {
        super();
    }

    LogAppendersSet(Collection<LogAppenders> appenders) {
        super(appenders);
    }
}
