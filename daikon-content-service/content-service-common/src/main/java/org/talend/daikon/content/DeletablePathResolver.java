package org.talend.daikon.content;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import static java.util.Arrays.stream;

public interface DeletablePathResolver extends ResourcePatternResolver {

    @Override
    DeletableResource[] getResources(String locationPattern) throws IOException;

    @Override
    DeletableResource getResource(String location);

    default void clear(String location) throws IOException {
        Resource[] files = getResources(location);
        stream(files).forEach(r -> {
            try {
                ((DeletableResource) r).delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
