package org.talend.daikon.version;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.StreamUtils;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Identifiable;
import org.talend.daikon.version.api.VersionedRepository;

class Storage<T extends Identifiable> implements VersionedRepository<T> {

    private final CrudRepository<T, String> storage;

    Storage(CrudRepository<T, String> storage) {
        this.storage = storage;
    }

    @Override
    public VersionedRepository<T> sync() {
        return this;
    }

    @Override
    public VersionedRepository<T> async() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset(String id, String version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T checkout(String id, String eventId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<DataEvent> log(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exist(String filter) {
        return false;
    }

    @Override
    public Stream<T> list() {
        return StreamUtils.createStreamFromIterator(storage.findAll().iterator());
    }

    @Override
    public Stream<T> list(String filter) {
        return list();
    }

    @Override
    public T create(T item) {
        return storage.save(item);
    }

    @Override
    public void update(T object) {
        storage.save(object);
    }

    @Override
    public T get(String id) {
        return storage.findOne(id);
    }

    @Override
    public void clear() {
        storage.deleteAll();
    }

    @Override
    public void remove(String id) {
        storage.delete(id);
    }
}
