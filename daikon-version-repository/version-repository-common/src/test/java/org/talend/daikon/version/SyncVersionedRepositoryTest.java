package org.talend.daikon.version;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.VersionedRepository;
import org.talend.daikon.version.events.ChangeType;

public class SyncVersionedRepositoryTest extends AbstractVersionedRepositoryTest {

    @Override
    public VersionedRepository<TestData> repository() {
        return repository.sync();
    }

    @Test
    public void shouldGetAfterSave() throws Exception {
        // When
        repository().create(new TestData("123"));

        // Then
        assertEquals(1, journal.log("123").count());
        assertNotNull(repository().get("123"));
    }

    @Test
    public void shouldDelete() throws Exception {
        // When
        repository().create(new TestData("123"));
        repository().remove("123");

        // Then
        assertEquals(2, journal.log("123").count());
        assertNull(repository().get("123"));
    }

    @Test
    public void shouldCheckoutUsingJournal() throws Exception {
        // Given
        final TestData item = new TestData("123");

        // When
        repository().create(item); // Create event
        Thread.sleep(200); // FIXME Journal events need to be created @ different times
        repository().update(item.setData("My data")); // Update event

        // When
        final Optional<DataEvent> creationEvent = journal.log("123") //
                .filter(e -> e.getType() == ChangeType.CREATE) //
                .findFirst();
        final Optional<DataEvent> updateEvent = journal.log("123") //
                .filter(e -> e.getType() == ChangeType.UPDATE) //
                .findFirst();
        assertTrue(creationEvent.isPresent());
        assertTrue(updateEvent.isPresent());
        final TestData createdItem = repository().checkout("123", creationEvent.get().getId());
        final TestData updatedItem = repository().checkout("123", updateEvent.get().getId());

        // Then
        assertNull(createdItem.getData());
        assertEquals("My data", updatedItem.getData());
    }

}