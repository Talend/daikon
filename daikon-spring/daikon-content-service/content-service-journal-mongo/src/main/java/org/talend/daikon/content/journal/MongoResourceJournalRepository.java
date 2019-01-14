package org.talend.daikon.content.journal;

import java.util.List;

import org.springframework.data.domain.Example;

public interface MongoResourceJournalRepository {

    List<ResourceJournalEntry> findByNameStartsWith(String name);

    long countByName(String name);

    void deleteByName(String name);

    void deleteByNameStartsWith(String name);

    void save(ResourceJournalEntry resourceJournalEntry);

    ResourceJournalEntry findOne(Example<ResourceJournalEntry> of);

    boolean exists(String journalReadyMarker);
}