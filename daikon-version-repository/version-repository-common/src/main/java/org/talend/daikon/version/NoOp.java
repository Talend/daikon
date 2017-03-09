package org.talend.daikon.version;

import java.util.stream.Stream;

import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Identifiable;
import org.talend.daikon.version.api.VersionedRepository;

class NoOp<T extends Identifiable> implements VersionedRepository<T> {

    @Override
    public VersionedRepository<T> sync() {
        return this;
    }

    @Override
    public VersionedRepository<T> async() {
        return this;
    }

    @Override
    public void reset(String id, String version) {
        // Nothing to do.
    }

    @Override
    public T checkout(String id, String version) {
        // Nothing to do.
        return null;
    }

    @Override
    public Stream<DataEvent> log(String id) {
        // Nothing to do.
        return null;
    }

    @Override
    public boolean exist(String filter) {
        // Nothing to do.
        return false;
    }

    @Override
    public Stream<T> list() {
        return Stream.empty();
    }

    @Override
    public Stream<T> list(String filter) {
        return Stream.empty();
    }

    @Override
    public T create(T item) {
        return item;
    }

    @Override
    public void update(T object) {
        // Nothing to do.
    }

    @Override
    public T get(String id) {
        // Nothing to do.
        return null;
    }

    @Override
    public void clear() {
        // Nothing to do.
    }

    @Override
    public void remove(String id) {
        // Nothing to do.
    }
}
