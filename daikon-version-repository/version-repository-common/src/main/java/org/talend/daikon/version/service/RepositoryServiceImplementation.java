package org.talend.daikon.version.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Identifiable;
import org.talend.daikon.version.api.VersionedRepository;

public abstract class RepositoryServiceImplementation<T extends Identifiable> implements RepositoryService<T> {

    @Autowired
    private VersionedRepository<T> repository;

    @Override
    public T get(String id) {
        return repository.get(id);
    }

    @Override
    public boolean exists(String id) {
        return repository.exist("id = '" + id + "'");
    }

    @Override
    public List<T> list() {
        return repository.list().collect(Collectors.toList());
    }

    @Override
    public T checkout(String id, String eventId) {
        return repository.checkout(id, eventId);
    }

    @Override
    public List<DataEvent> log(String id) {
        return repository.log(id).collect(Collectors.toList());
    }

    @Override
    public String create(T item) {
        return repository.create(item).id();
    }

    @Override
    public void update(T item) {
        repository.update(item);
    }

    @Override
    public void remove(String id) {
        repository.remove(id);
    }

    @Override
    public void reset(String id, String version) {
        repository.reset(id, version);
    }
}
