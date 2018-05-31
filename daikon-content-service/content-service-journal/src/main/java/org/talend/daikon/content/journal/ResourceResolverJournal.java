package org.talend.daikon.content.journal;

import org.springframework.core.io.Resource;
import org.talend.daikon.content.ResourceResolver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

class ResourceResolverJournal implements ResourceJournal {

    private final ResourceResolver delegate;

    ResourceResolverJournal(ResourceResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sync() {
        // Nothing to do
    }

    @Override
    public Stream<String> matches(String pattern) throws IOException {
        return Stream.of(delegate.getResources(pattern)) //
                .map(Resource::getFilename);
    }

    @Override
    public void clear(String location) {
        // Nothing to do
    }

    @Override
    public void add(String location) {
        // Nothing to do
    }

    @Override
    public void remove(String location) {
        // Nothing to do
    }

    @Override
    public void move(String source, String target) {
        // Nothing to do
    }

    @Override
    public boolean exist(String location) {
        return true;
    }
}
