package org.talend.daikon.kafka;

/**
 * Enum to indicate the priority of a kafka record
 */
public enum RecordPriority {

    /**
     * Flag a record priority as high, which means that this message could affect application data consistency
     */
    HIGH,

    /**
     * Flag a record priority as low, which means that this message can be delayed without harming the application
     */
    LOW

}
