package org.talend.logging.audit.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.logging.audit.Context;

/**
 *
 */
public class DefaultContextImpl extends LinkedHashMap<String, String> implements Context {

    public DefaultContextImpl() {
        super();
    }

    public DefaultContextImpl(Map<String, String> context) {
        super(context);
    }
}
