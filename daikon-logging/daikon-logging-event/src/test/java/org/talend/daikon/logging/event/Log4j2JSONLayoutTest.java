package org.talend.daikon.logging.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.nio.charset.Charset;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Test;

public class Log4j2JSONLayoutTest {

    static final Logger LOGGER = LogManager.getLogger(Log4j2JSONLayoutTest.class);

    static final String EXPECTED_BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"@timestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"loggerName\":\"org.talend.daikon.logging.event.Log4j2JSONLayoutTest\"," + "\"logMessage\":\"Test Message\","
            + "\"fileName\":\"org.talend.daikon.logging.event.Log4j2JSONLayoutTest\"," + "}";

    @Test
    public void basicSimpleTest() {
        final Message message = new SimpleMessage("Test Message");
        final LogEvent event = Log4jLogEvent.newBuilder().setLoggerName(LOGGER.getName()).setLevel(Level.DEBUG)
                .setLoggerFqcn("org.talend.daikon.logging.event.Log4j2JSONLayoutTest").setMessage(message).build();

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
    public void testThrowableFull() {
        final String[] options = { "full" };
        final ThrowablePatternConverter converter = ThrowablePatternConverter.newInstance(options);
        Throwable parent;
        try {
            try {
                throw new NullPointerException("null pointer");
            } catch (final NullPointerException e) {
                throw new IllegalArgumentException("IllegalArgument", e);
            }
        } catch (final IllegalArgumentException e) {
            parent = e;
        }
        final LogEvent event = Log4jLogEvent.newBuilder() //
                .setLoggerName("testLogger") //
                .setLoggerFqcn(this.getClass().getName()) //
                .setLevel(Level.DEBUG) //
                .setMessage(new SimpleMessage("test exception")) //
                .setThrown(parent).build();
        final StringBuilder sb = new StringBuilder();
        converter.format(event, sb);
        final String result = sb.toString();
        //System.out.print(result);
        assertTrue("Incorrect start of msg", result.startsWith("java.lang.IllegalArgumentException: IllegalArgument"));
        assertTrue("Missing nested exception", result.contains("java.lang.NullPointerException: null pointer"));
    }

}
