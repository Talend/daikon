package org.talend.daikon.content.local;

import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractDeletablePathResolver;
import org.talend.daikon.content.DeletableResource;

class LocalDeletablePathResolver extends AbstractDeletablePathResolver {

    LocalDeletablePathResolver(ResourcePatternResolver delegate) {
        super(delegate);
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new LocalDeletableResource(this, writableResource);
    }

}
