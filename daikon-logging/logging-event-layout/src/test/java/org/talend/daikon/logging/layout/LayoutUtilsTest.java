package org.talend.daikon.logging.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONObject;
import org.junit.Test;
import org.talend.daikon.logging.event.layout.LayoutUtils;

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
}
