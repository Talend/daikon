package org.talend.daikon.spring.audit.logs.service;

import static org.talend.daikon.spring.audit.logs.model.AuditLogFieldEnum.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.talend.daikon.spring.audit.logs.exception.AuditLogException;
import org.talend.daikon.spring.audit.logs.model.AuditLogFieldEnum;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.impl.DefaultContextImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuditLogContextBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogContextBuilder.class);

    private final Map<String, String> context = new LinkedHashMap<>();

    private final Map<String, Object> request = new LinkedHashMap<>();

    private final Map<String, Object> response = new LinkedHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    private AuditLogContextBuilder() {
    }

    public static Context emptyContext() {
        return new DefaultContextImpl();
    }

    public static AuditLogContextBuilder create() {
        return new AuditLogContextBuilder();
    }

    private AuditLogContextBuilder with(String key, String value) {
        return with(key, value, context);
    }

    private AuditLogContextBuilder with(String key, Object value, Map contextMap) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        contextMap.put(key, value);
        return this;
    }

    public AuditLogContextBuilder withTimestamp(String timestamp) {
        return this.with(TIMESTAMP.getId(), timestamp);
    }

    public AuditLogContextBuilder withLogId(UUID logId) {
        return this.with(LOG_ID.getId(), logId.toString());
    }

    public AuditLogContextBuilder withRequestId(UUID requestId) {
        return this.with(REQUEST_ID.getId(), requestId.toString());
    }

    public AuditLogContextBuilder withAccountId(String accountId) {
        return this.with(ACCOUNT_ID.getId(), accountId);
    }

    public AuditLogContextBuilder withUserId(String userId) {
        return this.with(USER_ID.getId(), userId);
    }

    public AuditLogContextBuilder withUsername(String username) {
        return this.with(USERNAME.getId(), username);
    }

    public AuditLogContextBuilder withEmail(String email) {
        return this.with(EMAIL.getId(), email);
    }

    public AuditLogContextBuilder withApplicationId(String applicationId) {
        return this.with(APPLICATION_ID.getId(), applicationId);
    }

    public AuditLogContextBuilder withEventType(String eventType) {
        return this.with(EVENT_TYPE.getId(), eventType);
    }

    public AuditLogContextBuilder withEventCategory(String eventCategory) {
        return this.with(EVENT_CATEGORY.getId(), eventCategory);
    }

    public AuditLogContextBuilder withEventOperation(String eventOperation) {
        return this.with(EVENT_OPERATION.getId(), eventOperation);
    }

    public AuditLogContextBuilder withClientIp(String clientIp) {
        return this.with(CLIENT_IP.getId(), clientIp);
    }

    public AuditLogContextBuilder withRequestUrl(String requestUrl) {
        return this.with(URL.getId(), requestUrl, request);
    }

    public AuditLogContextBuilder withRequestMethod(String requestMethod) {
        return this.with(METHOD.getId(), requestMethod, request);
    }

    public AuditLogContextBuilder withRequestUserAgent(String requestUserAgent) {
        return this.with(USER_AGENT.getId(), requestUserAgent, request);
    }

    public AuditLogContextBuilder withRequestBody(Object requestBody) {
        return this.with(REQUEST_BODY.getId(), requestBody, request);
    }

    public AuditLogContextBuilder withResponseCode(int httpStatus) {
        return this.with(RESPONSE_CODE.getId(), String.valueOf(httpStatus), response);
    }

    public AuditLogContextBuilder withResponseBody(Object responseBody) {
        return this.with(RESPONSE_BODY.getId(), responseBody, response);
    }

    public Context build() throws AuditLogException {
        try {
            context.values().removeAll(Collections.singletonList(null));
            request.values().removeAll(Collections.singletonList(null));
            response.values().removeAll(Collections.singletonList(null));
            if (!request.isEmpty()) {
                context.put(REQUEST.getId(), objectMapper.writeValueAsString(request));
            }
            if (!response.isEmpty()) {
                context.put(RESPONSE.getId(), objectMapper.writeValueAsString(response));
            }
            checkAuditContextIsValid();
            return new DefaultContextImpl(context);
        } catch (JsonProcessingException e) {
            throw new AuditLogException("audit log serialization failure", e);
        }
    }

    public AuditLogContextBuilder withRequest(HttpServletRequest request, Object requestBody) {
        String userAgent = request.getHeader("User-Agent");
        return withClientIp(request.getRemoteAddr()).withRequestUrl(request.getRequestURL().toString())
                .withRequestMethod(request.getMethod()).withRequestUserAgent(userAgent).withRequestBody(requestBody);
    }

    public AuditLogContextBuilder withResponse(int httpStatus, Object body) {
        return withResponseCode(httpStatus).withResponseBody(body);
    }

    public void checkAuditContextIsValid() throws AuditLogException {
        // check elements of the context
        List<AuditLogFieldEnum> notFound = new ArrayList<>();
        for (AuditLogFieldEnum auditLogFieldEnum : AuditLogFieldEnum.values()) {
            if (auditLogFieldEnum.isMandatory() && !auditLogFieldEnum.hasParent()) {
                if (!context.containsKey(auditLogFieldEnum.getId())
                        || StringUtils.isEmpty(context.get(auditLogFieldEnum.getId()))) {
                    notFound.add(auditLogFieldEnum);
                }
            }
        }
        // then check request and response
        for (AuditLogFieldEnum requestChild : REQUEST.getChildren()) {
            if (requestChild.isMandatory()
                    && (!request.containsKey(requestChild.getId()) || StringUtils.isEmpty(request.get(requestChild.getId())))) {
                notFound.add(requestChild);
            }
        }
        for (AuditLogFieldEnum responseChild : RESPONSE.getChildren()) {
            if (responseChild.isMandatory() && (!response.containsKey(responseChild.getId())
                    || StringUtils.isEmpty(response.get(responseChild.getId())))) {
                notFound.add(responseChild);
            }
        }

        if (!notFound.isEmpty()) {
            throw new AuditLogException("audit log context is incomplete, missing information: " + notFound);
        }
    }

    public Map<String, String> getContext() {
        return context;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public Map<String, Object> getResponse() {
        return response;
    }
}
