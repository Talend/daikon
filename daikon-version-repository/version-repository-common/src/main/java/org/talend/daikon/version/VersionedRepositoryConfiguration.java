package org.talend.daikon.version;

import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.Identifiable;
import org.talend.daikon.version.api.Journal;
import org.talend.daikon.version.api.ModificationsProvider;
import org.talend.daikon.version.api.VersionedRepository;
import org.talend.daikon.version.events.javers.JaversModificationsProvider;

/**
 * A configuration class to declare a {@link VersionedRepository} in the Spring context.
 */
public class VersionedRepositoryConfiguration {

    private VersionedRepositoryConfiguration() {
    }

    /**
     * Entry point for building a {@link VersionedRepositoryConfiguration}.
     * @param itemClass The item class to be handled in the {@link VersionedRepository}.
     * @param <T> The type of the item class
     * @return A builder for the {@link VersionedRepositoryConfiguration} for chaining calls.
     */
    public static <T extends Identifiable> TypedVersionedRepositoryConfiguration<T> versionedItem(Class<T> itemClass) {
        return new TypedVersionedRepositoryConfiguration<>(itemClass);
    }

    public static class TypedVersionedRepositoryConfiguration<T extends Identifiable> {

        private final Class<T> itemClass;

        private Journal journal;

        private ModificationsProvider modificationsProvider = new JaversModificationsProvider();

        private CrudRepository<T, String> storage;

        private TypedVersionedRepositoryConfiguration(Class<T> itemClass) {
            this.itemClass = itemClass;
        }

        public TypedVersionedRepositoryConfiguration<T> journal(Journal journal) {
            this.journal = journal;
            return this;
        }

        public TypedVersionedRepositoryConfiguration<T> modificationProvider(ModificationsProvider modificationsProvider) {
            this.modificationsProvider = modificationsProvider;
            return this;
        }

        public TypedVersionedRepositoryConfiguration<T> storage(CrudRepository<T, String> storage) {
            this.storage = storage;
            return this;
        }

        public VersionedRepository<T> build() {
            return new DefaultVersionedRepository<>(itemClass, journal, modificationsProvider, storage);
        }

    }
}
