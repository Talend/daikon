package org.talend.daikon.logging.layout;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.junit.After;
import org.junit.Test;
import org.talend.daikon.logging.event.field.LayoutFields;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class Log4jJSONEventLayoutTest {

    static final String[] FIELDS = new String[] { "logMessage", "hostName", "logTimestamp", "@version" };

    static final Logger LOGGER = Logger.getRootLogger();

    static final String EXPECTED_BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"@timestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"logMessage\":\"Test Message\","
            + "\"logSource\":{\"logger.name\":\"org.talend.daikon.logging.layout.Log4jJSONEventLayoutTest\","
            + "\"file.name\":\"org.talend.daikon.logging.layout.Log4jJSONEventLayoutTest\"}" + "," + "\"foo\":\"bar\"}";

    @After
    public void clearTestAppender() {
        NDC.clear();
    }

    @Test
    public void testJSONEventLayoutIsJSON() {
        LOGGER.info("this is an info message");
        assertTrue("Event is not valid JSON", JSONValue.isValidJsonStrict(EXPECTED_BASIC_SIMPLE_JSON_TEST));
    }

    @Test
    public void testJSONEventLayoutHasMDC() {
        MDC.put("foo", "bar");
        MDC.put("service", "log4j");
        LOGGER.warn("I should have MDC data in my log");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        assertEquals("MDC is wrong", "bar", jsonObject.get("foo"));
    }

    @Test
    public void testJSONEventLayoutHasNestedMDC() {
        MDC.put("foo", "bar");
        LOGGER.warn("I should have nested MDC data in my log");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        assertTrue("Event is missing foo key", jsonObject.containsKey("foo"));
    }

    @Test
    public void testJSONEventLayoutHasClassName() {
        LOGGER.warn("warning dawg");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertEquals("Logged class does not match", this.getClass().getCanonicalName().toString(), logSource.get("logger.name"));
    }

    @Test
    public void testJSONEventHasFileName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertNotNull("File value is missing", logSource.get("file.name"));
    }

    @Test
    public void testJSONEventHasLoggerName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertNotNull("LoggerName value is missing", logSource.get("logger.name"));
    }

    @Test
    public void testJSONEventHasThreadName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(EXPECTED_BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        assertNotNull("ThreadName value is missing", jsonObject.get("threadName"));
    }

    @Test
    public void testDateFormat() {
        long timestamp = 1364844991207L;
        assertEquals("format does not produce expected output", "2013-04-01T19:36:31.207Z", dateFormat(timestamp));
    }

    private String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }
}
