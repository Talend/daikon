package org.talend.daikon.spring.audit.logs.model;

import java.util.Arrays;
import java.util.List;

public enum AuditLogFieldEnum {

    TIMESTAMP("timestamp"),
    REQUEST_ID("requestId"),
    LOG_ID("logId"),
    // account id is not mandatory in the case of audit log of anonymous users
    ACCOUNT_ID("accountId", false),
    // user info is not mandatory in the case of audit log of anonymous users
    USER_ID("userId", false),
    USERNAME("username", false),
    EMAIL("email", false),
    APPLICATION_ID("applicationId"),
    EVENT_TYPE("eventType"),
    EVENT_CATEGORY("eventCategory"),
    EVENT_OPERATION("eventOperation"),
    CLIENT_IP("clientIp"),
    URL("url"),
    METHOD("method"),
    // user agent not mandatory because not always provided
    USER_AGENT("userAgent", false),
    // request body is not present for all user actions
    REQUEST_BODY("body", false),
    REQUEST("request", Arrays.asList(URL, METHOD, USER_AGENT, REQUEST_BODY)),
    RESPONSE_BODY("body", false),
    RESPONSE_CODE("code"),
    RESPONSE("response", Arrays.asList(RESPONSE_BODY, RESPONSE_CODE));

    private String id;

    private boolean mandatory;

    private final List<AuditLogFieldEnum> children;

    AuditLogFieldEnum(String id, List<AuditLogFieldEnum> children) {
        this.id = id;
        this.mandatory = true;
        this.children = children;
    }

    AuditLogFieldEnum(String id, boolean mandatory) {
        this.id = id;
        this.mandatory = mandatory;
        this.children = null;
    }

    AuditLogFieldEnum(String id) {
        this.id = id;
        this.mandatory = true;
        this.children = null;
    }

    public boolean hasParent() {
        return Arrays.stream(values()).anyMatch(it -> it.getChildren() != null && it.getChildren().contains(this));
    }

    public String getId() {
        return id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<AuditLogFieldEnum> getChildren() {
        return children;
    }
}
