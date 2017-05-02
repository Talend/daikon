package org.talend.daikon.logging.event.layout;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.helpers.LogLog;
import org.talend.daikon.logging.event.field.HostData;
import org.talend.daikon.logging.event.field.LayoutFields;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.minidev.json.JSONObject;

/**
 * Logback JSON Layout
 * @author sdiallo
 *
 */
public class LogbackJSONLayout extends JsonLayout<ILoggingEvent> {

    private boolean locationInfo;

    private String customUserFields;

    private JSONObject logstashEvent;

    private JSONObject userFieldsEvent;

    /**
     * For backwards compatibility, the default is to generate location information
     * in the log messages.
     */
    public LogbackJSONLayout() {
        this(true);
    }

    /**
     * Creates a layout that optionally inserts location information into log messages.
     *
     * @param locationInfo whether or not to include location information in the log messages.
     */
    public LogbackJSONLayout(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    @Override
    public String doLayout(ILoggingEvent loggingEvent) {
        logstashEvent = new JSONObject();
        userFieldsEvent = new JSONObject();
        HostData host = new HostData();
        String threadName = loggingEvent.getThreadName();
        long timestamp = loggingEvent.getTimeStamp();
        Map<String, String> mdc = loggingEvent.getMDCPropertyMap();
        String eventUUD = UUID.randomUUID().toString();
        String whoami = this.getClass().getSimpleName();

        /**
         * Extract and add fields from log4j config, if defined
         */
        if (getUserFields() != null) {
            String userFlds = getUserFields();
            LogLog.debug("[" + whoami + "] Got user data from logback property: " + userFlds);
            addUserFields(userFlds);
        }

        /**
         * Now we start injecting our own stuff.
         */
        addEventData(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        addEventData(LayoutFields.EVENT_UUID, eventUUD);
        addEventData(LayoutFields.TIME_STAMP, dateFormat(timestamp));
        addEventData(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        addEventData(LayoutFields.THREAD_NAME, threadName);
        Date currentDate = new Date();
        addEventData(LayoutFields.AGENT_TIME_STAMP, dateFormat(currentDate.getTime()));
        addEventData(LayoutFields.LOG_MESSAGE, loggingEvent.getFormattedMessage());

        if (loggingEvent.getThrowableProxy() != null) {

            if (loggingEvent.getThrowableProxy().getClass().getCanonicalName() != null) {
                addEventData(LayoutFields.EXCEPTION_CLASS, loggingEvent.getThrowableProxy().getClass().getCanonicalName());
            }

            if (loggingEvent.getThrowableProxy().getMessage() != null) {
                addEventData(LayoutFields.EXCEPTION_MESSAGE, loggingEvent.getThrowableProxy().getMessage());
            }

            ThrowableProxyConverter converter = new RootCauseFirstThrowableProxyConverter();
            String stackTrace = converter.convert(loggingEvent);
            addEventData(LayoutFields.STACK_TRACE, stackTrace);
        }

        JSONObject logSourceEvent = new JSONObject();
        if (locationInfo) {
            StackTraceElement callerData = extractCallerData(loggingEvent);
            if (callerData != null) {
                logSourceEvent.put(LayoutFields.FILE_NAME, callerData.getFileName());
                logSourceEvent.put(LayoutFields.LINE_NUMBER, callerData.getLineNumber());
                logSourceEvent.put(LayoutFields.CLASS_NAME, callerData.getClassName());
                logSourceEvent.put(LayoutFields.METHOD_NAME, callerData.getMethodName());
                logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
            }
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            String jvmName = runtimeBean.getName();
            logSourceEvent.put(LayoutFields.PROCESS_ID, Long.valueOf(jvmName.split("@")[0]));
        }
        logSourceEvent.put(LayoutFields.HOST_NAME, host.getHostName());
        logSourceEvent.put(LayoutFields.HOST_IP, host.getHostAddress());
        addEventData(LayoutFields.LOG_SOURCE, logSourceEvent);

        for (Map.Entry<String, String> entry : mdc.entrySet()) {
            userFieldsEvent.put(entry.getKey(), entry.getValue());
        }

        if (!userFieldsEvent.isEmpty()) {
            addEventData(LayoutFields.CUSTOM_INFO, userFieldsEvent);
        }

        return logstashEvent.toString() + "\n";

    }

    private void addUserFields(String data) {
        if (null != data) {

            String[] pairs = data.split(",");
            for (String pair : pairs) {
                String[] userField = pair.split(":", 2);
                if (userField[0] != null) {
                    String key = userField[0];
                    String val = userField[1];
                    userFieldsEvent.put(key, val);
                }
            }
        }
    }

    private void addEventData(String keyname, Object keyval) {
        if (null != keyval) {
            logstashEvent.put(keyname, keyval);
        }
    }

    private StackTraceElement extractCallerData(final ILoggingEvent event) {
        final StackTraceElement[] ste = event.getCallerData();
        if (ste == null || ste.length == 0) {
            return null;
        }
        return ste[0];
    }

    private String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

    /**
     * Query whether log messages include location information.
     *
     * @return true if location information is included in log messages, false otherwise.
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * Set whether log messages should include location information.
     *
     * @param locationInfo true if location information should be included, false otherwise.
     */
    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getUserFields() {
        return customUserFields;
    }

    public void setUserFields(String userFields) {
        this.customUserFields = userFields;
    }
}
