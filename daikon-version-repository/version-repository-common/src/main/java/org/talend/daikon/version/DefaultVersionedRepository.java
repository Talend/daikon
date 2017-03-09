package org.talend.daikon.version;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.*;

class DefaultVersionedRepository<T extends Identifiable> implements VersionedRepository<T> {

    private final VersionedRepository<T> sync;

    private final VersionedRepository<T> async;

    public DefaultVersionedRepository(Class<T> itemClass, Journal journal, ModificationsProvider modificationsProvider,
            CrudRepository<T, String> storage) {
        final Storage<T> crudStorage = new Storage<>(storage);
        sync = new Version<>(journal, itemClass, modificationsProvider, crudStorage);
        async = new Version<>(journal, itemClass, modificationsProvider, new NoOp<>());
    }

    @Override
    public VersionedRepository<T> sync() {
        return sync;
    }

    @Override
    public VersionedRepository<T> async() {
        return async;
    }

    @Override
    public void reset(String id, String version) {
        sync.reset(id, version);
    }

    @Override
    public T checkout(String id, String eventId) {
        return sync.checkout(id, eventId);
    }

    @Override
    public Stream<DataEvent> log(String id) {
        return sync.log(id);
    }

    @Override
    public boolean exist(String filter) {
        return sync.exist(filter);
    }

    @Override
    public Stream<T> list() {
        return sync.list();
    }

    @Override
    public Stream<T> list(String filter) {
        return sync.list(filter);
    }

    @Override
    public T create(T item) {
        return sync.create(item);
    }

    @Override
    public void update(T object) {
        sync.update(object);
    }

    @Override
    public T get(String id) {
        return sync.get(id);
    }

    @Override
    public void clear() {
        sync.clear();
    }

    @Override
    public void remove(String id) {
        sync.remove(id);
    }
}
