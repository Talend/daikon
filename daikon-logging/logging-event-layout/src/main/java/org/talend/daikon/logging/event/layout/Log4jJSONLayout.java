package org.talend.daikon.logging.event.layout;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.talend.daikon.logging.ecs.EcsSerializer;
import org.talend.daikon.logging.event.field.HostData;

import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;

/**
 * Log4j ECS JSON layout
 */
@Deprecated
public class Log4jJSONLayout extends Layout {

    private boolean locationInfo;

    private boolean hostInfo;

    private boolean addEventUuid;

    private String serviceName;

    private List<AdditionalField> additionalFields = new ArrayList<AdditionalField>();

    /**
     * Print no location info by default, but print host information (for backward compatibility).
     */
    public Log4jJSONLayout() {
        this(false, true, true);
    }

    /**
     * Creates a layout that optionally inserts location information into log messages.
     *
     * @param locationInfo whether or not to include location information in the log messages.
     * @param hostInfo whether or not to include host information (host name and IP address) in the log messages.
     */
    public Log4jJSONLayout(boolean locationInfo, boolean hostInfo, boolean addEventUuid) {
        this.locationInfo = locationInfo;
        this.hostInfo = hostInfo;
        this.addEventUuid = addEventUuid;
    }

    public boolean isLocationInfo() {
        return locationInfo;
    }

    public boolean isHostInfo() {
        return hostInfo;
    }

    public boolean isAddEventUuid() {
        return addEventUuid;
    }

    public void setAddEventUuid(boolean addEventUuid) {
        this.addEventUuid = addEventUuid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<AdditionalField> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(List<AdditionalField> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    public void setHostInfo(boolean hostInfo) {
        this.hostInfo = hostInfo;
    }

    public void setMetaFields(Map<String, String> metaFields) {
        setAdditionalFields(metaFields.entrySet().stream().map(e -> new AdditionalField(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public String format(LoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        EcsJsonSerializer.serializeObjectStart(builder, event.getTimeStamp());
        EcsJsonSerializer.serializeLogLevel(builder, event.getLevel().toString());
        EcsJsonSerializer.serializeFormattedMessage(builder, event.getRenderedMessage());
        EcsSerializer.serializeEcsVersion(builder);
        EcsJsonSerializer.serializeServiceName(builder, serviceName);
        EcsJsonSerializer.serializeThreadName(builder, event.getThreadName());
        EcsJsonSerializer.serializeLoggerName(builder, event.getLoggerName());

        // Call custom serializer for additional fields & MDC (for mapping and filtering)
        EcsSerializer.serializeAdditionalFields(builder, additionalFields);
        EcsSerializer.serializeMDC(builder, event.getProperties());

        if (this.hostInfo) {
            EcsSerializer.serializeHostInfo(builder, new HostData());
        }

        if (this.addEventUuid) {
            EcsSerializer.serializeEventId(builder, UUID.randomUUID());
        }

        if (this.locationInfo) {
            LocationInfo locationInformation = event.getLocationInformation();
            if (locationInformation != null) {
                EcsJsonSerializer.serializeOrigin(builder, locationInformation.getFileName(), locationInformation.getMethodName(),
                        getLineNumber(locationInformation));
            }
        }
        ThrowableInformation throwableInformation = event.getThrowableInformation();
        if (throwableInformation != null) {
            EcsJsonSerializer.serializeException(builder, throwableInformation.getThrowable(), false);
        }
        EcsJsonSerializer.serializeObjectEnd(builder);
        return builder.toString();
    }

    private static int getLineNumber(LocationInfo locationInformation) {
        int lineNumber = -1;
        String lineNumberString = locationInformation.getLineNumber();
        if (!LocationInfo.NA.equals(lineNumberString)) {
            try {
                lineNumber = Integer.parseInt(lineNumberString);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return lineNumber;
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {

    }
}
