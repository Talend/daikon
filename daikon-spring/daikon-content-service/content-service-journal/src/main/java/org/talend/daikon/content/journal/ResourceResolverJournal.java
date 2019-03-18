package org.talend.daikon.content.journal;

import org.springframework.core.io.Resource;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

import java.io.IOException;
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
    public Stream<String> matches(String pattern) {
        try {
            return Stream.of(delegate.getResources(pattern)).map(Resource::getFilename);
        } catch (IOException e) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public void clear(String pattern) {
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

    @Override
    public boolean ready() {
        return false;
    }

    @Override
    public void validate() {
        // Nothing to do
    }

    @Override
    public void invalidate() {
        // Nothing to do
    }

    @Override
    public void setResourceResolver(ResourceResolver resourceResolver) {
        // Nothing to do
    }
}
