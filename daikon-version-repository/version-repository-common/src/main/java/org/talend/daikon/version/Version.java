package org.talend.daikon.version;

import static java.util.Comparator.comparingLong;
import static org.talend.daikon.version.events.DataEventBuilder.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.talend.daikon.version.api.*;
import org.talend.daikon.version.events.FieldModification;

class Version<T extends Identifiable> implements VersionedRepository<T> {

    private final VersionedRepository<T> next;

    private final Journal journal;

    private final Class<T> itemClass;

    private final ModificationsProvider modificationsProvider;

    public Version(Journal journal, Class<T> itemClass, ModificationsProvider modificationsProvider,
            VersionedRepository<T> next) {
        this.journal = journal;
        this.itemClass = itemClass;
        this.modificationsProvider = modificationsProvider;
        this.next = next;
    }

    @Override
    public VersionedRepository<T> sync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VersionedRepository<T> async() {
        return this;
    }

    @Override
    public void reset(String id, String version) {
        Optional.ofNullable(checkout(id, version)).ifPresent(this::create);
    }

    @Override
    public T checkout(String id, String eventId) {
        final Stream<DataEvent> event = journal.log(id).sorted(comparingLong(DataEvent::getTimestamp).reversed());
        final T item = get(id);
        final Iterator<DataEvent> iterator = event.iterator();
        while (iterator.hasNext()) {
            DataEvent e = iterator.next();
            if (StringUtils.equals(e.getId(), eventId)) {
                break;
            }

            final List<FieldModification> changes = e.getDetail();
            for (FieldModification change : changes) {
                change.undo(item);
            }
        }
        return item;
    }

    @Override
    public Stream<DataEvent> log(String id) {
        return journal.log(id);
    }

    @Override
    public boolean exist(String filter) {
        return false;
    }

    @Override
    public Stream<T> list() {
        return next.list();
    }

    @Override
    public Stream<T> list(String filter) {
        return next.list(filter);
    }

    @Override
    public T create(T item) {
        final DataEvent event = buildResourceCreationEvent(itemClass.getName(), item.id()).build();
        journal.append(event);
        return next.create(item);
    }

    @Override
    public void update(T object) {
        final Optional<T> previous = Optional.ofNullable(next.get(object.id()));
        final DataEvent event = buildResourceUpdateEvent(itemClass.getName(), object.id()) //
                .setModifications(modificationsProvider, previous.orElse(object), object) //
                .build();
        journal.append(event);
        next.update(object);
    }

    @Override
    public T get(String id) {
        return next.get(id);
    }

    @Override
    public void clear() {
        list().forEach(object -> remove(object.id()));
    }

    @Override
    public void remove(String id) {
        final DataEvent event = buildResourceDeletionEvent(itemClass.getName(), id).build();
        journal.append(event);
        next.remove(id);
    }

}
