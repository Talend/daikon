package org.talend.daikon.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.talend.daikon.version.api.VersionedRepository;

public class AsyncVersionedRepositoryTest extends AbstractVersionedRepositoryTest {

    @Override
    public VersionedRepository<TestData> repository() {
        return repository.async();
    }

    @Test
    public void shouldGetAfterSave() throws Exception {
        // When
        repository().create(new TestData("123"));

        // Then
        assertEquals(1, journal.log("123").count());
        assertNull(repository().get("123"));
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

}