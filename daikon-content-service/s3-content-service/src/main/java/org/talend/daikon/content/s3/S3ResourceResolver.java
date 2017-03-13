package org.talend.daikon.content.s3;

import static org.talend.daikon.content.s3.LocationUtils.toS3Location;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractResourceResolver;
import org.talend.daikon.content.DeletableResource;

import com.amazonaws.services.s3.AmazonS3;

class S3ResourceResolver extends AbstractResourceResolver {

    private final AmazonS3 amazonS3;

    private final String bucket;

    S3ResourceResolver(ResourcePatternResolver delegate, AmazonS3 amazonS3, String bucket) {
        super(delegate);
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        return super.getResources("s3://" + bucket + locationPattern);
    }

    @Override
    public DeletableResource getResource(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location can not be null");
        }
        final String cleanedUpLocation = location.trim();
        if (StringUtils.isEmpty(cleanedUpLocation)) {
            throw new IllegalArgumentException("Location can not be empty (was '" + location + "')");
        }

        return super.getResource("s3://" + bucket + "/" + toS3Location(location));
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new S3DeletableResource(writableResource, amazonS3, writableResource.getFilename(), bucket);
    }
}
