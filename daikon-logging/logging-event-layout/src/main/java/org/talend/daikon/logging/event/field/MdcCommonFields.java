package org.talend.daikon.logging.event.field;

/**
 * sdiallo
 * 
 * Common MDC fields
 */

public class MdcCommonFields {

    //  The tenant Id (account Id)
    public static final String AGENT_ID = "agentId";

    //The job  Id
    public static final String JOB_ID = "jobId";

    //The job execution Id
    public static final String EXECUTION_ID = "executionId";

    //The application name
    public static final String APPLICATION_NAME = "applicationName";

    private MdcCommonFields() {
        // not to be instantiated
    }
}
