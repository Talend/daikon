package org.talend.daikon.logging.layout;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.StringMap;
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Log4j2JSONLayoutTest extends AbstractLayoutTest {

    // /!\/!\/!\/!\/!\/!\/!\
    // do not remove this line - it has the side effect of initializing log4j2.xml contents
    //
    // However the user defined attributes as KeyValuePair are per-instance basis.
    // This means that when calling the `createLayout` method the properties should be
    // passed otherwise the user variables are not taken into account
    private static final Logger LOGGER = LogManager.getLogger(Log4j2JSONLayoutTest.class);

    @Override
    protected String log(Object event, LogDetails logDetails) {
        KeyValuePair[] scimAdditionalProps = new KeyValuePair[4];
        scimAdditionalProps[0] = new KeyValuePair("mdc_field_1", "my value 1");
        scimAdditionalProps[1] = new KeyValuePair("ecs.field.second", "my value 2");
        scimAdditionalProps[2] = new KeyValuePair("labels.my_awesome_label", "my value 3");
        scimAdditionalProps[3] = new KeyValuePair("unknown_field", "my value 4");

        AbstractStringLayout myLayout = Log4j2JSONLayout.newBuilder().setLocationInfo(logDetails.isLocationInfo())
                .setHostInfo(true).setAdditionalFields(scimAdditionalProps).setServiceName("my_service").build();

        return myLayout.toSerializable((LogEvent) event);
    }

    @Override
    protected Map<String, String> additionalUserFields() {
        Map<String, String> additionalFields = new HashMap<>();
        additionalFields.put("mdc_field_1", "my value 1");
        additionalFields.put("ecs.field.second", "my value 2");
        additionalFields.put("labels.my_awesome_label", "my value 3");
        additionalFields.put("unknown_field", "my value 4");
        return additionalFields;
    }

    @Override
    protected Object newEvent(LogDetails logDetails) {
        final Message message = new SimpleMessage(logDetails.getLogMessage());
        final StringMap contextData;
        if (!logDetails.getMdc().isEmpty()) {
            contextData = ContextDataFactory.createContextData();
            logDetails.getMdc().forEach(contextData::putValue);
        } else {
            contextData = null;
        }
        Log4jLogEvent.Builder builder = Log4jLogEvent.newBuilder().setLoggerName(logDetails.getClassName())
                .setTimeMillis(logDetails.getTimeMillis()).setLevel(Level.DEBUG).setContextData(contextData)
                .setIncludeLocation(logDetails.isLocationInfo()).setLoggerFqcn(logDetails.getClassName()).setMessage(message);
        if (logDetails.isLocationInfo()) {
            builder.setSource(new StackTraceElement(logDetails.getClassName(), logDetails.getMethodName(),
                    logDetails.getFileName(), logDetails.getLineNumber()));
        }
        if (logDetails.getException() != null) {
            builder.setThrown(logDetails.getException());
        }
        return builder.build();
    }
}
