package org.talend.daikon.logging.event.layout;

import java.util.*;

import org.slf4j.Marker;
import org.talend.daikon.logging.event.field.LayoutFields;

import net.minidev.json.JSONObject;
import org.talend.daikon.logging.event.field.MdcKeys;

/**
 * Json Layout Utils
 *
 * @author sdiallo
 */
public final class LayoutUtils {

    /**
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
     * @param timestamp
     * @return
     */
    public static String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

    /**
     * @param data
     * @param userFieldsEvent
     */
    public static void addUserFields(String data, JSONObject userFieldsEvent) {
        if (null != data) {
            String[] pairs = data.split(",");
            for (String pair : pairs) {
                String[] userField = pair.split(":", 2);
                if (userField.length == 2 && userField[0] != null) {
                    String key = userField[0];
                    String val = userField[1];
                    userFieldsEvent.put(key, val);
                }
            }
        }
    }

    /**
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

    /**
     * Find the marker that has the 'LayoutFields.CUSTOM_INFO' name in a marker tree.
     *
     * @param marker the root marker of the tree, or subtree
     * @param visited a collection of already visited markers, used to avoid an infinite loop
     * @return the marker if found, otherwise `null`
     */
    public static Marker findCustomFieldsMarker(Marker marker, Set<Marker> visited) {
        if (marker == null || visited.contains(marker)) {
            // The already visited marker case is there to secure a potential infinite loop. A marker M1 might reference
            // another marker M2, which would reference M1. It's in theory because, at the time of writing, the markers
            // API already prevents that case.
            return null;
        } else if (LayoutFields.CUSTOM_INFO.equals(marker.getName())) {
            return marker;
        } else {
            visited.add(marker);
            Iterator<Marker> children = marker.iterator();
            while (children.hasNext()) {
                Marker foundMarker = findCustomFieldsMarker(children.next(), visited);
                if (foundMarker != null)
                    return foundMarker;
            }
        }
        return null;
    }

    private LayoutUtils() {
        // not to be instantiated
    }
}
