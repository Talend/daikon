package org.talend.daikon.logging.layout;

import java.nio.charset.Charset;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.StringMap;
import org.junit.Test;
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;

public class Log4j2JSONLayoutTest {

    static final Logger LOGGER = LogManager.getLogger(Log4j2JSONLayoutTest.class);

    static final String EXPECTED_BASIC_SIMPLE_JSON_TEST = "{\"@version\":1," +
    // "\"@timestamp\":\"2015-07-28T11:31:18.492-07:00\",\"timeMillis\":1438108278492," +
            "\"threadName\":\"" + Thread.currentThread().getName() + "\"," + "\"severity\":\"DEBUG\","
            + "\"logMessage\":\"Test Message\"," + "\"logSource\":{} }";

    @Test
    public void basicSimpleTest() {
        final Message message = new SimpleMessage("Test Message");
        final StringMap contextData = ContextDataFactory.createContextData();
        contextData.putValue("service", "log4j2");
        final LogEvent event = Log4jLogEvent.newBuilder().setLoggerName(LOGGER.getName()).setLevel(Level.DEBUG)
                .setContextData(contextData).setIncludeLocation(true)
                .setLoggerFqcn("org.talend.logging.event.Log4j2JSONLayoutTest").setMessage(message).build();

        AbstractStringLayout layout = Log4j2JSONLayout.createLayout(true, //location
                true, //properties
                true, //complete
                true, //compact
                false, //eventEol
                Charset.defaultCharset(), null);
        //MDC.put("spanId", "123456789"); work only when log4j-slf4j-impl is added as dependencies
        LOGGER.debug("I shouldn't have MDC data in my log");
        String actualJSON = layout.toSerializable(event);
        assertThat(actualJSON,
                sameJSONAs(EXPECTED_BASIC_SIMPLE_JSON_TEST).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

    }
}
