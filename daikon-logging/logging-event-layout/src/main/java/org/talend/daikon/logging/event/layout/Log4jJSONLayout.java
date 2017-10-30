package org.talend.daikon.logging.event.layout;

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

import net.minidev.json.JSONObject;

/**
 * Log4j JSON Layout
 * @author sdiallo
 *
 */
public class Log4jJSONLayout extends Layout {

    private boolean locationInfo;

    private String customUserFields;

    private boolean ignoreThrowable;

    private JSONObject logstashEvent;

    /**
     * Print no location info by default.
     */
    public Log4jJSONLayout() {
        this(false);
    }

    /**
     * Creates a layout that optionally inserts location information into log messages.
     *
     * @param locationInfo whether or not to include location information in the log messages.
     */
    public Log4jJSONLayout(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String format(LoggingEvent loggingEvent) {
        logstashEvent = new JSONObject();
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
        addEventData(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        addEventData(LayoutFields.TIME_STAMP, LayoutUtils.dateFormat(loggingEvent.getTimeStamp()));
        addEventData(LayoutFields.AGENT_TIME_STAMP, LayoutUtils.dateFormat(new Date().getTime()));
        addEventData(LayoutFields.NDC, ndc);
        addEventData(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        addEventData(LayoutFields.THREAD_NAME, loggingEvent.getThreadName());
        addEventData(LayoutFields.LOG_MESSAGE, loggingEvent.getRenderedMessage());
        handleThrown(loggingEvent);
        JSONObject logSourceEvent = createLogSourceEvent(loggingEvent, host);
        addEventData(LayoutFields.LOG_SOURCE, logSourceEvent);
        LayoutUtils.addMDC(mdc, userFieldsEvent, logstashEvent);

        if (!userFieldsEvent.isEmpty()) {
            addEventData(LayoutFields.CUSTOM_INFO, userFieldsEvent);
        }

        return logstashEvent.toString() + "\n";
    }

    @Override
    public boolean ignoresThrowable() {
        return ignoreThrowable;
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
        if (locationInfo) {
            LocationInfo info = loggingEvent.getLocationInformation();
            logSourceEvent.put(LayoutFields.FILE_NAME, info.getFileName());
            logSourceEvent.put(LayoutFields.LINE_NUMBER, info.getLineNumber());
            logSourceEvent.put(LayoutFields.CLASS_NAME, info.getClassName());
            logSourceEvent.put(LayoutFields.METHOD_NAME, info.getMethodName());
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            String jvmName = runtimeBean.getName();
            logSourceEvent.put(LayoutFields.PROCESS_ID, Long.valueOf(jvmName.split("@")[0]));
        }
        logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
        logSourceEvent.put(LayoutFields.HOST_NAME, host.getHostName());
        logSourceEvent.put(LayoutFields.HOST_IP, host.getHostAddress());
        return logSourceEvent;
    }

    private void addEventData(String keyname, Object keyval) {
        if (null != keyval) {
            logstashEvent.put(keyname, keyval);
        }
    }

    private void handleThrown(LoggingEvent loggingEvent) {
        if (loggingEvent.getThrowableInformation() != null) {
            final ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();
            if (throwableInformation.getThrowable().getClass().getCanonicalName() != null) {
                addEventData(LayoutFields.EXCEPTION_CLASS, throwableInformation.getThrowable().getClass().getCanonicalName());
            }

            if (throwableInformation.getThrowable().getMessage() != null) {
                addEventData(LayoutFields.EXCEPTION_MESSAGE, throwableInformation.getThrowable().getMessage());
            }
            createStackTraceEvent(loggingEvent, throwableInformation);
        }
    }

    private void createStackTraceEvent(LoggingEvent loggingEvent, final ThrowableInformation throwableInformation) {
        if (throwableInformation.getThrowableStrRep() != null) {
            final String[] options = { "full" };
            final ThrowableInformationPatternConverter converter = ThrowableInformationPatternConverter.newInstance(options);
            final StringBuffer sb = new StringBuffer();
            converter.format(loggingEvent, sb);
            final String stackTrace = sb.toString();
            addEventData(LayoutFields.STACK_TRACE, stackTrace);
        }
    }

}
