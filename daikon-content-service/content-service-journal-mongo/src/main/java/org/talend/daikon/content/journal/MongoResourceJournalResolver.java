package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

/**
 * An implementation of {@link ResourceJournal} that uses a MongoDB database as backend.
 */
@Component
@EnableMongoRepositories
public class MongoResourceJournalResolver implements ResourceJournal {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoResourceJournalResolver.class);

    static final String JOURNAL_READY_MARKER = ".journal.ready";

    /**
     * Spring MongoDB template.
     */
    @Autowired
    private MongoResourceJournalRepository repository;

    @Override
    public void sync(ResourceResolver resourceResolver) {
        if (ready()) {
            LOGGER.warn("Journal is flagged 'ready', consider calling invalidate() first.");
            return;
        }

        try {
            LOGGER.info("Running initial sync...");
            final DeletableResource[] resources = resourceResolver.getResources("/**");
            for (int i = 0; i < resources.length; i++) {
                add(resources[i].getFilename());
                if (i % 500 == 0) {
                    LOGGER.info("Sync in progress ({}/{})", i, resources.length);
                }
            }
            validate();
            LOGGER.info("Initial sync done.");
        } catch (IOException e) {
            invalidate();
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public Stream<String> matches(String pattern) {
        LOGGER.debug("Match locations using pattern '{}'", pattern);
        if (StringUtils.isEmpty(pattern)) {
            return Stream.empty();
        }

        String patternForMatch = formattingStringToMongoPattern(pattern);
        return repository.findByNameStartsWith(patternForMatch).stream().map(ResourceJournalEntry::getName);
    }

    @Override
    public void clear(String pattern) {
        String patternForClear = formattingStringToMongoPattern(pattern);
        repository.deleteByNameStartsWith(patternForClear);
        LOGGER.debug("Cleared location '{}'.", patternForClear);
    }

    @Override
    public void add(String location) {
        if (StringUtils.isEmpty(location)) {
            return;
        }
        String savedLocation = updateLocationToAbsolutePath(location);
        if (!exist(savedLocation)) {
            repository.save(new ResourceJournalEntry(savedLocation));
        }
        LOGGER.debug("Location '{}' added to journal.", savedLocation);
    }

    @Override
    public void remove(String location) {
        repository.deleteByName(location);
        LOGGER.debug("Location '{}' removed from journal.", location);
    }

    @Override
    public void move(String source, String target) {
        ResourceJournalEntry dbResourceJournalEntry = repository.findOne(Example.of(new ResourceJournalEntry(source)));
        if (dbResourceJournalEntry != null) {
            dbResourceJournalEntry.setName(target);
            repository.save(dbResourceJournalEntry);
            repository.deleteByName(source);
            LOGGER.debug("Move from '{}' to '{}' recorded in journal.", source, target);
        } else {
            LOGGER.warn("Unable to move '{}' to '{}' (not found in journal)", source, target);
        }
    }

    @Override
    public boolean exist(String location) {
        String savedLocation = updateLocationToAbsolutePath(location);
        final boolean exist = repository.countByName(savedLocation) > 0L;
        LOGGER.debug("Location check on '{}': {}", location, exist);
        return exist;
    }

    @Override
    public boolean ready() {
        return repository.exists(JOURNAL_READY_MARKER);
    }

    @Override
    public void validate() {
        final ResourceJournalEntry entry = new ResourceJournalEntry(JOURNAL_READY_MARKER);
        if (!repository.exists(JOURNAL_READY_MARKER)) {
            repository.save(entry);
        }
    }

    @Override
    public void invalidate() {
        repository.deleteByName(JOURNAL_READY_MARKER);
    }

    private String updateLocationToAbsolutePath(String location) {
        String savedLocation = location;
        if (location.charAt(0) != '/') {
            savedLocation = "/" + location;
        }
        return savedLocation;
    }

    private String formattingStringToMongoPattern(String pattern) {
        return StringUtils.remove(pattern, "*");
    }
}
