package org.talend.logging.audit.impl;

/**
 *
 */
public class EventFields {

    private static final String PREFIX = "talend.meta.";

    public static final String ID = PREFIX + "id";

    public static final String AUDIT = PREFIX + "audit";

    public static final String APPLICATION = PREFIX + "application";

    public static final String SERVICE = PREFIX + "service";

    public static final String INSTANCE = PREFIX + "instance";

    public static final String CATEGORY = PREFIX + "category";

    private EventFields() {
    }
}
