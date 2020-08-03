package org.talend.daikon.content;

import static java.util.Arrays.stream;

import java.io.IOException;

import io.micrometer.core.annotation.Timed;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class AbstractResourceResolver implements ResourceResolver {

    private final ResourcePatternResolver delegate;

    public AbstractResourceResolver(ResourcePatternResolver delegate) {
        this.delegate = delegate;
    }

    @Timed
    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        final Resource[] resources = delegate.getResources(locationPattern);
        return stream(resources) //
                .map(resource -> convert((WritableResource) resource)) //
                .toArray(DeletableResource[]::new);
    }

    @Timed
    @Override
    public DeletableResource getResource(String location) {
        return convert((WritableResource) delegate.getResource(location));
    }

    protected abstract DeletableResource convert(WritableResource writableResource);

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }
}
