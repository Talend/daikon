package org.talend.daikon.version.api;

import java.util.stream.Stream;

/**
 * A generic object storage that can store multiple versions of any object.
 */
public interface VersionedRepository<T extends Identifiable> {

    /**
     * Returns <code>true</code> if at least one <code>clazz</code> matches given filter.
     *
     * @param filter A TQL filter (i.e. storage-agnostic)
     * @return <code>true</code> if at least one <code>clazz</code> matches <code>filter</code>.
     */
    boolean exist(String filter);

    /**
     * @return A {@link java.lang.Iterable iterable} of <code>clazz</code>.
     */
    Stream<T> list();

    /**
     * List all items and filter items at the same time.
     *
     * @param filter A TQL filter for filtering items.
     * @return A {@link java.lang.Iterable iterable} of <code>clazz</code> that match given <code>filter</code>.
     */
    Stream<T> list(String filter);

    /**
     * Create a new item based on provided value.
     * 
     * @param item The item to be created.
     * @return The created item.
     */
    T create(T item);

    /**
     * Save or update an identifiable object.
     *
     * @param object the identifiable to save.
     */
    void update(T object);

    /**
     * Returns the Identifiable that matches the id and the class or null if none match.
     *
     * @param id the wanted Identifiable id.
     * @return the Identifiable that matches the id and the class or null if none match.
     */
    T get(String id);

    /**
     * Removes all {@link Identifiable} stored in this repository.
     */
    void clear();

    /**
     * Removes the {@link Identifiable identifiable} from repository.
     *
     * @param id The {@link Identifiable identifiable} to be deleted (only {@link Identifiable#id()} will be used for
     * remove).
     */
    void remove(String id);

    /**
     * Returns an {@link Identifiable} at given <code>version</code>.
     * 
     * @param id The resource id.
     * @param eventId The event id as returned by {@link DataEvent#getId()}.
     * @return A transient {@link Identifiable} instance with values at given event id.
     */
    T checkout(String id, String eventId);

    /**
     * List all {@link DataEvent events} for given identifiable.
     * 
     * @param id The {@link Identifiable}'s id.
     * @return A stream of {@link DataEvent events} for given identifiable's id.
     */
    Stream<DataEvent> log(String id);

    /**
     * Reset the item at given version, this is similar to {@link #checkout(String, String)} although changes are
     * persisted in this case.
     * 
     * @param id The {@link Identifiable}'s id to be reset.
     * @param version The version to reset to.
     */
    void reset(String id, String version);

    /**
     * @return A instance that performs journal and store operations in a blocking way.
     */
    VersionedRepository<T> sync();

    /**
     * @return A instance that only performs journal append and no store operation.
     */
    VersionedRepository<T> async();

}
