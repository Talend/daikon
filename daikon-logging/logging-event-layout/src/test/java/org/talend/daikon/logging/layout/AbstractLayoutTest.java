package org.talend.daikon.logging.layout;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Marker;
import org.talend.daikon.logging.event.field.LayoutFields;
import org.talend.daikon.logging.event.layout.LayoutUtils;

/**
 * @author agonzalez
 */
public abstract class AbstractLayoutTest {

    private LogDetails logDetails;

    @Before
    public void setUp() {
        this.logDetails = new LogDetails(this.getClass());
    }

    @Test
    public void testSimple() {
        final Object event = newEvent(logDetails);
        String payload = log(event, logDetails);
        assertPayload(payload);
    }

    /**
     * Test LogEvent logging with a non-null source attribute
     */
    @Test
    public void testLogSource() {
        logDetails.setLocationInfo(true);
        final Object event = newEvent(logDetails);
        String payload = log(event, logDetails);
        assertPayload(payload);
    }

    /**
     * Test LogEvent logging with a non-null source attribute
     */
    @Test
    public void testLogSleuthField() {
        logDetails.getMdc().put("service", "log4j2");
        logDetails.getMdc().put("X-B3-SpanId", "testSpan");
        logDetails.getMdc().put("X-B3-TraceId", "testTrace");
        logDetails.getMdc().put("X-Span-Export", "testExport");
        final Object event = newEvent(logDetails);
        String payload = log(event, logDetails);
        assertPayload(payload);
    }

    /**
     * Test LogEvent logging with a non-null source attribute
     */
    @Test
    public void testMdc() {
        logDetails.getMdc().put("customMDC", "someMDC");
        final Object event = newEvent(logDetails);
        String payload = log(event, logDetails);
        assertPayload(payload);
    }

    /**
     * Test LogEvent logging with a non-null source attribute
     */
    @Test
    public void testException() {
        logDetails.setException(new NullPointerException("My precious NullPointerException"));
        final Object event = newEvent(logDetails);
        String payload = log(event, logDetails);
        assertPayload(payload);
    }

    // protected abstract Object newLayout();

    protected abstract Object newEvent(LogDetails logDetails);

    /**
     * Returns the additional fields set in the log configuration.
     *
     * Default implementation returns empty.
     */
    protected Map<String, String> additionalUserFields() {
        return Collections.emptyMap();
    }

    protected void assertPayload(String payload) {
        assertThat(payload, hasJsonPath("$.@version", equalTo(1)));
        assertThat(payload, hasJsonPath("$.logTimestamp", equalTo(dateFormat(getLogDetails().getTimeMillis()))));
        assertThat(payload, hasJsonPath("$.agentTimestamp", not(empty())));
        assertThat(payload, hasJsonPath("$.threadName", equalTo(getLogDetails().getThreadName())));
        assertThat(payload, hasJsonPath("$.severity", equalTo(getLogDetails().getSeverity())));
        assertThat(payload, hasJsonPath("$.logMessage", equalTo(getLogDetails().getLogMessage())));
        assertThat(payload, hasJsonPath("$.logSource['logger.name']", equalTo(getLogDetails().getClassName())));
        assertThat(payload, hasJsonPath("$.logSource['host.address']", not(empty())));
        assertThat(payload, hasJsonPath("$.logSource['host.name']", not(empty())));
        if (logDetails.isLocationInfo()) {
            assertThat(payload, hasJsonPath("$.logSource['file.name']", equalTo(getLogDetails().getFileName())));
            assertThat(payload, hasJsonPath("$.logSource['method.name']", equalTo(getLogDetails().getMethodName())));
            assertThat(payload, hasJsonPath("$.logSource['line.number']", not(empty())));
            assertThat(payload, hasJsonPath("$.logSource['process.id']", not(empty())));
            assertThat(payload, hasJsonPath("$.logSource['class.name']", equalTo(getLogDetails().getClassName())));
        } else {
            assertThat(payload, hasNoJsonPath("$.logSource['file.name']"));
            assertThat(payload, hasNoJsonPath("$.logSource['method.name']"));
            assertThat(payload, hasNoJsonPath("$.logSource['line.number']"));
            assertThat(payload, hasNoJsonPath("$.logSource['process.id']"));
            assertThat(payload, hasNoJsonPath("$.logSource['class.name']"));
        }
        if (!logDetails.getMdc().isEmpty() || !additionalUserFields().isEmpty()) {
            logDetails.getMdc().entrySet().forEach(it -> {
                if (LayoutUtils.isSleuthField(it.getKey())) {
                    assertThat(payload, hasJsonPath(String.format("$['%s']", it.getKey()), equalTo(it.getValue())));
                } else {
                    assertThat(payload, hasJsonPath(String.format("$.customInfo['%s']", it.getKey()), equalTo(it.getValue())));
                }
            });
            additionalUserFields().entrySet().forEach(it -> {
                assertThat(payload, hasJsonPath(String.format("$.customInfo['%s']", it.getKey()), equalTo(it.getValue())));
            });
        } else {
            assertThat(payload, hasNoJsonPath("$.customInfo"));
        }
        if (logDetails.getException() != null) {
            assertThat(payload, hasJsonPath("$.exceptionClass", equalTo(logDetails.getException().getClass().getName())));
            assertThat(payload, hasJsonPath("$.stackTrace", containsString(logDetails.getException().toString())));
            assertThat(payload, hasJsonPath("$.exceptionMessage", containsString(logDetails.getException().getMessage())));
        } else {
            assertThat(payload, hasNoJsonPath("$.exceptionClass"));
            assertThat(payload, hasNoJsonPath("$.stackTrace"));
            assertThat(payload, hasNoJsonPath("$.exceptionMessage"));
        }
    }

    private String dateFormat(long timestamp) {
        return LayoutFields.DATETIME_TIME_FORMAT.format(timestamp);
    }

    private LogDetails getLogDetails() {
        return logDetails;
    }

    protected abstract String log(Object event, LogDetails logDetails);

    protected static class LogDetails {

        private boolean locationInfo;

        private String methodName;

        private String className;

        private String fileName;

        private String severity;

        private String logMessage;

        private String threadName;

        private int lineNumber;

        private long timeMillis;

        private Exception exception;

        private Map<String, String> mdc = new HashMap<>();

        private Marker marker;

        public LogDetails(Class clazz) {
            this.className = clazz.getName();
            this.methodName = "newEvent";
            this.severity = "DEBUG";
            this.fileName = clazz.getSimpleName() + ".java";
            this.logMessage = "Test Message";
            this.threadName = Thread.currentThread().getName();
            this.lineNumber = 10;
            this.timeMillis = System.currentTimeMillis();
        }

        public long getTimeMillis() {
            return timeMillis;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public boolean isLocationInfo() {
            return locationInfo;
        }

        public void setLocationInfo(boolean locationInfo) {
            this.locationInfo = locationInfo;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getLogMessage() {
            return logMessage;
        }

        public void setLogMessage(String logMessage) {
            this.logMessage = logMessage;
        }

        public Map<String, String> getMdc() {
            return mdc;
        }

        public void setMdc(Map<String, String> mdc) {
            this.mdc = mdc;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }

}
