
package org.talend.daikon.logging.event.layout;

import net.minidev.json.JSONObject;
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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Log4j2 JSON Layout
 *
 * @author sdiallo
 *
 */
@Plugin(name = "Log4j2JSONLayout", category = "Core", elementType = "layout", printObject = true)
public class Log4j2JSONLayout extends AbstractStringLayout {

    private boolean locationInfo;

    private boolean hostInfo;

    private String customUserFields;

    private Map<String, String> metaFields = new HashMap<>();

    private Map<String, String> additionalAttributes = new HashMap<>();

    protected Log4j2JSONLayout(final Boolean locationInfo, final Boolean hostInfo, final Charset charset,
            final Map<String, String> additionalLogAttributes) {
        super(charset);
        setLocationInfo(locationInfo);
        setHostInfo(hostInfo);
        additionalAttributes.putAll(additionalLogAttributes);
    }

    public void setMetaFields(Map<String, String> metaFields) {
        this.metaFields = new HashMap<>(metaFields);
    }

    /**
     * Creates a JSON Layout.
     *
     * @param locationInfo
     * If "true", includes the location information in the generated JSON.
     * @param hostInfo
     * If "true", includes the information about the local host name and IP address.
     * @param properties
     * If "true", includes the thread context in the generated JSON.
     * @param complete
     * If "true", includes the JSON header and footer, defaults to "false".
     * @param compact
     * If "true", does not use end-of-lines and indentation, defaults to "false".
     * @param eventEol
     * If "true", forces an EOL after each log event (even if compact is "true"), defaults to "false". This
     * allows one even per line, even in compact mode.
     * @param charset
     * The character set to use, if {@code null}, uses "UTF-8".
     * @param pairs
     * MDC attributes
     * @return A JSON Layout.
     */
    @PluginFactory
    public static AbstractStringLayout createLayout(
    // @formatter:off
            @PluginAttribute(value = "locationInfo") final boolean locationInfo,
            @PluginAttribute(value = "hostInfo", defaultBoolean = true) final boolean hostInfo,
            @PluginAttribute(value = "properties") final boolean properties,
            @PluginAttribute(value = "complete") final boolean complete,
            @PluginAttribute(value = "compact") final boolean compact,
            @PluginAttribute(value = "eventEol") final boolean eventEol,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset,
            @PluginElement("Pairs") final KeyValuePair[] pairs
            // @formatter:on
    ) {

        // Unpacke the pairs list
        final Map<String, String> additionalLogAttributes = unpackPairs(pairs);
        return new Log4j2JSONLayout(locationInfo, hostInfo, charset, additionalLogAttributes);

    }

    /**
     * Formats a {@link org.apache.logging.log4j.core.LogEvent}.
     *
     * @param loggingEvent The LogEvent.
     * @return The JSON representation of the LogEvent.
     */
    @Override
    public String toSerializable(final LogEvent loggingEvent) {
        JSONObject logstashEvent = new JSONObject();
        JSONObject userFieldsEvent = new JSONObject();
        HostData host = new HostData();

        // Extract and add fields from log4j2 config, if defined
        LayoutUtils.addUserFields(additionalAttributes, userFieldsEvent);

        Map<String, String> mdc = LayoutUtils.processMDCMetaFields(loggingEvent.getContextData().toMap(), logstashEvent,
                metaFields);

        // Now we start injecting our own stuff.
        logstashEvent.put(LayoutFields.VERSION, LayoutFields.VERSION_VALUE);
        logstashEvent.put(LayoutFields.TIME_STAMP, LayoutUtils.dateFormat(loggingEvent.getTimeMillis()));
        logstashEvent.put(LayoutFields.SEVERITY, loggingEvent.getLevel().toString());
        logstashEvent.put(LayoutFields.THREAD_NAME, loggingEvent.getThreadName());
        logstashEvent.put(LayoutFields.AGENT_TIME_STAMP, LayoutUtils.dateFormat(new Date().getTime()));
        final String formattedMessage = loggingEvent.getMessage().getFormattedMessage();
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

    public boolean getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(boolean hostInfo) {
        this.hostInfo = hostInfo;
    }

    public String getUserFields() {
        return customUserFields;
    }

    public void setUserFields(String userFields) {
        this.customUserFields = userFields;
    }

    private static Map<String, String> unpackPairs(final KeyValuePair[] pairs) {
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
        return additionalLogAttributes;
    }

    private JSONObject createLogSourceEvent(final LogEvent loggingEvent, HostData host) {
        JSONObject logSourceEvent = new JSONObject();
        if (locationInfo && loggingEvent.getSource() != null) {
            logSourceEvent.put(LayoutFields.FILE_NAME, loggingEvent.getSource().getFileName());
            logSourceEvent.put(LayoutFields.LINE_NUMBER, loggingEvent.getSource().getLineNumber());
            logSourceEvent.put(LayoutFields.CLASS_NAME, loggingEvent.getSource().getClassName());
            logSourceEvent.put(LayoutFields.METHOD_NAME, loggingEvent.getSource().getMethodName());
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            String jvmName = runtimeBean.getName();
            logSourceEvent.put(LayoutFields.PROCESS_ID, Long.valueOf(jvmName.split("@")[0]));
        }
        logSourceEvent.put(LayoutFields.LOGGER_NAME, loggingEvent.getLoggerName());
        if (hostInfo) {
            logSourceEvent.put(LayoutFields.HOST_NAME, host.getHostName());
            logSourceEvent.put(LayoutFields.HOST_IP, host.getHostAddress());
        }
        return logSourceEvent;
    }

    private void handleThrown(final JSONObject logstashEvent, final LogEvent loggingEvent) {
        if (loggingEvent.getThrown() != null) {
            if (loggingEvent.getThrown().getClass() != null && loggingEvent.getThrown().getClass().getCanonicalName() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_CLASS, loggingEvent.getThrown().getClass().getCanonicalName());
            }

            if (loggingEvent.getThrown().getMessage() != null) {
                logstashEvent.put(LayoutFields.EXCEPTION_MESSAGE, loggingEvent.getThrown().getMessage());
            }
            createStackTraceEvent(logstashEvent, loggingEvent);
        }
    }

    private void createStackTraceEvent(final JSONObject logstashEvent, final LogEvent loggingEvent) {
        if (loggingEvent.getThrown().getStackTrace() != null) {
            final String[] options = { "full" };
            final ThrowablePatternConverter converter = ThrowablePatternConverter.newInstance(configuration, options);
            final StringBuilder sb = new StringBuilder();
            converter.format(loggingEvent, sb);
            final String stackTrace = sb.toString();
            logstashEvent.put(LayoutFields.STACK_TRACE, stackTrace);
        }
    }
}
