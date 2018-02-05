package org.talend.daikon.logging.event.layout;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Print no location info by default.
     */
    public LogbackJSONLayout() {
        this(false);
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
        JSONObject logstashEvent = new JSONObject();
        JSONObject userFieldsEvent = new JSONObject();
        HostData host = new HostData();
        Map<String, String> mdc = loggingEvent.getMDCPropertyMap();

        /**
         * Extract and add fields from log4j config, if defined
         */
        if (getUserFields() != null) {
            String userFlds = getUserFields();
            LayoutUtils.addUserFields(userFlds, userFieldsEvent);
        }

        /**
         * Now we start injecting our own stuff.
         */
        logstashEvent.put(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        logstashEvent.put(LayoutFields.EVENT_UUID, UUID.randomUUID().toString());
        logstashEvent.put(LayoutFields.TIME_STAMP, dateFormat(loggingEvent.getTimeStamp()));
        logstashEvent.put(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        logstashEvent.put(LayoutFields.THREAD_NAME, loggingEvent.getThreadName());
        logstashEvent.put(LayoutFields.AGENT_TIME_STAMP, dateFormat(new Date().getTime()));
        final String formattedMessage = loggingEvent.getFormattedMessage();
        if (formattedMessage != null) {
            logstashEvent.put(LayoutFields.LOG_MESSAGE, formattedMessage);
        }
        handleThrown(logstashEvent, loggingEvent);
        JSONObject logSourceEvent = createLogSourceEvent(loggingEvent, host);
        logstashEvent.put(LayoutFields.LOG_SOURCE, logSourceEvent);
        LayoutUtils.addMDC(mdc, userFieldsEvent, logstashEvent);

        if (!userFieldsEvent.isEmpty()) {
            logstashEvent.put(LayoutFields.CUSTOM_INFO, userFieldsEvent);
        }

        return logstashEvent.toString() + "\n";

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

    private void handleThrown(JSONObject logstashEvent, ILoggingEvent loggingEvent) {
        if (loggingEvent.getThrowableProxy() != null) {

            if (loggingEvent.getThrowableProxy().getClassName() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_CLASS, loggingEvent.getThrowableProxy().getClassName());
            }

            if (loggingEvent.getThrowableProxy().getMessage() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_MESSAGE, loggingEvent.getThrowableProxy().getMessage());
            }

            ThrowableProxyConverter converter = new RootCauseFirstThrowableProxyConverter();
            converter.setOptionList(Arrays.asList("full"));
            converter.start();
            String stackTrace = converter.convert(loggingEvent);
            logstashEvent.put(LayoutFields.STACK_TRACE, stackTrace);
        }
    }

    private JSONObject createLogSourceEvent(ILoggingEvent loggingEvent, HostData host) {
        JSONObject logSourceEvent = new JSONObject();
        if (locationInfo) {
            StackTraceElement callerData = extractCallerData(loggingEvent);
            if (callerData != null) {
                logSourceEvent.put(LayoutFields.FILE_NAME, callerData.getFileName());
                logSourceEvent.put(LayoutFields.LINE_NUMBER, callerData.getLineNumber());
                logSourceEvent.put(LayoutFields.CLASS_NAME, callerData.getClassName());
                logSourceEvent.put(LayoutFields.METHOD_NAME, callerData.getMethodName());
            }
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            String jvmName = runtimeBean.getName();
            logSourceEvent.put(LayoutFields.PROCESS_ID, Long.valueOf(jvmName.split("@")[0]));
        }
        logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
        logSourceEvent.put(LayoutFields.HOST_NAME, host.getHostName());
        logSourceEvent.put(LayoutFields.HOST_IP, host.getHostAddress());
        return logSourceEvent;
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

}
