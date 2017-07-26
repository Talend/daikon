//package org.talend.daikon.kafka.util;
//
//import java.util.*;
//
//import org.springframework.stereotype.Component;
//import org.talend.daikon.kafka.PendingRecord;
//import org.talend.daikon.kafka.PendingRecordRepository;
//
//@Component
//public class PendingRecordRepositoryMock implements PendingRecordRepository {
//
//    private Map<String, List<PendingRecord<String, String>>> pendingRecords = new HashMap<>();
//
//    private Integer idIncrement = 0;
//
//    @Override
//    public List<PendingRecord<String, String>> findPendingRecordByApplication(String appName) {
//        return pendingRecords.containsKey(appName) ? new ArrayList<>(pendingRecords.get(appName)) : Collections.emptyList();
//    }
//
//    @Override
//    public PendingRecord<String, String> savePendingRecord(PendingRecord record) {
//        if (!pendingRecords.containsKey(record.getApplicationName())) {
//            pendingRecords.put(record.getApplicationName(), new ArrayList<>());
//        }
//        pendingRecords.get(record.getApplicationName()).add(record);
//        if (record.getId() == null) {
//            record.setId((idIncrement++).toString());
//        }
//        return record;
//    }
//
//    @Override
//    public void delete(String pendingRecordId) {
//        pendingRecords.values().forEach(list -> list.removeIf(pr -> pr.getId().equals(pendingRecordId)));
//    }
//
//    @Override
//    public boolean hasHighPriorityPendingRecords(String applicationName) {
//        return pendingRecords.containsKey(applicationName) && !pendingRecords.get(applicationName).isEmpty();
//    }
//
//    public void clean() {
//        pendingRecords.clear();
//    }
//}
