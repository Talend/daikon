package org.talend.daikon.logging.event.layout;

import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.daikon.logging.event.field.LayoutFields;

import net.minidev.json.JSONObject;

/**
 * Json Layout Utils
 * 
 * @author sdiallo
 *
 */
public final class LayoutUtils {

    /**
     * 
     * @param mdc
     * @param userFieldsEvent
     * @param logstashEvent
     */
    public static void addMDC(Map<String, String> mdc, JSONObject userFieldsEvent, JSONObject logstashEvent) {
        for (Map.Entry<String, String> entry : mdc.entrySet()) {
            if (isSleuthField(entry.getKey())) {
                logstashEvent.put(entry.getKey(), entry.getValue());
            } else {
                userFieldsEvent.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 
     * @param timestamp
     * @return
     */
    public static String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

    /**
     * 
     * @param data
     * @param userFieldsEvent
     */
    public static void addUserFields(String data, JSONObject userFieldsEvent) {
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

    /**
     * 
     * @param additionalLogAttributes
     * @param userFieldsEvent
     */
    public static void addUserFields(Map<String, String> additionalLogAttributes, JSONObject userFieldsEvent) {
        for (Map.Entry<String, String> entry : additionalLogAttributes.entrySet()) {
            userFieldsEvent.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Check if this field name added by Spring Cloud Sleuth
     * 
     * @param fieldName
     * @return true if the fieldName represent added by Spring Cloud Sleuth
     */
    public static boolean isSleuthField(String fieldName) {
        return "service".equals(fieldName) || "X-B3-SpanId".equals(fieldName) || "X-B3-TraceId".equals(fieldName)
                || "X-Span-Export".equals(fieldName);
    }

    /**
     * This method moves pre-defined values from MDC into event structure (otherwise it would go into custom info map).
     *
     * @param existingMdc the MDC values
     * @param logstashEvent the Log Event
     * @param metaFields map of the fields to be moved (key - field name in MDC, value - field name in log event)
     * @return MDC map without fields which have become part of log event
     */
    public static Map<String, String> processMDCMetaFields(Map<String, String> existingMdc, JSONObject logstashEvent,
            Map<String, String> metaFields) {
        final Map<String, String> mdc = new LinkedHashMap<>(existingMdc);

        if (metaFields == null) {
            return mdc;
        }

        for (Map.Entry<String, String> field : metaFields.entrySet()) {
            if (mdc.containsKey(field.getKey())) {
                String val = mdc.remove(field.getKey());
                logstashEvent.put(field.getValue(), val);
            }
        }

        return mdc;
    }

    private LayoutUtils() {
        // not to be instantiated
    }
}
