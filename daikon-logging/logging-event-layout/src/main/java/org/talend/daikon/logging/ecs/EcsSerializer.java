package org.talend.daikon.logging.ecs;

import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;
import java.time.Instant;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.talend.daikon.logging.config.LoggingProperties;
import org.talend.daikon.logging.event.field.HostData;

/**
 * Utility ECS serializer class
 */
public class EcsSerializer {

    private static final String ECS_VERSION = LoggingProperties.get("ecs.version");

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

    /**
     * Serialize ECS Categorization Fields.
     * See https://www.elastic.co/guide/en/ecs/1.8/ecs-category-field-values-reference.html
     *
     * @param builder
     */
    public static void serializeHttpEventCategorizationFields(StringBuilder builder) {
        builder.append("\"").append(EcsFields.EVENT_KIND.fieldName).append("\":\"event\", ").append("\"")
                .append(EcsFields.EVENT_CATEGORY.fieldName).append("\":\"web\", ").append("\"")
                .append(EcsFields.EVENT_TYPE.fieldName).append("\":\"access\", ");
    }

    /**
     * Serialize the http status code.
     *
     * @param builder the builder to serialize in
     * @param statusCode
     */
    public static void serializeHttpStatusCode(StringBuilder builder, int statusCode) {
        builder.append("\"").append(EcsFields.HTTP_RESPONSE_STATUS_CODE.fieldName).append("\":").append(statusCode).append(",");
    }

    public static void serializeEventOutcome(StringBuilder builder, int statusCode) {
        String outcome = "success";
        if (statusCode >= 400) {
            outcome = "failure";
        }
        builder.append("\"").append(EcsFields.EVENT_OUTCOME.fieldName).append("\":\"").append(outcome).append("\",");
    }

    public static void serializeHttpMethod(StringBuilder builder, String method) {
        builder.append("\"").append(EcsFields.HTTP_REQUEST_METHOD.fieldName).append("\":\"").append(method).append("\",");
    }

    /**
     * Serialize the User agent request header.
     *
     * @param builder the builder to serialize in
     * @param userAgent optional user agent header
     */
    public static void serializeUserAgent(StringBuilder builder, String userAgent) {
        if (userAgent != null) {
            builder.append("\"").append(EcsFields.USER_AGENT_ORIGINAL.fieldName).append("\":\"").append(userAgent).append("\", ");
        }
    }

    public static void serializeClientIp(StringBuilder builder, String clientIp) {
        if (clientIp != null) {
            builder.append("\"").append(EcsFields.CLIENT_IP.fieldName).append("\":\"").append(clientIp).append("\",");
        }
    }

    public static void serializeClientPort(StringBuilder builder, int clientPort) {
        if (clientPort > 0) {
            builder.append("\"").append(EcsFields.CLIENT_PORT.fieldName).append("\":").append(clientPort).append(",");
        }
    }

    public static void serializeNetworkProtocol(StringBuilder builder, String protocol) {
        builder.append("\"").append(EcsFields.NETWORK_PROTOCOL.fieldName).append("\":\"").append(protocol).append("\",");
    }

    public static void serializeUrlScheme(StringBuilder builder, String scheme) {
        builder.append("\"").append(EcsFields.URL_SCHEME.fieldName).append("\":\"").append(scheme).append("\",");
    }

    public static void serializeHttpVersion(StringBuilder builder, String version) {
        if (version != null) {
            builder.append("\"").append(EcsFields.HTTP_VERSION.fieldName).append("\":\"").append(version).append("\",");
        }
    }

    public static void serializeHttpRequestBodyBytes(StringBuilder builder, long requestBodyLength) {
        builder.append("\"").append(EcsFields.HTTP_REQUEST_BODY_BYTES.fieldName).append("\":").append(requestBodyLength)
                .append(",");
    }

    public static void serializeHttpResponseBodyBytes(StringBuilder builder, long contentLength) {
        builder.append("\"").append(EcsFields.HTTP_RESPONSE_BODY_BYTES.fieldName).append("\":").append(contentLength).append(",");
    }

    /**
     * Serialize the event duration in nanoseconds.
     * 
     * @param builder
     * @param duration
     */
    public static void serializeEventDuration(StringBuilder builder, long duration) {
        builder.append("\"").append(EcsFields.EVENT_DURATION.fieldName).append("\":").append(duration).append(",");
    }

    /**
     * Serialize the event start date.
     *
     * @param builder
     * @param eventStart
     */
    public static void serializeEventStart(StringBuilder builder, long eventStart) {
        if (eventStart > 0) {
            builder.append("\"").append(EcsFields.EVENT_START.fieldName).append("\":\"")
                    .append(Instant.ofEpochMilli(eventStart)).append("\",");
        }
    }

    /**
     * Serialize the event end date.
     *
     * @param builder
     * @param eventEnd
     */
    public static void serializeEventEnd(StringBuilder builder, long eventEnd) {
        if (eventEnd > 0) {
            builder.append("\"").append(EcsFields.EVENT_END.fieldName).append("\":\"")
                    .append(Instant.ofEpochMilli(eventEnd)).append("\",");
        }
    }

    /**
     * Serialize the request query string if any.
     *
     * @param builder
     * @param queryString
     */
    public static void serializeUrlQuery(StringBuilder builder, String queryString) {
        if (queryString != null) {
            builder.append("\"").append(EcsFields.URL_QUERY.fieldName).append("\":\"").append(queryString).append("\",");
        }
    }

    /**
     * Serialize the request uri path.
     * 
     * @param builder
     * @param path
     */
    public static void serializeUrlPath(StringBuilder builder, String path) {
        builder.append("\"").append(EcsFields.URL_PATH.fieldName).append("\":\"").append(path).append("\",");
    }

    /**
     * Serialize the request username if any.
     * 
     * @param builder
     * @param username
     */
    public static void serializeUrlUser(StringBuilder builder, String username) {
        if (username != null) {
            builder.append("\"").append(EcsFields.URL_USERNAME.fieldName).append("\":\"").append(username).append("\",");
        }
    }

    public static void serializeTraceId(StringBuilder builder, String traceId) {
        if (traceId != null) {
            builder.append("\"").append(EcsFields.TRACE_ID.fieldName).append("\":\"").append(traceId).append("\",");
        }
    }

    public static void serializeSpanId(StringBuilder builder, String spanId) {
        if (spanId != null) {
            builder.append("\"").append(EcsFields.SPAN_ID.fieldName).append("\":\"").append(spanId).append("\",");
        }
    }

    public static void serialiseHttpRequestHeaders(final StringBuilder builder, Map<String, String> requestHeaderMap) {
        builder.append("\"http.request.headers\":{");
        EcsJsonSerializer.serializeMDC(builder, requestHeaderMap.entrySet().stream().filter(
                e -> !ArrayUtils.contains(new String[] { "authorization", "host", "user-agent" }, e.getKey().toLowerCase()))
                .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue)));
        EcsJsonSerializer.removeIfEndsWith(builder, ",");
        builder.append("}, ");
    }

    public static void serializeHttpResponseHeaders(StringBuilder builder, Map<String, String> responseHeaderMap) {
        builder.append("\"http.response.headers\":{");
        EcsJsonSerializer.serializeMDC(builder,
                responseHeaderMap.entrySet().stream()
                        .filter(e -> !ArrayUtils.contains(new String[] { "set-cookie", "date" }, e.getKey().toLowerCase()))
                        .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue)));
        EcsJsonSerializer.removeIfEndsWith(builder, ",");
        builder.append("}, ");
    }
}
