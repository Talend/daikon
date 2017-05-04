package org.talend.daikon.logging.layout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.nio.charset.Charset;

import org.apache.log4j.MDC;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Test;
import org.talend.daikon.logging.event.field.LayoutFields;
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class Log4j2JSONLayoutTest {

    static final Logger LOGGER = LogManager.getLogger(Log4j2JSONLayoutTest.class);

    static final String EXPECTED_BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"@timestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"logMessage\":\"Test Message\"," + "\"logSource\":{\"logger.name\":\"testLogger\"" + "} }";

    static final String BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"@timestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"logMessage\":\"Test Message\","
            + "\"logSource\":{\"logger.name\":\"org.talend.daikon.logging.layout.Log4j2JSONLayoutTest\","
            + "\"file.name\":\"org.talend.daikon.logging.layout.Log4j2JSONLayoutTest\"}" + "," + "\"foo\":\"bar\"}";

    @Test
    public void basicSimpleTest() {
        final LogEvent event = Log4jLogEvent.newBuilder() //
                .setLoggerName("testLogger") //
                .setIncludeLocation(true).setLoggerFqcn(this.getClass().getName()) //
                .setLevel(Level.DEBUG) //
                .setMessage(new SimpleMessage("Test Message")).build();

        AbstractStringLayout layout = Log4j2JSONLayout.createLayout(true, //location
                true, //properties
                true, //complete
                true, //compact
                false, //eventEol
                Charset.defaultCharset(), null);
        LOGGER.warn("I shouldn't have MDC data in my log");
        String actualJSON = layout.toSerializable(event);
        System.out.println(actualJSON);
        assertThat(actualJSON,
                sameJSONAs(EXPECTED_BASIC_SIMPLE_JSON_TEST).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

    }

    @Test
    public void testJSONEventLayoutIsJSON() {
        LOGGER.info("this is an info message");
        assertTrue("Event is not valid JSON", JSONValue.isValidJsonStrict(BASIC_SIMPLE_JSON_TEST));
    }

    @Test
    public void testJSONEventLayoutHasMDC() {
        MDC.put("foo", "bar");
        MDC.put("service", "log4j2");
        LOGGER.warn("I should have MDC data in my log");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        assertEquals("MDC is wrong", "bar", jsonObject.get("foo"));
    }

    @Test
    public void testJSONEventLayoutHasNestedMDC() {
        MDC.put("foo", "bar");
        LOGGER.warn("I should have nested MDC data in my log");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        assertTrue("Event is missing foo key", jsonObject.containsKey("foo"));
    }

    @Test
    public void testJSONEventLayoutHasClassName() {
        LOGGER.warn("warning dawg");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertEquals("Logged class does not match", this.getClass().getCanonicalName().toString(), logSource.get("logger.name"));
    }

    @Test
    public void testJSONEventHasFileName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertNotNull("File value is missing", logSource.get("file.name"));
    }

    @Test
    public void testJSONEventHasLoggerName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject logSource = (JSONObject) jsonObject.get("logSource");
        assertNotNull("LoggerName value is missing", logSource.get("logger.name"));
    }

    @Test
    public void testJSONEventHasThreadName() {
        LOGGER.warn("whoami");
        Object obj = JSONValue.parse(BASIC_SIMPLE_JSON_TEST);
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
