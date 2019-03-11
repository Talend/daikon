package org.talend.daikon.content;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * A helper for {@link ResourcePatternResolver} in case you want to prefix all locations with the same path. For
 * example, in case
 * you want "/file.txt" to be automatically converted to "/path/to/storage/file.txt".
 */
public class ContextualPatternResolver implements ResourcePatternResolver {

    private final ResourcePatternResolver delegate;

    private final String locationPrefix;

    public ContextualPatternResolver(ResourcePatternResolver delegate, String prefix) {
        this.delegate = delegate;

        if (!prefix.endsWith("/")) {
            this.locationPrefix = prefix + '/';
        } else {
            this.locationPrefix = prefix;
        }

    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        final String locationForList;
        if (locationPattern.startsWith("/")) {
            locationForList = locationPattern.substring(1);
        } else {
            locationForList = locationPattern;
        }

        return delegate.getResources(locationPrefix + locationForList);
    }

    @Override
    public Resource getResource(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location can not be null");
        }
        final String cleanedUpLocation = location.trim();
        if (StringUtils.isEmpty(cleanedUpLocation)) {
            throw new IllegalArgumentException("Location can not be empty (was '" + location + "')");
        }

        return delegate.getResource(locationPrefix + location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }
}
