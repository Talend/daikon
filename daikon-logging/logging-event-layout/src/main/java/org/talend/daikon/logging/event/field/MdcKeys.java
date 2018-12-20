package org.talend.daikon.logging.event.field;

/**
 * Common MDC keys
 * 
 * @author sdiallo
 */

public final class MdcKeys {

    // The tenant Id (account Id)
    public static final String ACCOUNT_ID = "accountId";

    // The job execution Id
    public static final String EXECUTION_ID = "executionId";

    // The name of the stack
    public static final String STACK_NAME = "stackName";

    // The user Id
    public static final String USER_ID = "userId";

    // The user atcivity Id
    public static final String USER_ACTIVITY_ID = "user-activity-id";

    // The request userActivtyId name in the Header
    // Use this key when you want to generate and set the correlation Id in the header of the request in your application
    public static final String HEADER_REQUEST_USER_ACTIVITY_ID = "X-talend-user-activity-id";

    // Is an attribute which allows to group similar types of log events
    public static final String CATEGORY = "category";

    // Is an attribute which allows to group similar types of log events
    public static final String EVENT_TYPE = "eventType";

    // Audit is a flag, if this log event is needed to be saved for auditing purpose
    public static final String AUDIT = "audit";

    private MdcKeys() {
        // not to be instantiated
    }
}
