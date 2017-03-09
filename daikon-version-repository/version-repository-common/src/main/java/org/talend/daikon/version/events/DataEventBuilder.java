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

package org.talend.daikon.version.events;

import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.ModificationsProvider;

/**
 * A fluent builder API to create {@link DataEvent} instances.
 */
public interface DataEventBuilder {

    /**
     * Starts building a new resource creation event and specify the event's timestamp
     * 
     * @param resourceType the type of the resource that was created
     * @param resourceId the id of the resource that was created
     * @param timestamp the creation timestamp
     * @return a builder for this event
     */
    static NoDetailDataEventBuilder buildResourceCreationEvent(String resourceType, String resourceId, long timestamp) {
        return new NoDetailDataEventBuilder(ChangeType.CREATE, resourceType, resourceId, timestamp);
    }

    /**
     * Starts building a new resource creation event and use the current timestamp as event's timestamp
     * 
     * @param resourceType the type of the resource that was created
     * @param resourceId the id of the resource that was created
     * @return a builder for this event
     */
    static NoDetailDataEventBuilder buildResourceCreationEvent(String resourceType, String resourceId) {
        return buildResourceCreationEvent(resourceType, resourceId, System.currentTimeMillis());
    }

    /**
     * Starts building a new resource deletion event and specify the event's timestamp
     * 
     * @param resourceType the type of the resource that was deleted
     * @param resourceId the id of the resource that was deleted
     * @param timestamp the deletion timestamp
     * @return a builder for this event
     */
    static NoDetailDataEventBuilder buildResourceDeletionEvent(String resourceType, String resourceId, long timestamp) {
        return new NoDetailDataEventBuilder(ChangeType.DELETE, resourceType, resourceId, timestamp);
    }

    /**
     * Starts building a new resource deletion event and use the current timestamp as event's timestamp
     * 
     * @param resourceType the type of the resource that was deleted
     * @param resourceId the id of the resource that was deleted
     * @return a builder for this event
     */
    static NoDetailDataEventBuilder buildResourceDeletionEvent(String resourceType, String resourceId) {
        return new NoDetailDataEventBuilder(ChangeType.DELETE, resourceType, resourceId, System.currentTimeMillis());
    }

    /**
     * Starts building a new resource update event and specify the event's timestamp
     * 
     * @param resourceType the type of the resource that was update
     * @param resourceId the id of the resource that was update
     * @param timestamp the update timestamp
     * @return a builder for this event
     */
    static DetailedDataEventBuilder buildResourceUpdateEvent(String resourceType, String resourceId, long timestamp) {
        return new DetailedDataEventBuilder(ChangeType.UPDATE, resourceType, resourceId, timestamp);
    }

    /**
     * Starts building a new resource update event and use the current timestamp as event's timestamp
     * 
     * @param resourceType the type of the resource that was update
     * @param resourceId the id of the resource that was update
     * @return a builder for this event
     */
    static DetailedDataEventBuilder buildResourceUpdateEvent(String resourceType, String resourceId) {
        return new DetailedDataEventBuilder(ChangeType.UPDATE, resourceType, resourceId, System.currentTimeMillis());
    }

    class NoDetailDataEventBuilder implements DataEventBuilder {

        protected final DataEvent dataEvent;

        private NoDetailDataEventBuilder(ChangeType changeType, String resourceType, String resourceId, long timestamp) {
            this.dataEvent = new DataEvent();
            this.dataEvent
                    .setId(resourceType + '.' + resourceId + '.' + timestamp + '.' + dataEvent.getActionParameters().hashCode());
            this.dataEvent.setType(changeType);
            this.dataEvent.setResourceType(resourceType);
            this.dataEvent.setResourceId(resourceId);
            this.dataEvent.setTimestamp(timestamp);
        }

        public NoDetailDataEventBuilder userId(String userId) {
            this.dataEvent.setUserId(userId);
            return this;
        }

        public NoDetailDataEventBuilder transaction(String transaction) {
            this.dataEvent.setTransaction(transaction);
            return this;
        }

        public NoDetailDataEventBuilder action(String action) {
            this.dataEvent.setAction(action);
            return this;
        }

        public NoDetailDataEventBuilder addActionParameter(String key, Object value) {
            this.dataEvent.getActionParameters().put(key, value);
            return this;
        }

        public DataEvent build() {
            return dataEvent;
        }
    }

    class DetailedDataEventBuilder extends NoDetailDataEventBuilder {

        private DetailedDataEventBuilder(ChangeType changeType, String resourceType, String resourceId, long timestamp) {
            super(changeType, resourceType, resourceId, timestamp);
        }

        @Override
        public DetailedDataEventBuilder userId(String userId) {
            super.userId(userId);
            return this;
        }

        @Override
        public DetailedDataEventBuilder transaction(String transaction) {
            super.transaction(transaction);
            return this;
        }

        @Override
        public DetailedDataEventBuilder action(String action) {
            super.action(action);
            return this;
        }

        @Override
        public DetailedDataEventBuilder addActionParameter(String key, Object value) {
            super.addActionParameter(key, value);
            return this;
        }

        public DetailedDataEventBuilder setModifications(ModificationsProvider provider, Object v1, Object v2) {
            this.dataEvent.setDetail(provider.getModifications(v1, v2));
            return this;
        }

        public DetailedDataEventBuilder addResourceFieldModification(FieldModification modification) {
            this.dataEvent.addFieldModification(modification);
            return this;
        }

        public DetailedDataEventBuilder addResourceFieldUpdate(String path, Object previousValue, Object newValue) {
            this.dataEvent.addFieldModification(FieldModification.createFieldUpdate(path, previousValue, newValue));
            return this;
        }

        public DetailedDataEventBuilder addResourceFieldCreation(String path, Object newValue) {
            this.dataEvent.addFieldModification(FieldModification.createFieldCreation(path, newValue));
            return this;
        }

        public DetailedDataEventBuilder addResourceFieldDeletion(String path, Object previousValue) {
            this.dataEvent.addFieldModification(FieldModification.createFieldDeletion(path, previousValue));
            return this;
        }
    }

}
