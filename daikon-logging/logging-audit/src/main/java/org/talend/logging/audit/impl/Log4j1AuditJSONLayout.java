package org.talend.logging.audit.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.pattern.ThrowableInformationPatternConverter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.talend.daikon.logging.event.field.HostData;
import org.talend.daikon.logging.event.field.LayoutFields;
import org.talend.daikon.logging.event.layout.LayoutUtils;

import net.minidev.json.JSONObject;

/**
 *
 */
public class Log4j1AuditJSONLayout extends Layout {

    private boolean locationInfo;

    private String customUserFields;

    /**
     * For backwards compatibility, the default is to generate location information
     * in the log messages.
     */
    public Log4j1AuditJSONLayout() {
        this(true);
    }

    /**
     * Creates a layout that optionally inserts location information into log messages.
     *
     * @param locationInfo whether or not to include location information in the log messages.
     */
    public Log4j1AuditJSONLayout(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String format(LoggingEvent loggingEvent) {
        JSONObject logstashEvent = new JSONObject();
        JSONObject userFieldsEvent = new JSONObject();
        HostData host = new HostData();
        Map<String, String> mdc = loggingEvent.getProperties();
        String ndc = loggingEvent.getNDC();

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
        logstashEvent.put("audit", "true");
        logstashEvent.put(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        logstashEvent.put(LayoutFields.TIME_STAMP, LayoutUtils.dateFormat(loggingEvent.getTimeStamp()));
        logstashEvent.put(LayoutFields.AGENT_TIME_STAMP, LayoutUtils.dateFormat(new Date().getTime()));
        logstashEvent.put(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        logstashEvent.put(LayoutFields.THREAD_NAME, loggingEvent.getThreadName());
        logstashEvent.put(LayoutFields.LOG_MESSAGE, loggingEvent.getRenderedMessage());

        handleThrown(logstashEvent, loggingEvent);

        JSONObject logSourceEvent = createLogSourceEvent(loggingEvent, host);
        logstashEvent.put(LayoutFields.LOG_SOURCE, logSourceEvent);

        LayoutUtils.addMDC(mdc, userFieldsEvent, logstashEvent);

        if (!userFieldsEvent.isEmpty()) {
            logstashEvent.put(LayoutFields.CUSTOM_INFO, userFieldsEvent);
        }

        if (ndc != null) {
            logstashEvent.put(LayoutFields.NDC, ndc);
        }

        return logstashEvent.toString() + "\n";
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
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

    @Override
    public void activateOptions() {
        //Not used
    }

    private JSONObject createLogSourceEvent(LoggingEvent loggingEvent, HostData host) {
        JSONObject logSourceEvent = new JSONObject();
        if (locationInfo || loggingEvent.locationInformationExists()) {
            LocationInfo info = loggingEvent.getLocationInformation();
            logSourceEvent.put(LayoutFields.FILE_NAME, info.getFileName());
            logSourceEvent.put(LayoutFields.LINE_NUMBER, info.getLineNumber());
            logSourceEvent.put(LayoutFields.CLASS_NAME, info.getClassName());
            logSourceEvent.put(LayoutFields.METHOD_NAME, info.getMethodName());
            logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            String jvmName = runtimeBean.getName();
            logSourceEvent.put(LayoutFields.PROCESS_ID, Long.valueOf(jvmName.split("@")[0]));
        }
        logSourceEvent.put(LayoutFields.HOST_NAME, host.getHostName());
        logSourceEvent.put(LayoutFields.HOST_IP, host.getHostAddress());
        return logSourceEvent;
    }

    private void handleThrown(JSONObject logstashEvent, LoggingEvent loggingEvent) {
        if (loggingEvent.getThrowableInformation() != null && loggingEvent.getThrowableInformation().getThrowable() != null) {

            final ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();
            final Throwable throwable = throwableInformation.getThrowable();

            if (throwable.getClass().getCanonicalName() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_CLASS, throwable.getClass().getCanonicalName());
            }
            if (throwable.getMessage() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_MESSAGE, throwable.getMessage());
            }

            createStackTraceEvent(logstashEvent, loggingEvent);
        }
    }

    private void createStackTraceEvent(JSONObject logstashEvent, LoggingEvent loggingEvent) {
        final String[] options = { "full" };
        final ThrowableInformationPatternConverter converter = ThrowableInformationPatternConverter.newInstance(options);
        final StringBuffer sb = new StringBuffer();
        converter.format(loggingEvent, sb);
        final String stackTrace = sb.toString();
        logstashEvent.put(LayoutFields.STACK_TRACE, stackTrace);
    }

}
