package org.talend.daikon.logging.ecs;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.talend.daikon.logging.event.field.HostData;

import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;

public class EcsSerializer {

    public static void serializeAdditionalFields(StringBuilder builder, List<AdditionalField> additionalFields) {
        EcsJsonSerializer.serializeAdditionalFields(builder, additionalFields.stream()
                // Map additional field keys with corresponding ECS field
                .map(f -> new AdditionalField(MdcEcsMapper.map(f.getKey()), f.getValue()))
                // Filter out non ECS fields
                .filter(f -> EcsFieldsChecker.isECSField(f.getKey())).collect(Collectors.toList()));
    }

    public static void serializeMDC(StringBuilder builder, Map<String, String> mdcPropertyMap) {
        EcsJsonSerializer.serializeMDC(builder, mdcPropertyMap.entrySet().stream()
                // Map additional field keys with corresponding ECS field
                .map(f -> new AbstractMap.SimpleEntry<String, String>(MdcEcsMapper.map(f.getKey()), f.getValue()))
                // Filter out non ECS fields
                .filter(f -> EcsFieldsChecker.isECSField(f.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public static void serializeHostInfo(StringBuilder builder, HostData hostData) {
        builder.append(String.format("\"%s\":[\"%s\"],", EcsFields.HOST_IP, hostData.getHostAddress()));
        builder.append(String.format("\"%s\":\"%s\",", EcsFields.HOST_HOSTNAME, hostData.getHostName()));
    }

    public static void serializeEventId(StringBuilder builder, UUID eventId) {
        builder.append(String.format("\"%s\":\"%s\",", EcsFields.EVENT_ID, eventId));
    }

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
}
