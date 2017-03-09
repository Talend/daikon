package org.talend.daikon.version.api;

import java.util.stream.Stream;

/**
 * An interface to store and retrieve {@link DataEvent events}.
 */
public interface Journal {

    /**
     * Append a new event to the journal.
     * @param dataEvent The {@link DataEvent event} to be added.
     */
    void append(DataEvent dataEvent);

    /**
     * List all {@link DataEvent events} for resource with <code>id</code>.
     * @param id The resource id (as of {@link DataEvent#getResourceId()}).
     * @return The stream of {@link DataEvent event} for the resource.
     */
    Stream<DataEvent> log(String id);
}
