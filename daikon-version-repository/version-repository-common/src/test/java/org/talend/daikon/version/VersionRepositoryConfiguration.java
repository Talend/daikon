package org.talend.daikon.version;

import static java.util.stream.Collectors.toList;
import static org.talend.daikon.version.VersionedRepositoryConfiguration.versionedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Journal;
import org.talend.daikon.version.api.ModificationsProvider;
import org.talend.daikon.version.api.VersionedRepository;
import org.talend.daikon.version.events.javers.JaversModificationsProvider;

@Configuration
public class VersionRepositoryConfiguration {

    @Bean
    public CrudRepository<TestData, String> repository() {
        return new InMemoryCrudRepository();
    }

    @Bean
    public Journal journal() {
        return new InMemoryJournal();
    }

    @Bean
    public ModificationsProvider modificationsProvider() {
        return new JaversModificationsProvider();
    }

    @Bean
    public VersionedRepository<TestData> versionedRepository(Journal journal, ModificationsProvider modificationsProvider,
            CrudRepository<TestData, String> storage) {
        return versionedItem(TestData.class) //
                .journal(journal) //
                .modificationProvider(modificationsProvider) //
                .storage(storage) //
                .build();
    }

    public static class InMemoryCrudRepository implements CrudRepository<TestData, String> {

        private final Map<String, TestData> store = new HashMap<>();

        @Override
        public <S extends TestData> S save(S s) {
            store.put(s.id(), s.copy());
            return s;
        }

        @Override
        public <S extends TestData> Iterable<S> save(Iterable<S> iterable) {
            for (S s : iterable) {
                save(s);
            }
            return iterable;
        }

        @Override
        public TestData findOne(String s) {
            final TestData testData = store.get(s);
            if (testData != null) {
                return testData.copy();
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String s) {
            return store.containsKey(s);
        }

        @Override
        public Iterable<TestData> findAll() {
            return store.values().stream().map(TestData::copy).collect(toList());
        }

        @Override
        public Iterable<TestData> findAll(Iterable<String> iterable) {
            return store.values().stream().map(TestData::copy).collect(toList());
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public void delete(String s) {
            store.remove(s);
        }

        @Override
        public void delete(TestData testData) {
            store.remove(testData.id());
        }

        @Override
        public void delete(Iterable<? extends TestData> iterable) {
            for (TestData testData : iterable) {
                delete(testData);
            }
        }

        @Override
        public void deleteAll() {
            store.clear();
        }

        void clear() {
            store.clear();
        }
    }

    public static class InMemoryJournal implements Journal {

        private final List<DataEvent> events = new ArrayList<>();

        @Override
        public void append(DataEvent dataEvent) {
            events.add(dataEvent);
        }

        @Override
        public Stream<DataEvent> log(String id) {
            return events.stream().filter(e -> e != null && StringUtils.equals(e.getResourceId(), id));
        }

        void clear() {
            events.clear();
        }
    }
}
