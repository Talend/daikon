package org.talend.daikon.version;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Journal;
import org.talend.daikon.version.api.VersionedRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VersionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractVersionedRepositoryTest {

    @Autowired
    protected VersionedRepository<TestData> repository;

    @Autowired
    protected Journal journal;

    @Autowired
    protected CrudRepository<TestData, String> storage;

    @After
    public void tearDown() throws Exception {
        ((VersionRepositoryConfiguration.InMemoryJournal) journal).clear();
        ((VersionRepositoryConfiguration.InMemoryCrudRepository) storage).clear();
    }

    public abstract VersionedRepository<TestData> repository();

    @Test
    public void shouldWriteToJournal() throws Exception {
        // Given
        final TestData item = new TestData("123");

        // When
        repository().create(item); // Create event
        repository().update(item.setData("My data")); // Update event

        // Then
        final Stream<DataEvent> log = journal.log("123");
        assertEquals(2, log.count());
    }

}
