package org.talend.daikon.logging.event.field;

import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Fields name present in each log entry
 * 
 * @author sdiallo
 *
 */
public final class LayoutFields {

    public static final Integer VERSION_VALUE = 1;

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static final FastDateFormat DATETIME_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", UTC);

    // Json fields
    public static final String VERSION = "@version";

    public static final String EVENT_UUID = "eventUUID";

    public static final String TIME_STAMP = "logTimestamp";

    public static final String AGENT_TIME_STAMP = "agentTimestamp";

    public static final String LOG_MESSAGE = "logMessage";

    public static final String LOG_SOURCE = "logSource";

    public static final String SEVERITY = "severity";

    public static final String THREAD_NAME = "threadName";

    public static final String NDC = "NDC";

    public static final String EXCEPTION_CLASS = "exceptionClass";

    public static final String EXCEPTION_MESSAGE = "exceptionMessage";

    public static final String STACK_TRACE = "stackTrace";

    public static final String CLASS_NAME = "class.name";

    public static final String FILE_NAME = "file.name";

    public static final String LINE_NUMBER = "line.number";

    public static final String METHOD_NAME = "method.name";

    public static final String LOGGER_NAME = "logger.name";

    public static final String HOST_NAME = "host.name";

    public static final String HOST_IP = "host.address";

    public static final String PROCESS_ID = "process.id";

    // Users fields defined by MDC
    public static final String CUSTOM_INFO = "customInfo";

    private LayoutFields() {
        // not to be instantiated
    }
}
