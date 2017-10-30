package org.talend.daikon.logging.layout;

import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

    @Override
    protected Object newEvent(LogDetails logDetails) {
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
