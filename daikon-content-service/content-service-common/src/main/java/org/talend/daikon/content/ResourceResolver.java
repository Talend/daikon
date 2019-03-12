package org.talend.daikon.content;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Strategy interface for resolving a deletable location into Resource objects.
 *
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ResourceResolver extends ResourcePatternResolver {

    @Override
    DeletableResource[] getResources(String locationPattern) throws IOException;

    @Override
    DeletableResource getResource(String location);

    /**
     * Returning the location prefix of the resource
     * 
     * @return the location prefix of the resource
     */
    String getLocationPrefix();

    /**
     * Clear all resource according to a location
     * 
     * @param location the location
     * @throws IOException e
     */
    default void clear(String location) throws IOException {
        Resource[] files = getResources(location);
        for (Resource resource : files) {
            ((DeletableResource) resource).delete();
        }
    }

}
