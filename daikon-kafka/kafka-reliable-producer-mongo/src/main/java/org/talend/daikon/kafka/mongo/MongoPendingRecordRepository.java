package org.talend.daikon.kafka.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.talend.daikon.kafka.RecordPriority;

public interface MongoPendingRecordRepository extends MongoRepository<PersistentPendingRecord, String> {

    List<PersistentPendingRecord> findByApplicationNameAndRecordPriority(String applicationName, RecordPriority priority);
}
