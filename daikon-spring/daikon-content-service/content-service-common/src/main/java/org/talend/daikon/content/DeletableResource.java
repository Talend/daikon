package org.talend.daikon.content;

import java.io.IOException;

import org.springframework.core.io.WritableResource;

/**
 * An extension of Spring's {@link WritableResource} with {@link #delete()} capabilities.
 */
public interface DeletableResource extends WritableResource {

    /**
     * Deletes the resources. Once completed, calls to {@link #exists()} should return <code>false</code>.
     */
    void delete() throws IOException;

    /**
     * Move given resource to the <code>location</code> given as parameter.
     * 
     * @param location The new location for the resource
     */
    void move(String location) throws IOException;

    /**
     * Get the full absolute path for the resource
     *
     * @return the full absolute path of the resource
     */
    default String getAbsolutePath() throws IOException {
        return getURI().getPath();
    }
}
