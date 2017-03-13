package org.talend.daikon.content;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class AbstractDeletablePathResolver implements DeletablePathResolver {

    private final ResourcePatternResolver delegate;

    public AbstractDeletablePathResolver(ResourcePatternResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        final Resource[] resources = delegate.getResources(locationPattern);
        return stream(resources) //
                .map(resource -> convert((WritableResource) resource)) //
                .collect(Collectors.toList()) //
                .toArray(new DeletableResource[0]);
    }

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
