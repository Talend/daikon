package org.talend.daikon.logging.event;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.helpers.LogLog;
import org.talend.daikon.logging.event.field.HostData;
import org.talend.daikon.logging.event.field.LayoutFields;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
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

    private HostData host = new HostData();

    private String hostName = host.getHostName();

    private String hostAdresse = host.getHostAddress();

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
        String threadName = loggingEvent.getThreadName();
        long timestamp = loggingEvent.getTimeStamp();
        Map<String, String> mdc = loggingEvent.getMDCPropertyMap();

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
            LogLog.debug("[" + whoami + "] Got user data from logback property: " + userFlds);
            addUserFields(userFlds);
        }

        /**
         * Now we start injecting our own stuff.
         */
        logstashEvent.put(LayoutFields.HOST_NAME, hostName);
        logstashEvent.put(LayoutFields.HOST_IP, hostAdresse);
        logstashEvent.put(LayoutFields.LOG_MESSAGE, loggingEvent.getFormattedMessage());

        if (loggingEvent.getCallerData() != null) {
            final IThrowableProxy throwableInformation = loggingEvent.getThrowableProxy();
            if (throwableInformation != null) {

                if (throwableInformation.getClass().getCanonicalName() != null) {
                    addEventData(LayoutFields.EXCEPTION_CLASS, throwableInformation.getClass().getCanonicalName());
                }

                if (throwableInformation.getMessage() != null) {
                    addEventData(LayoutFields.EXCEPTION_MESSAGE, throwableInformation.getMessage());
                }

                if (throwableInformation.getStackTraceElementProxyArray() != null) {
                    ThrowableProxyConverter converter = new RootCauseFirstThrowableProxyConverter();
                    String stackTrace = converter.convert(loggingEvent);
                    addEventData(LayoutFields.STACK_TRACE, stackTrace);
                }
            }
            if (locationInfo) {
                StackTraceElement callerData = extractCallerData(loggingEvent);
                if (callerData != null) {
                    addEventData(LayoutFields.FILE_NAME, callerData.getFileName());
                    addEventData(LayoutFields.LINE_NUMBER, callerData.getLineNumber());
                    addEventData(LayoutFields.CLASS_NAME, callerData.getClassName());
                    addEventData(LayoutFields.METHOD_NAME, callerData.getMethodName());
                }
            }
        }

        addEventData(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
        addEventData(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        addEventData(LayoutFields.THREAD_NAME, threadName);

        for (Map.Entry<String, String> entry : mdc.entrySet()) {
            addEventData(entry.getKey(), entry.getValue());
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

    private StackTraceElement extractCallerData(final ILoggingEvent event) {
        final StackTraceElement[] ste = event.getCallerData();
        if (ste == null || ste.length == 0) {
            return null;
        }
        return ste[0];
    }

    private static String dateFormat(long timestamp) {
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
