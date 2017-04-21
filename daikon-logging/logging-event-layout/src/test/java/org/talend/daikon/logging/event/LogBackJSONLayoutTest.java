package org.talend.daikon.logging.event;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.layout.Log4jJSONLayout;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class LogBackJSONLayoutTest {

    static final Logger LOGGER = LoggerFactory.getLogger("LogBackJSONLayoutTest.class");

    static final String EXPECTED_BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"logTimestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"loggerName\":\"org.talend.daikon.logging.event.LogBackJSONLayoutTest\"," + "\"logMessage\":\"Test Message\","
            + "\"fileName\":\"org.talend.daikon.logging.event.LogBackJSONLayoutTest\"," + "\"foo\":\"bar\"}";

    @Test
    public void testJSONEventLayoutHasMDC() {
        MDC.put("foo", "bar");
        LOGGER.warn("I should have MDC data in my log");
        //String message = appender.getMessages()[0];
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;

        assertEquals("MDC is wrong", "bar", jsonObject.get("foo"));
    }

    @Test
    public void testDateFormat() {
        long timestamp = 1364844991207L;
        assertEquals("format does not produce expected output", "2013-04-01T19:36:31.207Z",
                Log4jJSONLayout.dateFormat(timestamp));
    }
}
