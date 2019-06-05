package org.talend.daikon.logging.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Test;
import org.talend.daikon.logging.event.field.LayoutFields;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class Log4jJSONLayoutTest extends AbstractLayoutTest {

    @Test
    public void testDefaultLocationInfo() {
        Log4jJSONLayout layout = new Log4jJSONLayout();
        assertFalse(layout.getLocationInfo());
    }

    @Test
    public void testMDCMetaFields() {
        final String customFieldKey = "custom.info.field";
        final String customFieldValue = "customFieldValue";
        final String metaFieldKey = "meta.data.field";
        final String metaFieldKeyValue = "metaFieldValue";
        final String processedMetaFieldKey = "some.meta";

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put(customFieldKey, customFieldValue);
        mdc.put(metaFieldKey, metaFieldKeyValue);

        LogDetails logDetails = new LogDetails(this.getClass());
        logDetails.setMdc(mdc);
        LoggingEvent event = newEvent(logDetails);

        Map<String, String> metaFields = new LinkedHashMap<>();
        metaFields.put(metaFieldKey, processedMetaFieldKey);

        Log4jJSONLayout layout = new Log4jJSONLayout();
        layout.setMetaFields(metaFields);

        String result = layout.format(event);

        // ------------------------------------------

        JSONObject resultJson = JSONValue.parse(result, JSONObject.class);

        assertFalse(resultJson.containsKey(metaFieldKey));
        assertFalse(resultJson.containsKey(customFieldKey));
        assertTrue(resultJson.containsKey(processedMetaFieldKey));
        assertEquals(metaFieldKeyValue, resultJson.get(processedMetaFieldKey));

        JSONObject customInfo = (JSONObject) resultJson.get(LayoutFields.CUSTOM_INFO);
        assertFalse(customInfo.containsKey(metaFieldKey));
        assertFalse(customInfo.containsKey(processedMetaFieldKey));
        assertTrue(customInfo.containsKey(customFieldKey));
        assertEquals(customFieldValue, customInfo.get(customFieldKey));
    }

    @Override
    protected LoggingEvent newEvent(LogDetails logDetails) {
        LocationInfo locationInfo = new LocationInfo(logDetails.getFileName(), logDetails.getClassName(),
                logDetails.getMethodName(), String.valueOf(logDetails.getLineNumber()));
        Logger logger = Logger.getLogger(logDetails.getClassName());
        ThrowableInformation throwableInformation = logDetails.getException() != null
                ? new ThrowableInformation(logDetails.getException())
                : null;
        Properties mdc = new Properties();
        logDetails.getMdc().forEach(mdc::put);
        return new LoggingEvent(logDetails.getClassName(), logger, logDetails.getTimeMillis(),
                Level.toLevel(logDetails.getSeverity()), logDetails.getLogMessage(), logDetails.getThreadName(),
                throwableInformation, null, locationInfo, mdc);
    }

    @Override
    protected String log(Object event, LogDetails logDetails) {
        Log4jJSONLayout layout = new Log4jJSONLayout();
        layout.setLocationInfo(logDetails.isLocationInfo());
        return layout.format((LoggingEvent) event);
    }
}
