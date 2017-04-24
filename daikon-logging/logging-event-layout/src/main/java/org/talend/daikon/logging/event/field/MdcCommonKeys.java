package org.talend.daikon.logging.event.field;

/**
 * sdiallo
 * 
 * Common MDC keys
 */

public class MdcCommonKeys {

    //  The tenant Id (account Id)
    public static final String AGENT_ID = "agentId";

    //The job execution Id
    public static final String EXECUTION_ID = "executionId";

    //The application name
    public static final String APPLICATION_NAME = "applicationName";

    private MdcCommonKeys() {
        // not to be instantiated
    }
}
