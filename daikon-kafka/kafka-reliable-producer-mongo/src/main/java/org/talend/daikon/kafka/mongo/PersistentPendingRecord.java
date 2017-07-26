package org.talend.daikon.kafka.mongo;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.talend.daikon.kafka.PendingRecord;

@Document(collection = "pendingRecords")
public class PersistentPendingRecord<K, V> extends PendingRecord<K, V> {

    @Id
    private String persistentId;

    @CreatedDate
    private long persistentCreationDate;

    @LastModifiedDate
    private long persistentLastUpdateDate;

    public PersistentPendingRecord() {
        super();
    }

    public PersistentPendingRecord(PendingRecord<K, V> pendingRecord) {
        super();
        this.setId(pendingRecord.getId());
        this.setApplicationName(pendingRecord.getApplicationName());
        this.setCreationDate(pendingRecord.getCreationDate());
        this.setLastUpdateDate(pendingRecord.getLastUpdateDate());
        this.setRecordPriority(pendingRecord.getRecordPriority());
        this.setTopic(pendingRecord.getTopic());
        this.setPartition(pendingRecord.getPartition());
        this.setKey(pendingRecord.getKey());
        this.setValue(pendingRecord.getValue());
        this.setTimestamp(pendingRecord.getTimestamp());
    }

    public long getCreationDate() {
        return this.persistentCreationDate;
    }

    public void setCreationDate(long creationDate) {
        this.persistentCreationDate = creationDate;
    }

    public long getLastUpdateDate() {
        return persistentLastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.persistentLastUpdateDate = lastUpdateDate;
    }

    public String getId() {
        return persistentId;
    }

    public void setId(String id) {
        this.persistentId = id;
    }
}
