package org.talend.daikon.logging.event.layout;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.net.SyslogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;
import org.talend.daikon.logging.event.field.HostData;
import org.talend.daikon.logging.event.field.LayoutFields;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.minidev.json.JSONObject;

/**
 * Logback JSON Layout
 * 
 * @author sdiallo
 *
 */
public class LogbackJSONLayout extends JsonLayout<ILoggingEvent> {

    private boolean locationInfo;

    private String customUserFields;

    private Map<String, String> metaFields = new HashMap<>();

    private boolean addEventUuid = true;

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

        // Extract and add fields from log4j config, if defined
        String userFldsFromParam = getUserFields();
        // Extract and add fields from markers, if defined
        String userFldsFromMarker = getUserFieldsFromMarker(loggingEvent);
        LayoutUtils.addUserFields(mergeUserFields(userFldsFromParam, userFldsFromMarker), userFieldsEvent);

        Map<String, String> mdc = LayoutUtils.processMDCMetaFields(loggingEvent.getMDCPropertyMap(), logstashEvent, metaFields);

        // Now we start injecting our own stuff.
        if (addEventUuid) {
            logstashEvent.put(LayoutFields.EVENT_UUID, UUID.randomUUID().toString());
        }
        logstashEvent.put(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        logstashEvent.put(LayoutFields.TIME_STAMP, dateFormat(loggingEvent.getTimeStamp()));
        logstashEvent.put(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        logstashEvent.put(LayoutFields.THREAD_NAME, loggingEvent.getThreadName());
        logstashEvent.put(LayoutFields.AGENT_TIME_STAMP, dateFormat(new Date().getTime()));
        if (loggingEvent.getFormattedMessage() != null) {
            logstashEvent.put(LayoutFields.LOG_MESSAGE, loggingEvent.getFormattedMessage());
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

    public void setMetaFields(Map<String, String> metaFields) {
        this.metaFields = new HashMap<>(metaFields);
    }

    public void setAddEventUuid(boolean addEventUuid) {
        this.addEventUuid = addEventUuid;
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

    /**
     * Iterate over the logging event marker children, and concatenate them in a single string.
     *
     * @param event the logging event
     * @return a string that contains the marker children, separated by 'commas'
     */
    private String getUserFieldsFromMarker(final ILoggingEvent event) {
        Marker customFieldsMarker = LayoutUtils.findCustomFieldsMarker(event.getMarker(), new HashSet<>());
        if (customFieldsMarker != null) {
            Spliterator<Marker> markers = Spliterators.spliteratorUnknownSize(customFieldsMarker.iterator(), Spliterator.NONNULL);
            return StreamSupport.stream(markers, false).map(Marker::getName).collect(Collectors.joining(","));
        } else {
            return null;
        }
    }

    /**
     * Merges a list of user fields into a single one.
     *
     * {{{
     * field1: prop11:value11,prop12:value12
     * field2: prop21:value21,prop22:value22
     * }}}
     *
     * will result in:
     *
     * {{{
     * prop11:value11,prop12:value12,prop21:value21,prop22:value22
     * }}}
     *
     *
     * @param fields the list of user fields
     * @return a single user fields property, in which fields are separated by a comma.
     */
    private String mergeUserFields(String... fields) {
        String merged = Stream.of(fields).filter(Objects::nonNull).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(","));
        return (merged != null && merged.isEmpty()) ? null : merged;
    }

    private String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

}
