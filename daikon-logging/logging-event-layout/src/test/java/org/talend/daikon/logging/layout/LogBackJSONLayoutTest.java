package org.talend.daikon.logging.layout;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.logging.event.layout.LogbackJSONLayout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public class LogBackJSONLayoutTest extends AbstractLayoutTest {

    static final Logger LOGGER = LoggerFactory.getLogger(LogBackJSONLayoutTest.class);

    @Test
    public void testDefaultLocationInfo() {
        LogbackJSONLayout layout = new LogbackJSONLayout();
        assertFalse(layout.getLocationInfo());
    }

    @Override
    protected Object newEvent(LogDetails logDetails) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(logDetails.getClassName());
        LoggingEvent event = new LoggingEvent(this.getClass().getName(), logger, Level.DEBUG, logDetails.getLogMessage(),
                logDetails.getException(), null);
        event.setThreadName(logDetails.getThreadName());
        event.setTimeStamp(logDetails.getTimeMillis());
        event.setMDCPropertyMap(logDetails.getMdc());
        StackTraceElement callerData = new StackTraceElement(logDetails.getClassName(), logDetails.getMethodName(),
                logDetails.getFileName(), logDetails.getLineNumber());
        event.setCallerData(new StackTraceElement[] { callerData });
        return event;
    }

    @Override
    protected String log(Object event, LogDetails logDetails) {
        LogbackJSONLayout layout = new LogbackJSONLayout();
        layout.setLocationInfo(logDetails.isLocationInfo());
        layout.start();
        try {
            return layout.doLayout((LoggingEvent) event);
        } finally {
            layout.stop();
        }
    }
}
