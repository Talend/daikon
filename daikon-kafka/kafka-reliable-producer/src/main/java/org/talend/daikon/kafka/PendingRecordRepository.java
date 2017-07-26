package org.talend.daikon.kafka;

import java.util.List;

/**
 * Defines a storage of pending Kafka records.
 *
 * @param <K> type of key for the records (see {@link org.apache.kafka.common.record.Record}
 * @param <V> type of value for the records (see {@link org.apache.kafka.common.record.Record}
 */
public interface PendingRecordRepository<K, V> {

    /**
     * @return the list of pending records for the given application name.
     * @param applicationName the application name
     *
     */
    List<PendingRecord<K, V>> findPendingRecordByApplication(String applicationName);

    /**
     * Stores a pending record to this storage.
     * 
     * @param record the record to store
     * @return the stored record
     */
    PendingRecord<K, V> savePendingRecord(PendingRecord<K, V> record);

    /**
     * Deletes a pending record
     *
     * @param pendingRecordId
     */
    void delete(String pendingRecordId);

    /**
     * @return true if the storage contains at least one high priority for the application name
     * @param applicationName the application name
     *
     */
    boolean hasHighPriorityPendingRecords(String applicationName);
}
