package org.talend.daikon.logging.ecs;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.talend.daikon.logging.config.LoggingProperties;
import org.talend.daikon.logging.event.field.HostData;

import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;

/**
 * Utility ECS serializer class
 */
public class EcsSerializer {

    private static String ECS_VERSION = LoggingProperties.get("ecs.version");

    /**
     * Serialize the additional fields (mapped and filtered)
     *
     * @param builder the builder to serialize in
     * @param additionalFields the additional fields to serialize
     */
    public static void serializeAdditionalFields(StringBuilder builder, List<AdditionalField> additionalFields) {
        EcsJsonSerializer.serializeAdditionalFields(builder, additionalFields.stream()
                // Map additional field keys with corresponding ECS field
                .map(f -> new AdditionalField(MdcEcsMapper.map(f.getKey()), f.getValue()))
                // Filter out non ECS fields
                .filter(f -> EcsFieldsChecker.isECSField(f.getKey())).collect(Collectors.toList()));
    }

    /**
     * Serialize the MDC (mapped and filtered)
     *
     * @param builder the builder to serialize in
     * @param mdcPropertyMap the MDC to serialize
     */
    public static void serializeMDC(StringBuilder builder, Map<String, String> mdcPropertyMap) {
        EcsJsonSerializer.serializeMDC(builder, mdcPropertyMap.entrySet().stream()
                // Map additional field keys with corresponding ECS field
                .map(f -> new AbstractMap.SimpleEntry<String, String>(MdcEcsMapper.map(f.getKey()), f.getValue()))
                // Filter out non ECS fields
                .filter(f -> EcsFieldsChecker.isECSField(f.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * Serialize the host data
     *
     * @param builder the builder to serialize in
     * @param hostData the host data to serialize
     */
    public static void serializeHostInfo(StringBuilder builder, HostData hostData) {
        builder.append(String.format("\"%s\":[\"%s\"],", EcsFields.HOST_IP.fieldName, hostData.getHostAddress()));
        builder.append(String.format("\"%s\":\"%s\",", EcsFields.HOST_HOSTNAME.fieldName, hostData.getHostName()));
    }

    /**
     * Serialize the event id
     *
     * @param builder the builder to serialize in
     * @param eventId the event id to serialize
     */
    public static void serializeEventId(StringBuilder builder, UUID eventId) {
        builder.append(String.format("\"%s\":\"%s\",", EcsFields.EVENT_ID.fieldName, eventId));
    }

    /**
     * serialize the custom markers (following key:value pattern)
     *
     * @param builder the builder to serialize in
     * @param marker the marker to serialize
     */
    public static void serializeCustomMarker(StringBuilder builder, String marker) {
        if (marker != null) {
            String[] customMarker = marker.split(":");
            if (customMarker.length == 2 && customMarker[0] != null) {
                String markerKey = EcsFieldsChecker.isECSField(MdcEcsMapper.map(customMarker[0]))
                        ? MdcEcsMapper.map(customMarker[0])
                        : "labels." + customMarker[0];
                builder.append(String.format("\"%s\":\"%s\",", markerKey, customMarker[1]));
            }
        }
    }

    /**
     * Serialize the ECS version based on the value defined as property
     *
     * @param builder the builder to serialize in
     */
    public static void serializeEcsVersion(StringBuilder builder) {
        builder.append("\"ecs.version\":\"");
        builder.append(ECS_VERSION);
        builder.append("\",");
    }
}
