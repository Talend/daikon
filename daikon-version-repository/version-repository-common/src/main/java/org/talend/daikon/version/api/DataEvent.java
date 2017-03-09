/*
 * ============================================================================
 *
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */

package org.talend.daikon.version.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.daikon.version.events.ChangeType;
import org.talend.daikon.version.events.FieldModification;

/**
 * Represents an event on a resource.
 */
public class DataEvent {

    /**
     * The internal id of this event
     */
    private String id;

    /**
     * Event timestamp (UTC)
     */
    private long timestamp;

    /**
     * The type of resource
     */
    private String resourceType;

    /**
     * The unique identifier of the resource
     */
    private String resourceId;

    /**
     * The type of change
     */
    private ChangeType type;

    /**
     * An optional user identifier
     */
    private String userId;

    /**
     * An optional transaction identifier
     */
    private String transaction;

    /**
     * An optional action that identifies the functional action
     * performed on the resource. Useful to better qualify the modification
     * in case of UPDATE.
     */
    private String action;

    /**
     * Optional parameters to better define the action performed on the resource
     */
    private Map<String, Object> actionParameters = new HashMap<>();

    /**
     * An optional list of details. Useful to provide details in case of update.
     */
    private List<FieldModification> detail = new ArrayList<>();

    /**
     * Gets id
     *
     * @return value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id
     *
     * @param id The id of the data event.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets transaction
     *
     * @return value of transaction
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     * Sets transaction
     *
     * @param transaction An arbitrary transaction id to be used to correlate events.
     */
    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    /**
     * Gets timestamp
     *
     * @return value of timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp
     *
     * @param timestamp The time
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets userId
     *
     * @return value of userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets userId
     *
     * @param userId The user id for the event.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets resourceType
     *
     * @return value of resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets resourceType
     *
     * @param resourceType An arbitrary resource type name.
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Gets resourceId
     *
     * @return value of resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets resourceId
     *
     * @param resourceId The id of the resource that triggered the event.
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Gets action
     *
     * @return value of action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets action
     *
     * @param action The type of action (update, create...).
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets actionParameters
     *
     * @return value of actionParameters
     */
    public Map<String, Object> getActionParameters() {
        return actionParameters;
    }

    /**
     * Sets actionParameters
     *
     * @param actionParameters Optional parameters of the event.
     */
    public void setActionParameters(Map<String, Object> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     * Gets detail
     *
     * @return value of detail
     */
    public List<FieldModification> getDetail() {
        return detail;
    }

    /**
     * Sets detail
     *
     * @param detail All modifications in the event?
     */
    public void setDetail(List<FieldModification> detail) {
        this.detail = detail;
    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public ChangeType getType() {
        return type;
    }

    /**
     * Sets type
     *
     * @param type Type of change.
     */
    public void setType(ChangeType type) {
        this.type = type;
    }

    /**
     * Adds a new field modification for this event
     * 
     * @param fieldModification the modification to add to this event
     */
    public void addFieldModification(FieldModification fieldModification) {
        this.detail.add(fieldModification);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataEvent dataEvent = (DataEvent) o;
        if (timestamp != dataEvent.timestamp) {
            return false;
        }
        if (resourceType != null ? !resourceType.equals(dataEvent.resourceType) : dataEvent.resourceType != null)
            return false;
        if (resourceId != null ? !resourceId.equals(dataEvent.resourceId) : dataEvent.resourceId != null) {
            return false;
        }
        if (type != dataEvent.type) {
            return false;
        }
        if (userId != null ? !userId.equals(dataEvent.userId) : dataEvent.userId != null) {
            return false;
        }
        if (transaction != null ? !transaction.equals(dataEvent.transaction) : dataEvent.transaction != null)
            return false;
        return action != null ? action.equals(dataEvent.action) : dataEvent.action == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (transaction != null ? transaction.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataEvent{" + "id='" + id + '\'' + ", timestamp=" + timestamp + ", resourceType='" + resourceType + '\''
                + ", resourceId='" + resourceId + '\'' + ", type=" + type + ", userId='" + userId + '\'' + ", transaction='"
                + transaction + '\'' + ", action='" + action + '\'' + ", actionParameters=" + actionParameters + ", detail="
                + detail + '}';
    }
}
