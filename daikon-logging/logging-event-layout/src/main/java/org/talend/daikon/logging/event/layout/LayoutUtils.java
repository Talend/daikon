package org.talend.daikon.logging.event.layout;

import java.util.Map;

import org.talend.daikon.logging.event.field.LayoutFields;

import net.minidev.json.JSONObject;

/**
 * Json Layout Utils
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
     *  Check if this field name added by Spring Cloud Sleuth
     * @param fieldName
     * @return true if the fieldName represent added by Spring Cloud Sleuth 
     */
    public static boolean isSleuthField(String fieldName) {
        return "service".equals(fieldName) || "X-B3-SpanId".equals(fieldName) || "X-B3-TraceId".equals(fieldName)
                || "X-Span-Export".equals(fieldName);
    }

    private LayoutUtils() {
        // not to be instantiated
    }
}
