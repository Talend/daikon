package org.talend.daikon.version.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Identifiable;

public interface RepositoryService<T extends Identifiable> {

    /**
     * Returns an instance by id.
     * 
     * @param id The id of the instance to be retrieved.
     * @return The instance or <code>null</code> if not found.
     */
    @RequestMapping(value = "/repository/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    T get(@PathVariable("id") String id);

    /**
     * Check if an instance by id.
     *
     * @param id The id of the instance to be checked.
     * @return <code>true</code> if instance exists, <code>false</code> otherwise.
     */
    @RequestMapping(value = "/repository/{id}", method = RequestMethod.OPTIONS)
    boolean exists(@PathVariable("id") String id);

    /**
     * @return All the items stored in this repository.
     */
    @RequestMapping(value = "/repository", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    List<T> list();

    /**
     * Returns an {@link Identifiable} at given <code>version</code>.
     *
     * @param id The resource id.
     * @param eventId The event id as returned by {@link DataEvent#getId()}.
     * @return A transient {@link Identifiable} instance with values at given event id.
     */
    @RequestMapping(value = "/repository/{id}/{eventId}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    T checkout(@PathVariable("id") String id, @PathVariable("eventId") String eventId);

    /**
     * List all {@link DataEvent events} for given identifiable.
     *
     * @param id The {@link Identifiable}'s id.
     * @return A stream of {@link DataEvent events} for given identifiable's id.
     */
    @RequestMapping(value = "/repository/{id}/events", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    List<DataEvent> log(@PathVariable("id") String id);

    /**
     * Create a new item based on provided value.
     *
     * @param item The item to be created.
     * @return The created item.
     */
    @RequestMapping(value = "/repository/", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    String create(@RequestBody T item);

    /**
     * Save or update an identifiable object.
     *
     * @param item the identifiable to save.
     */
    @RequestMapping(value = "/repository/", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    void update(@RequestBody T item);

    /**
     * Removes the {@link Identifiable identifiable} from repository.
     *
     * @param id The {@link Identifiable identifiable} to be deleted (only {@link Identifiable#id()} will be used for
     * remove).
     */
    @RequestMapping(value = "/repository/{id}", method = RequestMethod.DELETE)
    void remove(@PathVariable("id") String id);

    /**
     * Reset the item at given version, this is similar to {@link #checkout(String, String)} although changes are
     * persisted in this case.
     *
     * @param id The {@link Identifiable}'s id to be reset.
     * @param version The version to reset to.
     */
    @RequestMapping(value = "/repository/{id}/{version}", method = RequestMethod.POST)
    void reset(@PathVariable("id") String id, @PathVariable("version") String version);
}
