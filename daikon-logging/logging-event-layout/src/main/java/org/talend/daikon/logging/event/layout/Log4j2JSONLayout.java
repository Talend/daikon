
package org.talend.daikon.logging.event.layout;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.talend.daikon.logging.event.field.HostData;
import org.talend.daikon.logging.event.field.LayoutFields;

import net.minidev.json.JSONObject;

/**
 * Log4j2 JSON Layout
 * @author sdiallo
 *
 */
@Plugin(name = "Log4j2JSONLayout", category = "Core", elementType = "layout", printObject = true)
public class Log4j2JSONLayout extends AbstractStringLayout {

    static final String CONTENT_TYPE = "application/json";

    private static final Map<String, String> ADDITIONNAL_ATTRIBUTES = new HashMap<>();

    private boolean locationInfo;

    private String customUserFields;

    private JSONObject logstashEvent;

    private JSONObject userFieldsEvent;

    protected Log4j2JSONLayout(final Boolean locationInfo, final Charset charset, final Map<String, String> additionalLogAttributes) {
        super(charset);
        setLocationInfo(locationInfo);
        Log4j2JSONLayout.ADDITIONNAL_ATTRIBUTES.putAll(additionalLogAttributes);
    }

    private String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

    /**
     * Creates a JSON Layout.
     *
     * @param locationInfo
     *        If "true", includes the location information in the generated JSON.
     * @param properties
     *        If "true", includes the thread context in the generated JSON.
     * @param complete
     *        If "true", includes the JSON header and footer, defaults to "false".
     * @param compact
     *        If "true", does not use end-of-lines and indentation, defaults to "false".
     * @param eventEol
     *        If "true", forces an EOL after each log event (even if compact is "true"), defaults to "false". This
     *        allows one even per line, even in compact mode.
     * @param charset
     *        The character set to use, if {@code null}, uses "UTF-8".
     * @param pairs
     *          MDC attributes
     * @return A JSON Layout.
     */
    @PluginFactory
    public static AbstractStringLayout createLayout(
            // @formatter:off
            @PluginAttribute(value = "locationInfo", defaultBoolean = false) final boolean locationInfo,
            @PluginAttribute(value = "properties", defaultBoolean = false) final boolean properties,
            @PluginAttribute(value = "complete", defaultBoolean = false) final boolean complete,
            @PluginAttribute(value = "compact", defaultBoolean = false) final boolean compact,
            @PluginAttribute(value = "eventEol", defaultBoolean = false) final boolean eventEol,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset,
            @PluginElement("Pairs") final KeyValuePair[] pairs
            // @formatter:on
    ) {

        //Unpacke the pairs list
        final Map<String, String> additionalLogAttributes = new HashMap<>();
        if (pairs != null && pairs.length > 0) {
            for (final KeyValuePair pair : pairs) {
                final String key = pair.getKey();
                if (key == null) {
                    LOGGER.error("A null key is not valid in MapFilter");
                }
                final String value = pair.getValue();
                if (value == null) {
                    LOGGER.error("A null value for key " + key + " is not allowed in MapFilter");
                }
                if (additionalLogAttributes.containsKey(key)) {
                    LOGGER.error("Duplicate entry for key: {} is forbidden!", key);
                }
                additionalLogAttributes.put(key, value);
            }

        }
        return new Log4j2JSONLayout(locationInfo, charset, additionalLogAttributes);

    }


    /**
     * Formats a {@link org.apache.logging.log4j.core.LogEvent}.
     *
     * @param loggingEvent The LogEvent.
     * @return The JSON representation of the LogEvent.
     */
    @Override
    public String toSerializable(final LogEvent loggingEvent) {
        logstashEvent = new JSONObject();
        userFieldsEvent = new JSONObject();
        HostData host = new HostData();
        String threadName = loggingEvent.getThreadName();
        long timestamp = loggingEvent.getTimeMillis();
        Map<String, String> mdc = loggingEvent.getContextData().toMap();

        /**
         * Extract and add fields from log4j2 config, if defined
         */
        addUserFields(ADDITIONNAL_ATTRIBUTES);
        
        /**
         * Now we start injecting our own stuff.
         */
        addEventData(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        addEventData(LayoutFields.TIME_STAMP, dateFormat(timestamp));
        addEventData(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        addEventData(LayoutFields.THREAD_NAME, threadName);
        Date currentDate = new Date();
        addEventData(LayoutFields.AGENT_TIME_STAMP, dateFormat(currentDate.getTime()));
        addEventData(LayoutFields.LOG_MESSAGE, loggingEvent.getMessage().getFormattedMessage());

        if (loggingEvent.getThrown() != null) {
            if (loggingEvent.getThrown().getClass() != null && loggingEvent.getThrown().getClass().getCanonicalName() != null) {
                addEventData(LayoutFields.EXCEPTION_CLASS, loggingEvent.getThrown().getClass().getCanonicalName());
            }

            if (loggingEvent.getThrown().getMessage() != null) {
                addEventData(LayoutFields.EXCEPTION_MESSAGE, loggingEvent.getThrown().getMessage());
            }

            if (loggingEvent.getThrown().getStackTrace() != null) {
                final String[] options = { "full" };
                final ThrowablePatternConverter converter = ThrowablePatternConverter.newInstance(options);
                final StringBuilder sb = new StringBuilder();
                converter.format(loggingEvent, sb);
                final String stackTrace = sb.toString();
                addEventData(LayoutFields.STACK_TRACE, stackTrace);
            }
        }

        JSONObject logSourceEvent = new JSONObject();
        if (locationInfo && loggingEvent.getSource() != null) {
            logSourceEvent.put(LayoutFields.FILE_NAME, loggingEvent.getSource().getFileName());
            logSourceEvent.put(LayoutFields.LINE_NUMBER, loggingEvent.getSource().getLineNumber());
            logSourceEvent.put(LayoutFields.CLASS_NAME, loggingEvent.getSource().getClassName());
            logSourceEvent.put(LayoutFields.METHOD_NAME, loggingEvent.getSource().getMethodName());
            logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
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

    private void addUserFields(Map<String, String> additionalLogAttributes) {
        for (Map.Entry<String, String> entry : additionalLogAttributes.entrySet()) {
            userFieldsEvent.put(entry.getKey(), entry.getValue());
        }
    }

    private void addEventData(String keyname, Object keyval) {
        if (null != keyval) {
            logstashEvent.put(keyname, keyval);
        }
    }

}
