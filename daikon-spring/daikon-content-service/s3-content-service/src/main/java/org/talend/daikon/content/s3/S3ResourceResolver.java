package org.talend.daikon.content.s3;

import static org.talend.daikon.content.s3.LocationUtils.toS3Location;
import static org.talend.daikon.content.s3.LocationUtils.S3PathBuilder.builder;
import static org.talend.daikon.content.s3.S3ContentServiceConfiguration.EC2_AUTHENTICATION;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractResourceResolver;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.s3.provider.S3BucketProvider;

import com.amazonaws.services.s3.AmazonS3;

import io.micrometer.core.annotation.Timed;

public class S3ResourceResolver extends AbstractResourceResolver {

    private final AmazonS3 amazonS3;

    private final S3BucketProvider bucket;

    private final Environment environment;

    S3ResourceResolver(ResourcePatternResolver delegate, AmazonS3 amazonS3, S3BucketProvider bucket, Environment environment) {
        super(delegate);
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.environment = environment;
    }

    @Timed
    @Override
    public DeletableResource[] getResources(String locationPattern) throws IOException {
        final String cleanedUpLocationPattern = builder(bucket.getBucketName()) //
                .append(bucket.getRoot()) //
                .append(locationPattern) //
                .build();

        final DeletableResource[] resources = super.getResources("s3://" + cleanedUpLocationPattern);
        return Stream.of(resources) //
                .map(r -> {
                    final String s3Location = builder(bucket.getBucketName()) //
                            .append(bucket.getRoot()) //
                            .append(toS3Location(r.getFilename())) //
                            .build();
                    return wrap(s3Location, r);
                }) //
                .toArray(DeletableResource[]::new);
    }

    @Timed
    @Override
    public DeletableResource getResource(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location can not be null");
        }
        final String cleanedUpLocation = location.trim();
        if (StringUtils.isEmpty(cleanedUpLocation)) {
            throw new IllegalArgumentException("Location can not be empty (was '" + location + "')");
        }

        final String s3Location = builder(bucket.getBucketName()) //
                .append(bucket.getRoot()) //
                .append(toS3Location(location)) //
                .build();

        final DeletableResource resource = super.getResource("s3://" + s3Location);
        return wrap(s3Location, resource);
    }

    private DeletableResource wrap(String s3Location, DeletableResource resource) {
        final String authentication = environment
                .getProperty(S3ContentServiceConfiguration.CONTENT_SERVICE_STORE_AUTHENTICATION, EC2_AUTHENTICATION)
                .toUpperCase();
        switch (authentication) {
        case S3ContentServiceConfiguration.MINIO_AUTHENTICATION:
        case S3ContentServiceConfiguration.CUSTOM_AUTHENTICATION:
            final String host = environment.getProperty(S3ContentServiceConfiguration.S3_ENDPOINT_URL);
            return new FixedURLS3Resource(host, s3Location, resource);
        default:
            return resource;
        }
    }

    @Override
    public String getLocationPrefix() {
        return builder(bucket.getBucketName()) //
                .append(bucket.getRoot()) //
                .build();
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new S3DeletableResource(writableResource, amazonS3, writableResource.getFilename(), bucket.getBucketName(),
                bucket.getRoot());
    }
}
