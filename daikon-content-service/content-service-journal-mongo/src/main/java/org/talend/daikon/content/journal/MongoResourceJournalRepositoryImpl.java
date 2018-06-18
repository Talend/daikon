package org.talend.daikon.content.journal;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoResourceJournalRepositoryImpl implements MongoResourceJournalRepository {

    @Value("${mongo.resource.journal.collection:resourceJournalEntry}")
    private String collectionName;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ResourceJournalEntry> findByNameStartsWith(String name) {
        Query findByName = query(where("name").regex("^" + name));
        return mongoTemplate.find(findByName, ResourceJournalEntry.class, collectionName);
    }

    @Override
    public long countByName(String name) {
        Query countByName = query(where("name").is(name));
        return mongoTemplate.count(countByName, ResourceJournalEntry.class, collectionName);
    }

    @Override
    public void deleteByName(String name) {
        Query deleteByName = query(where("name").is(name));
        mongoTemplate.remove(deleteByName, ResourceJournalEntry.class, collectionName);
    }

    @Override
    public void deleteByNameStartsWith(String name) {
        Query deleteByName = query(where("name").regex(name));
        mongoTemplate.remove(deleteByName, ResourceJournalEntry.class, collectionName);
    }

    @Override
    public void save(ResourceJournalEntry resourceJournalEntry) {
        mongoTemplate.insert(resourceJournalEntry, collectionName);
    }

    @Override
    public ResourceJournalEntry findOne(Example<ResourceJournalEntry> example) {
        Query findOne = query(byExample(example));
        return mongoTemplate.findOne(findOne, ResourceJournalEntry.class, collectionName);
    }

    @Override
    public boolean exists(String journalReadyMarker) {
        Query existQuery = query(where("_id").is(journalReadyMarker));
        return mongoTemplate.exists(existQuery, collectionName);
    }

}