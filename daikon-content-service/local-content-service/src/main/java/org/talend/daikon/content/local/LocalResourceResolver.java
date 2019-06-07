package org.talend.daikon.content.local;

import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractResourceResolver;
import org.talend.daikon.content.DeletableResource;

public class LocalResourceResolver extends AbstractResourceResolver {

    private String locationPrefix;

    public LocalResourceResolver(ResourcePatternResolver delegate, String locationPrefix) {
        super(delegate);
        this.locationPrefix = locationPrefix;
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new LocalDeletableResource(this, writableResource);
    }

    @Override
    public String getLocationPrefix() {
        return this.locationPrefix;
    }
}
