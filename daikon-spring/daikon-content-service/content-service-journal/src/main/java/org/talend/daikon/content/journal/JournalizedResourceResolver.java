package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

import io.micrometer.core.annotation.Timed;

public class JournalizedResourceResolver implements ResourceResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalizedResourceResolver.class);

    private final ResourceResolver delegate;

    private final ResourceJournal resourceJournal;

    public JournalizedResourceResolver(ResourceResolver delegate) {
        this(delegate, new ResourceResolverJournal(delegate));
    }

    public JournalizedResourceResolver(ResourceResolver delegate, ResourceJournal resourceJournal) {
        this.delegate = delegate;
        this.resourceJournal = resourceJournal;
    }

    @Timed
    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        if (locationPattern.indexOf('*') < 0) {
            // No pattern in locationPattern, switch to getResource
            return new DeletableResource[] { delegate.getResource(locationPattern) };
        }

        if (resourceJournal.ready()) {
            return resourceJournal
                    .matches(locationPattern) //
                    .map(location -> new LazyDeletableResource(location, this)) //
                    .toArray(DeletableResource[]::new);
        } else {
            LOGGER.warn("Journal is not ready (delegate call to non-indexed resolver to find '{}')", locationPattern);
            return delegate.getResources(locationPattern);
        }
    }

    @Timed
    @Override
    public DeletableResource getResource(String location) {
        final DeletableResource resource = delegate.getResource(location);
        resourceJournal.add(location);
        return new JournalizedDeletableResource(location, resource, resourceJournal);
    }

    @Override
    public String getLocationPrefix() {
        return delegate.getLocationPrefix();
    }

    @Timed
    @Override
    public void clear(String location) throws IOException {
        Stream.of(getResources(location)).forEach(deletableResource -> {
            try {
                deletableResource.delete();
            } catch (IOException e) {
                LOGGER.error("Unable to delete resource '{}'", deletableResource.getFilename(), e);
            }
        });
        resourceJournal.clear(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

}
