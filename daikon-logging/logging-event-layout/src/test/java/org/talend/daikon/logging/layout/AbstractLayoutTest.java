package org.talend.daikon.logging.layout;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Marker;
import org.talend.daikon.logging.ecs.EcsFieldsChecker;
import org.talend.daikon.logging.ecs.MdcEcsMapper;

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
    public void testMdc() {
        logDetails.getMdc().put("mdc_field_1", "my value 1");
        logDetails.getMdc().put("ecs.field.second", "my value 2");
        logDetails.getMdc().put("labels.my_awesome_label", "my value 3");
        logDetails.getMdc().put("unknown_field", "my value 4");
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

        assertThat(payload, hasJsonPath("$.['@timestamp']", equalTo(
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(getLogDetails().getTimeMillis()), ZoneOffset.UTC).toString())));
        assertThat(payload, hasJsonPath("$.['ecs.version']", equalTo("4.2.0")));
        assertThat(payload, hasJsonPath("$.['log.level']", equalTo(getLogDetails().getSeverity())));
        assertThat(payload, hasJsonPath("$.['message']", equalTo(getLogDetails().getLogMessage())));
        assertThat(payload, hasJsonPath("$.['process.thread.name']", equalTo(getLogDetails().getThreadName())));
        assertThat(payload, hasJsonPath("$.['log.logger']", equalTo(getLogDetails().getClassName())));
        assertThat(payload, hasJsonPath("$.['host.ip']", not(empty())));
        assertThat(payload, hasJsonPath("$.['host.hostname']", not(empty())));

        if (logDetails.isLocationInfo()) {
            assertThat(payload, hasJsonPath("$.['log'].['origin'].['file'].['name']", equalTo(getLogDetails().getFileName())));
            assertThat(payload, hasJsonPath("$.['log'].['origin'].['function']", equalTo(getLogDetails().getMethodName())));
            assertThat(payload, hasJsonPath("$.['log'].['origin'].['file'].['line']", not(empty())));
        } else {
            assertThat(payload, hasNoJsonPath("$.['log'].['origin'].['file'].['name']"));
            assertThat(payload, hasNoJsonPath("$.['log'].['origin'].['function']"));
            assertThat(payload, hasNoJsonPath("$.['log'].['origin'].['file'].['line']"));
        }
        if (!logDetails.getMdc().isEmpty() || !additionalUserFields().isEmpty()) {
            logDetails.getMdc().entrySet().forEach(it -> assertThat(payload,
                    EcsFieldsChecker.isECSField(MdcEcsMapper.map(it.getKey()))
                            ? hasJsonPath(String.format("$.['%s']", MdcEcsMapper.map(it.getKey())), equalTo(it.getValue()))
                            : hasNoJsonPath(String.format("$.['%s']", MdcEcsMapper.map(it.getKey())))));
            additionalUserFields().entrySet().forEach(it -> assertThat(payload,
                    EcsFieldsChecker.isECSField(MdcEcsMapper.map(it.getKey()))
                            ? hasJsonPath(String.format("$.['%s']", MdcEcsMapper.map(it.getKey())), equalTo(it.getValue()))
                            : hasNoJsonPath(String.format("$.['%s']", MdcEcsMapper.map(it.getKey())))));
        }
        if (logDetails.getException() != null) {
            assertThat(payload, hasJsonPath("$.['error.type']", equalTo(logDetails.getException().getClass().getName())));
            assertThat(payload, hasJsonPath("$.['error.stack_trace']", containsString(logDetails.getException().toString())));
            assertThat(payload, hasJsonPath("$.['error.message']", containsString(logDetails.getException().getMessage())));
        } else {
            assertThat(payload, hasNoJsonPath("$.['error.type']"));
            assertThat(payload, hasNoJsonPath("$.['error.stack_trace']"));
            assertThat(payload, hasNoJsonPath("$.['error.message']"));
        }
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
