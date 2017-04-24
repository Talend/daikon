package org.talend.daikon.logging.event.layout;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
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

    private HostData host = new HostData();

    private String hostName = host.getHostName();

    private String hostAdresse = host.getHostAddress();

    /**
     * For backwards compatibility, the default is to generate location information
     * in the log messages.
     */
    public Log4jJSONLayout() {
        this(true);
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
        String threadName = loggingEvent.getThreadName();
        long timestamp = loggingEvent.getTimeStamp();
        Map<String, String> mdc = loggingEvent.getProperties();
        String ndc = loggingEvent.getNDC();

        logstashEvent = new JSONObject();
        String whoami = this.getClass().getSimpleName();

        logstashEvent.put(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        logstashEvent.put(LayoutFields.TIME_STAMP, dateFormat(timestamp));
        Date currentDate = new Date();
        logstashEvent.put(LayoutFields.AGENT_TIME_STAMP, dateFormat(currentDate.getTime()));

        /**
         * Extract and add fields from log4j config, if defined
         */
        if (getUserFields() != null) {
            String userFlds = getUserFields();
            LogLog.debug("[" + whoami + "] Got user data from log4j property: " + userFlds);
            addUserFields(userFlds);
        }

        /**
         * Now we start injecting our own stuff.
         */
        logstashEvent.put(LayoutFields.HOST_NAME, hostName);
        logstashEvent.put(LayoutFields.HOST_IP, hostAdresse);
        logstashEvent.put(LayoutFields.LOG_MESSAGE, loggingEvent.getRenderedMessage());

        if (loggingEvent.getThrowableInformation() != null) {
            final ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();
            if (throwableInformation.getThrowable().getClass().getCanonicalName() != null) {
                addEventData(LayoutFields.EXCEPTION_CLASS, throwableInformation.getThrowable().getClass().getCanonicalName());
            }

            if (throwableInformation.getThrowable().getMessage() != null) {
                addEventData(LayoutFields.EXCEPTION_MESSAGE, throwableInformation.getThrowable().getMessage());
            }

            if (throwableInformation.getThrowableStrRep() != null) {
                final String[] options = { "full" };
                final ThrowableInformationPatternConverter converter = ThrowableInformationPatternConverter.newInstance(options);
                final StringBuffer sb = new StringBuffer();
                converter.format(loggingEvent, sb);
                final String stackTrace = sb.toString();
                addEventData(LayoutFields.STACK_TRACE, stackTrace);
            }
        }

        if (locationInfo) {
            LocationInfo info = loggingEvent.getLocationInformation();
            addEventData(LayoutFields.FILE_NAME, info.getFileName());
            addEventData(LayoutFields.LINE_NUMBER, info.getLineNumber());
            addEventData(LayoutFields.CLASS_NAME, info.getClassName());
            addEventData(LayoutFields.METHOD_NAME, info.getMethodName());
        }

        addEventData(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
        addEventData(LayoutFields.NDC, ndc);
        addEventData(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        addEventData(LayoutFields.THREAD_NAME, threadName);
        for (Map.Entry<String, String> entry : mdc.entrySet()) {
            addEventData(entry.getKey(), entry.getValue());
        }
        return logstashEvent.toString() + "\n";
    }

    public static String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
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

    private void addUserFields(String data) {
        if (null != data) {
            String[] pairs = data.split(",");
            for (String pair : pairs) {
                String[] userField = pair.split(":", 2);
                if (userField[0] != null) {
                    String key = userField[0];
                    String val = userField[1];
                    addEventData(key, val);
                }
            }
        }
    }

    private void addEventData(String keyname, Object keyval) {
        if (null != keyval) {
            logstashEvent.put(keyname, keyval);
        }
    }
}
