package org.talend.daikon.logging.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONObject;
import org.junit.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.talend.daikon.logging.event.field.LayoutFields;
import org.talend.daikon.logging.event.layout.LayoutUtils;

import static org.junit.Assert.*;

/**
 *
 */
public class LayoutUtilsTest {

    @Test
    public void testProcessMDCMetaFields() {
        JSONObject logstashEvent = new JSONObject();

        Map<String, String> metaFields = new HashMap<>();
        metaFields.put("mdctest", "metatest");

        Map<String, String> existingMdc = new LinkedHashMap<>();
        existingMdc.put("mdctest", "metavalue");
        existingMdc.put("customtest", "customvalue");

        Map<String, String> mdc = LayoutUtils.processMDCMetaFields(existingMdc, logstashEvent, metaFields);

        assertEquals(1, mdc.size());
        assertEquals("customvalue", mdc.get("customtest"));

        assertEquals("metavalue", logstashEvent.get("metatest"));
        assertFalse(logstashEvent.containsKey("mdctest"));
        assertFalse(logstashEvent.containsKey("customtest"));
    }

    @Test
    public void testProcessMDCMetaFieldsNoMetaFields() {
        JSONObject logstashEvent = new JSONObject();

        Map<String, String> existingMdc = new LinkedHashMap<>();
        existingMdc.put("mdctest", "metavalue");
        existingMdc.put("customtest", "customvalue");

        Map<String, String> mdc = LayoutUtils.processMDCMetaFields(existingMdc, logstashEvent, null);

        assertEquals("metavalue", mdc.get("mdctest"));
        assertEquals("customvalue", mdc.get("customtest"));

        assertFalse(logstashEvent.containsKey("mdctest"));
        assertFalse(logstashEvent.containsKey("metatest"));
        assertFalse(logstashEvent.containsKey("customtest"));
    }

    @Test
    public void testFindCustomFieldsMarker_emptyTree() {
        assertNull(LayoutUtils.findCustomFieldsMarker(null, new HashSet<>()));
    }

    @Test
    public void testFindCustomFieldsMarker_markerNotFound() {
        assertNull(LayoutUtils.findCustomFieldsMarker(createMarker(null), new HashSet<>()));
    }

    @Test
    public void testFindCustomFieldsMarker_markerFound() {
        Marker customFieldsMarker = MarkerFactory.getMarker(LayoutFields.CUSTOM_INFO);
        assertEquals(customFieldsMarker, LayoutUtils.findCustomFieldsMarker(createMarker(customFieldsMarker), new HashSet<>()));
    }

    @Test
    public void testFindCustomFieldsMarker_markerNotFoundInfiniteLoop() {
        assertNull(LayoutUtils.findCustomFieldsMarker(createAutoReferencedMarker(), new HashSet<>()));
    }

    private Marker createMarker(Marker customMarker) {
        Marker level_0 = MarkerFactory.getDetachedMarker("level_0");
        Marker level_1_marker_1_from_0 = MarkerFactory.getDetachedMarker("level_1_marker_1_from_0");
        Marker level_1_marker_2_from_0 = MarkerFactory.getDetachedMarker("level_1_marker_2_from_0");
        Marker level_1_marker_3_from_0 = MarkerFactory.getDetachedMarker("level_1_marker_3_from_0");
        Marker level_2_marker_1_from_1 = MarkerFactory.getDetachedMarker("level_2_marker_1_from_1");
        Marker level_2_marker_1_from_2 = MarkerFactory.getDetachedMarker("level_2_marker_1_from_2");
        Marker level_2_marker_2_from_2 = customMarker != null ? customMarker
                : MarkerFactory.getDetachedMarker("level_2_marker_2_from_2");
        Marker level_2_marker_1_from_3 = MarkerFactory.getDetachedMarker("level_2_marker_1_from_3");
        level_0.add(level_1_marker_1_from_0);
        level_0.add(level_1_marker_2_from_0);
        level_0.add(level_1_marker_3_from_0);
        level_1_marker_1_from_0.add(level_2_marker_1_from_1);
        level_1_marker_2_from_0.add(level_2_marker_1_from_2);
        level_1_marker_2_from_0.add(level_2_marker_2_from_2);
        level_1_marker_3_from_0.add(level_2_marker_1_from_3);
        return level_0;
    }

    private Marker createAutoReferencedMarker() {
        Marker level_0 = MarkerFactory.getDetachedMarker("level_0");
        Marker level_1 = MarkerFactory.getDetachedMarker("level_1");
        Marker level_2 = MarkerFactory.getDetachedMarker("level_2");
        Marker level_3 = MarkerFactory.getDetachedMarker("level_3");
        level_0.add(level_1);
        level_1.add(level_2);
        level_2.add(level_0);
        level_2.add(level_3);
        return level_0;
    }
}
