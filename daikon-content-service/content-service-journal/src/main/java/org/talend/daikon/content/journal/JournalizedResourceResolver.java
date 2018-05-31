package org.talend.daikon.content.journal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

import java.io.IOException;
import java.util.stream.Stream;

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
        LOGGER.info("Starting sync...");
        resourceJournal.sync();
        LOGGER.error("Sync done.");
    }

    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        return resourceJournal.matches(locationPattern) //
                .map(this::getResource) //
                .toArray(DeletableResource[]::new);
    }

    @Override
    public DeletableResource getResource(String location) {
        final DeletableResource resource = delegate.getResource(location);
        resourceJournal.add(location);
        return new JournalizedDeletableResource(location, resource, resourceJournal);
    }

    @Override
    public void clear(String location) throws IOException {
        Stream.of(getResources(location)).forEach(deletableResource -> {
            try {
                deletableResource.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        resourceJournal.clear(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

}
