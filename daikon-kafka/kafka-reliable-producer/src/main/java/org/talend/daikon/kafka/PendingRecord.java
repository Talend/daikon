package org.talend.daikon.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * A wrapper for a Kafka record ({@link org.apache.kafka.common.record.Record}
 * 
 * @param <K> the record's key type
 * @param <V> the record's value type
 */
public class PendingRecord<K, V> {

    private String id;

    private String applicationName;

    private long creationDate;

    private long lastUpdateDate;

    private RecordPriority recordPriority;

    private String topic;

    private Integer partition;

    private K key;

    private V value;

    private Long timestamp;

    public PendingRecord() {
    }

    public PendingRecord(String applicationName, ProducerRecord<K, V> producerRecord, RecordPriority recordPriority) {
        this.timestamp = producerRecord.timestamp();
        this.key = producerRecord.key();
        this.value = producerRecord.value();
        this.partition = producerRecord.partition();
        this.topic = producerRecord.topic();
        this.applicationName = applicationName;
        this.recordPriority = recordPriority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public RecordPriority getRecordPriority() {
        return recordPriority;
    }

    public void setRecordPriority(RecordPriority recordPriority) {
        this.recordPriority = recordPriority;
    }

    public ProducerRecord<K, V> getProducerRecord() {
        return new ProducerRecord<>(getTopic(), getPartition(), getTimestamp(), getKey(), getValue());
    }

    @Override
    public String toString() {
        return "PendingRecord{" + "id='" + id + '\'' + ", applicationName='" + applicationName + '\'' + ", creationDate="
                + creationDate + ", lastUpdateDate=" + lastUpdateDate + ", topic='" + topic + '\'' + ", partition=" + partition
                + ", key=" + key + ", value=" + value + ", timestamp=" + timestamp + ", recordPriority=" + recordPriority + '}';
    }
}
