package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.stream.Stream;

import io.micrometer.core.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * Resource resolver use to get the resource
     */
    private ResourceResolver resourceResolver;

    @Timed
    @Override
    public void sync() {
        if (ready()) {
            LOGGER.warn("Journal is flagged 'ready', consider calling invalidate() first.");
            return;
        }

        try {
            LOGGER.info("Running initial sync...");
            final DeletableResource[] resources = resourceResolver.getResources("/**");
            for (int i = 0; i < resources.length; i++) {
                // removing prefix from absolute path
                add(resources[i].getAbsolutePath());
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

    /**
     * Removing prefix from resourceName
     *
     * @param resourceName current resource name
     * @return the resourceName without the prefix
     */
    private String removePrefixFromResourceName(String resourceName) {
        String locationPrefix = formattingPrefixAccordingToResourceName(resourceName);

        if (resourceName.startsWith(locationPrefix)) {
            resourceName = resourceName.replaceFirst(locationPrefix, "");
        }

        return resourceName;
    }

    /**
     * Formatting prefix according to resourceName.
     * if resourceName is starting by / be sure that locationPrefix is also starting by /
     * if locationPrefix is starting by / and resourceName not remove it
     *
     * @param resourceName current resource name
     * @return the location prefix formatted according to the resourceName
     */
    private String formattingPrefixAccordingToResourceName(String resourceName) {
        String locationPrefix = resourceResolver.getLocationPrefix();
        if (StringUtils.isEmpty(locationPrefix)) {
            return "";
        }
        if (resourceName.startsWith("/") && !locationPrefix.startsWith("/")) {
            return "/" + locationPrefix;
        } else if (!resourceName.startsWith("/") && locationPrefix.startsWith("/")) {
            return locationPrefix.substring(1);
        }

        return locationPrefix;
    }

    @Timed
    @Override
    public Stream<String> matches(String pattern) {
        LOGGER.debug("Match locations using pattern '{}'", pattern);
        if (StringUtils.isEmpty(pattern)) {
            return Stream.empty();
        }

        String patternForMatch = formattingStringToMongoPattern(removePrefixFromResourceName(pattern));
        return repository.findByNameStartsWith(patternForMatch).stream().map(ResourceJournalEntry::getName);
    }

    @Timed
    @Override
    public void clear(String pattern) {
        String patternForClear = formattingStringToMongoPattern(removePrefixFromResourceName(pattern));
        repository.deleteByNameStartsWith(patternForClear);
        LOGGER.debug("Cleared location '{}'.", patternForClear);
    }

    @Timed
    @Override
    public void add(String location) {
        if (StringUtils.isEmpty(location)) {
            return;
        }
        String savedLocation = updateLocationToAbsolutePath(removePrefixFromResourceName(location));
        if (!exist(savedLocation)) {
            repository.save(new ResourceJournalEntry(savedLocation));
        }
        LOGGER.debug("Location '{}' added to journal.", savedLocation);
    }

    @Timed
    @Override
    public void remove(String location) {
        repository.deleteByName(removePrefixFromResourceName(location));
        LOGGER.debug("Location '{}' removed from journal.", location);
    }

    @Timed
    @Override
    public void move(String source, String target) {
        String sourceWithoutPrefix = removePrefixFromResourceName(source);
        String targetWithoutPrefix = removePrefixFromResourceName(target);

        ResourceJournalEntry dbResourceJournalEntry = repository
                .findOne(Example.of(new ResourceJournalEntry(sourceWithoutPrefix)));
        if (dbResourceJournalEntry != null) {
            dbResourceJournalEntry.setName(targetWithoutPrefix);
            repository.save(dbResourceJournalEntry);
            repository.deleteByName(sourceWithoutPrefix);
            LOGGER.debug("Move from '{}' to '{}' recorded in journal.", sourceWithoutPrefix, targetWithoutPrefix);
        } else {
            LOGGER.warn("Unable to move '{}' to '{}' (not found in journal)", sourceWithoutPrefix, targetWithoutPrefix);
        }
    }

    @Timed
    @Override
    public boolean exist(String location) {
        String savedLocation = updateLocationToAbsolutePath(removePrefixFromResourceName(location));
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

    @Override
    public void setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
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
