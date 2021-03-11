package org.talend.daikon.logging.event.layout;

import java.util.*;

import org.slf4j.Marker;
import org.talend.daikon.logging.ecs.EcsSerializer;
import org.talend.daikon.logging.event.field.HostData;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.LayoutBase;

import co.elastic.logging.EcsJsonSerializer;
import co.elastic.logging.AdditionalField;

/**
 * Logback ECS JSON layout
 */
public class LogbackJSONLayout extends LayoutBase<ILoggingEvent> {

    private boolean locationInfo;

    private boolean hostInfo;

    private boolean addEventUuid;

    private ThrowableProxyConverter throwableProxyConverter;

    private String serviceName;

    private final List<AdditionalField> additionalFields = new ArrayList<>();

    /**
     * Print no location info by default, but print host information (for backward compatibility).
     */
    public LogbackJSONLayout() {
        this(false, true, true);
    }

    /**
     * Creates a layout that optionally inserts location information into log messages.
     *
     * @param locationInfo whether or not to include location information in the log messages.
     */
    public LogbackJSONLayout(boolean locationInfo, boolean hostInfo, boolean addEventUuid) {
        this.locationInfo = locationInfo;
        this.hostInfo = hostInfo;
        this.addEventUuid = addEventUuid;
    }

    public boolean isLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    public boolean isHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(boolean hostInfo) {
        this.hostInfo = hostInfo;
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

    public void addAdditionalField(AdditionalField pair) {
        this.additionalFields.add(pair);
    }

    public void setMetaFields(Map<String, String> metaFields) {
        metaFields.forEach((k, v) -> this.addAdditionalField(new AdditionalField(k, v)));
    }

    @Override
    public void start() {
        super.start();
        throwableProxyConverter = new ThrowableProxyConverter();
        throwableProxyConverter.start();
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        EcsJsonSerializer.serializeObjectStart(builder, event.getTimeStamp());
        EcsJsonSerializer.serializeLogLevel(builder, event.getLevel().toString());
        EcsJsonSerializer.serializeFormattedMessage(builder, event.getFormattedMessage());
        EcsSerializer.serializeEcsVersion(builder);
        serializeMarkers(builder, event);
        EcsJsonSerializer.serializeServiceName(builder, serviceName);
        EcsJsonSerializer.serializeThreadName(builder, event.getThreadName());
        EcsJsonSerializer.serializeLoggerName(builder, event.getLoggerName());

        // Serialize custom markers with format key:value
        serializeCustomMarkers(builder, event);

        // Call custom serializer for additional fields & MDC (for mapping and filtering)
        EcsSerializer.serializeAdditionalFields(builder, additionalFields);
        EcsSerializer.serializeMDC(builder, event.getMDCPropertyMap());

        if (this.hostInfo) {
            EcsSerializer.serializeHostInfo(builder, new HostData());
        }

        if (this.addEventUuid) {
            EcsSerializer.serializeEventId(builder, UUID.randomUUID());
        }

        if (this.locationInfo) {
            StackTraceElement[] callerData = event.getCallerData();
            if (callerData != null && callerData.length > 0) {
                EcsJsonSerializer.serializeOrigin(builder, callerData[0]);
            }
        }
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy instanceof ThrowableProxy) {
            EcsJsonSerializer.serializeException(builder, ((ThrowableProxy) throwableProxy).getThrowable(), false);
        } else if (throwableProxy != null) {
            EcsJsonSerializer.serializeException(builder, throwableProxy.getClassName(), throwableProxy.getMessage(),
                    throwableProxyConverter.convert(event), false);
        }
        EcsJsonSerializer.serializeObjectEnd(builder);
        return builder.toString();
    }

    private void serializeMarkers(StringBuilder builder, ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker != null) {
            EcsJsonSerializer.serializeTagStart(builder);
            serializeMarker(marker, builder);
            EcsJsonSerializer.serializeTagEnd(builder);
        }
    }

    private void serializeMarker(Marker marker, StringBuilder builder) {
        if (marker != null) {
            EcsJsonSerializer.serializeSingleTag(builder, marker.getName());
            Iterator<Marker> it = marker.iterator();
            while (it.hasNext()) {
                serializeMarker(it.next(), builder);
            }
        }
    }

    private void serializeCustomMarkers(StringBuilder builder, ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker != null) {
            serializeCustomMarker(marker, builder);
        }
    }

    private void serializeCustomMarker(Marker marker, StringBuilder builder) {
        if (marker != null) {
            EcsSerializer.serializeCustomMarker(builder, marker.getName());
            Iterator<Marker> it = marker.iterator();
            while (it.hasNext()) {
                serializeCustomMarker(it.next(), builder);
            }
        }
    }
}
