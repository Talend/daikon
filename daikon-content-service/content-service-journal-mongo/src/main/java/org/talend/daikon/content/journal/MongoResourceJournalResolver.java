package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

@Component
@EnableMongoRepositories
public class MongoResourceJournalResolver implements ResourceJournal {

    /** Spring MongoDB template. */
    @Autowired
    private MongoResourceJournalRepository repository;

    @Override
    public void sync() {
        // do nothing
    }

    @Override
    public Stream<String> matches(String pattern) throws IOException {
        return repository.findByNameStartsWith(pattern).stream().map(ResourceJournalEntry::getName);
    }

    @Override
    public void clear(String location) {
        repository.deleteByNameStartsWith(location);
    }

    @Override
    public void add(String location) {
        repository.save(new ResourceJournalEntry(location));
    }

    @Override
    public void remove(String location) {
        repository.deleteByName(location);
    }

    @Override
    public void move(String source, String target) {
        ResourceJournalEntry dbResourceJournalEntry = repository.findOne(Example.of(new ResourceJournalEntry(source)));
        dbResourceJournalEntry.setName(target);
        repository.save(dbResourceJournalEntry);

    }

    @Override
    public boolean exist(String location) {
        return repository.countByName(location) > 0L;
    }
}
