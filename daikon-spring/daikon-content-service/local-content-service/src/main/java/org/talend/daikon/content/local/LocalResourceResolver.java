package org.talend.daikon.content.local;

import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractResourceResolver;
import org.talend.daikon.content.DeletableResource;

class LocalResourceResolver extends AbstractResourceResolver {

    LocalResourceResolver(ResourcePatternResolver delegate) {
        super(delegate);
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new LocalDeletableResource(this, writableResource);
    }

}
