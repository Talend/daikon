package org.talend.logging.audit.impl;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *
 */
public class LogAppendersSet extends LinkedHashSet<LogAppenders> {

    public LogAppendersSet() {
        super();
    }

    public LogAppendersSet(Collection<LogAppenders> appenders) {
        super(appenders);
    }
}
