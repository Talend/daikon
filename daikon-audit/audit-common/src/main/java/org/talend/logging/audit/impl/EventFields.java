package org.talend.logging.audit.impl;

/**
 *
 */
public class EventFields {

    public static final String ID = "eventid";

    public static final String CATEGORY = "category";

    public static final String AUDIT = "audit";

    public static final String APPLICATION = "application";

    public static final String SERVICE = "service";

    public static final String INSTANCE = "instance";

    public static final String USER = "user";

    public static final String OPERATION = "operation";

    public static final String RESOURCE = "resource";

    public static final String RESULT = "result";

    public static final String MDC_USER = USER;

    public static final String MDC_OPERATION = OPERATION;

    public static final String MDC_RESOURCE = RESOURCE;

    public static final String MDC_RESULT = RESULT;

    private static final String MDC_PREFIX = "talend.meta.";

    public static final String MDC_ID = MDC_PREFIX + ID;

    public static final String MDC_CATEGORY = MDC_PREFIX + CATEGORY;

    public static final String MDC_AUDIT = MDC_PREFIX + AUDIT;

    public static final String MDC_APPLICATION = MDC_PREFIX + APPLICATION;

    public static final String MDC_SERVICE = MDC_PREFIX + SERVICE;

    public static final String MDC_INSTANCE = MDC_PREFIX + INSTANCE;

    private EventFields() {
    }
}
