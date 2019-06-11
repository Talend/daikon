package org.talend.daikon.logging.layout;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

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
import org.talend.daikon.logging.event.layout.Log4j2JSONLayout;

public class Log4j2JSONLayoutTest extends AbstractLayoutTest {

    // do not remove this line - it has the side effect of initializing log4j2.xml contents
    // aka user attributes before running the tests
    // useful to check that Log4j2JSONLayout handles userAttributes set in log4j2.xml.
    private static final Logger LOGGER = LogManager.getLogger(Log4j2JSONLayoutTest.class);

    @Override
    protected String log(Object event, LogDetails logDetails) {

        AbstractStringLayout layout = Log4j2JSONLayout.createLayout(logDetails.isLocationInfo(), // location
                true, true, true, true, false, Charset.defaultCharset(), null);

        return layout.toSerializable((LogEvent) event);
    }

    @Override
    protected Map<String, String> additionalUserFields() {
        return Collections.singletonMap("application_user", "SCIM");
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
