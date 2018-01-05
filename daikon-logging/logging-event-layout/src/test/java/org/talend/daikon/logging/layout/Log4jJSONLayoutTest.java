package org.talend.daikon.logging.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.minidev.json.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Test;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;

public class Log4jJSONLayoutTest extends AbstractLayoutTest {

    static final Logger LOGGER = Logger.getRootLogger();

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

        final String userFieldKey = "user";
        final String userFieldKeyValue = "user0";
        final String operationFieldKey = "operation";
        final String operationFieldKeyValue = "create user";
        final String resultFieldKey = "result";
        final String resultFieldKeyValue = "success";

        Map<String, String> mdc = new LinkedHashMap<>();
        mdc.put(customFieldKey, customFieldValue);
        mdc.put(metaFieldKey, metaFieldKeyValue);
        mdc.put(userFieldKey, userFieldKeyValue);
        mdc.put(operationFieldKey, operationFieldKeyValue);
        mdc.put(resultFieldKey, resultFieldKeyValue);

        LogDetails logDetails = new LogDetails(this.getClass());
        logDetails.setMdc(mdc);
        LoggingEvent event = newEvent(logDetails);

        Map<String, String> metaFields = new LinkedHashMap<>();
        metaFields.put(metaFieldKey, processedMetaFieldKey);
        metaFields.put(userFieldKey, userFieldKeyValue);
        metaFields.put(operationFieldKey, operationFieldKeyValue);
        metaFields.put(resultFieldKey, resultFieldKeyValue);
        Log4jJSONLayout layout = new Log4jJSONLayout() {

            @Override
            protected Map<String, String> processMDCMetaFields(LoggingEvent loggingEvent, JSONObject logstashEvent,
                    Map<String, String> metaFields) {
                Map<String, String> newMdc = super.processMDCMetaFields(loggingEvent, logstashEvent, metaFields);
                assertFalse(newMdc.containsKey(metaFieldKey));
                assertFalse(newMdc.containsKey(processedMetaFieldKey));
                assertEquals(customFieldValue, newMdc.get(customFieldKey));

                assertFalse(logstashEvent.containsKey(metaFieldKey));
                assertFalse(logstashEvent.containsKey(customFieldKey));
                assertEquals(metaFieldKeyValue, logstashEvent.getAsString(processedMetaFieldKey));
                assertFalse(logstashEvent.containsKey(userFieldKey));
                assertFalse(logstashEvent.containsKey(operationFieldKey));
                assertFalse(logstashEvent.containsKey(resultFieldKey));

                return newMdc;
            }
        };
        layout.setMetaFields(metaFields);

        String result = layout.format(event);

        assertTrue(result.contains(processedMetaFieldKey));
    }

    @Override
    protected LoggingEvent newEvent(LogDetails logDetails) {
        LocationInfo locationInfo = new LocationInfo(logDetails.getFileName(), logDetails.getClassName(),
                logDetails.getMethodName(), String.valueOf(logDetails.getLineNumber()));
        Logger logger = Logger.getLogger(logDetails.getClassName());
        ThrowableInformation throwableInformation = logDetails.getException() != null
                ? new ThrowableInformation(logDetails.getException()) : null;
        Properties mdc = new Properties();
        logDetails.getMdc().entrySet().stream().forEach(it -> mdc.put(it.getKey(), it.getValue()));
        LoggingEvent event = new LoggingEvent(logDetails.getClassName(), logger, logDetails.getTimeMillis(),
                Level.toLevel(logDetails.getSeverity()), logDetails.getLogMessage(), logDetails.getThreadName(),
                throwableInformation, null, locationInfo, mdc);
        return event;
    }

    @Override
    protected String log(Object event, LogDetails logDetails) {
        Log4jJSONLayout layout = new Log4jJSONLayout();
        layout.setLocationInfo(logDetails.isLocationInfo());
        return layout.format((LoggingEvent) event);
    }
}
