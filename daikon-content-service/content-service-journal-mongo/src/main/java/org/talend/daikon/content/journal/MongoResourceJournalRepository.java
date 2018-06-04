package org.talend.daikon.content.journal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoResourceJournalRepository extends MongoRepository<ResourceJournalEntry, String> {

    List<ResourceJournalEntry> findByNameStartsWith(String name);

    long countByName(String name);

    void deleteByName(String name);

    void deleteByNameStartsWith(String name);

}