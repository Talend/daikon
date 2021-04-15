package org.talend.daikon.logging.event.layout;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.LayoutBase;
import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.logging.ecs.EcsSerializer;
import org.talend.daikon.logging.event.field.HostData;

public class LogbackJSONAccessEventLayout extends LayoutBase<IAccessEvent> {

    private boolean hostInfo;

    private boolean addEventUuid;

    private String serviceName;

    private final List<AdditionalField> additionalFields = new ArrayList<>();

    private boolean requestHeaders;

    private boolean responseHeaders;

    public LogbackJSONAccessEventLayout() {
        this(true, true);
    }

    public LogbackJSONAccessEventLayout(boolean hostInfo, boolean addEventUuid) {
        this.hostInfo = hostInfo;
        this.addEventUuid = addEventUuid;
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

    public List<AdditionalField> getAdditionalFields() {
        return additionalFields;
    }

    public void addAdditionalField(AdditionalField pair) {
        this.additionalFields.add(pair);
    }

    public void setMetaFields(Map<String, String> metaFields) {
        metaFields.forEach((k, v) -> this.addAdditionalField(new AdditionalField(k, v)));
    }

    public void setRequestHeaders(boolean requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setResponseHeaders(boolean responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @Override
    public String doLayout(IAccessEvent event) {
        StringBuilder builder = new StringBuilder();
        EcsJsonSerializer.serializeObjectStart(builder, event.getTimeStamp());
        EcsSerializer.serializeEcsVersion(builder);
        EcsJsonSerializer.serializeThreadName(builder, event.getThreadName());
        EcsJsonSerializer.serializeServiceName(builder, serviceName);

        EcsSerializer.serializeHttpEventCategorizationFields(builder);

        EcsJsonSerializer.serializeFormattedMessage(builder,
                String.format("%s - %s \"%s\" %s %s", event.getRemoteHost(),
                        event.getRemoteUser() == null ? "-" : event.getRemoteUser(), event.getRequestURL(), event.getStatusCode(),
                        event.getContentLength()));

        // network and url
        EcsSerializer.serializeNetworkProtocol(builder, "http");
        EcsSerializer.serializeUrlScheme(builder, event.getRequest().getScheme());
        EcsSerializer.serializeHttpVersion(builder, StringUtils.substringAfter(event.getProtocol(), "/"));

        EcsSerializer.serializeHttpMethod(builder, event.getMethod());
        EcsSerializer.serializeHttpStatusCode(builder, event.getStatusCode());
        EcsSerializer.serializeEventOutcome(builder, event.getStatusCode());

        // url and request
        EcsSerializer.serializeUrlPath(builder, event.getRequestURI());
        EcsSerializer.serializeUrlQuery(builder, event.getQueryString());
        EcsSerializer.serializeUrlUser(builder, event.getRemoteUser());
        EcsSerializer.serializeHttpRequestBodyBytes(builder, event.getRequest().getContentLength());
        EcsSerializer.serializeUserAgent(builder, event.getRequestHeader("user-agent"));

        // response
        EcsSerializer.serializeHttpResponseBodyBytes(builder, event.getContentLength());

        // event start and duration
        EcsSerializer.serializeEventDuration(builder,
                TimeUnit.NANOSECONDS.convert(event.getElapsedTime(), TimeUnit.MILLISECONDS));
        if (event.getServerAdapter() != null) {
            EcsSerializer.serializeEventStart(builder, event.getServerAdapter().getRequestTimestamp());
        }
        EcsSerializer.serializeEventEnd(builder, event.getTimeStamp());
        EcsSerializer.serializeClientIp(builder, event.getRemoteAddr());
        EcsSerializer.serializeClientPort(builder, event.getRequest().getRemotePort());

        // additional fields
        EcsSerializer.serializeAdditionalFields(builder, additionalFields);

        // span, trace id etc
        EcsSerializer.serializeTraceId(builder, event.getRequestHeader("x-b3-traceId"));
        EcsSerializer.serializeSpanId(builder, event.getRequestHeader("x-b3-spanId"));

        // request headers, response headers
        if (this.requestHeaders) {
            EcsSerializer.serialiseHttpRequestHeaders(builder, event.getRequestHeaderMap());
        }
        if (this.responseHeaders) {
            EcsSerializer.serializeHttpResponseHeaders(builder, event.getResponseHeaderMap());
        }

        if (this.hostInfo) {
            EcsSerializer.serializeHostInfo(builder, new HostData());
        }

        if (this.addEventUuid) {
            EcsSerializer.serializeEventId(builder, UUID.randomUUID());
        }

        EcsJsonSerializer.serializeObjectEnd(builder);

        return builder.toString();
    }

}
