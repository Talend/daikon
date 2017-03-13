package org.talend.daikon.content.s3;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.ContentServiceEnabled;
import org.talend.daikon.content.ResourceResolver;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
@ConditionalOnProperty(name = "content-service.store", havingValue = "s3")
public class S3ContentServiceConfiguration implements ContentServiceEnabled {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ContentServiceConfiguration.class);

    private static final String EC2_AUTHENTICATION = "EC2";

    private static final String TOKEN_AUTHENTICATION = "TOKEN";

    private static AmazonS3ClientBuilder configureEC2Authentication(AmazonS3ClientBuilder builder) {
        LOGGER.info("Using EC2 authentication");
        return builder.withCredentials(new EC2ContainerCredentialsProviderWrapper());
    }

    private static AmazonS3ClientBuilder configureTokenAuthentication(Environment environment, AmazonS3ClientBuilder builder) {
        LOGGER.info("Using Token authentication");
        final String key = environment.getProperty("content-service.store.s3.accessKey");
        final String secret = environment.getProperty("content-service.store.s3.secretKey");
        AWSCredentials awsCredentials = new BasicAWSCredentials(key, secret);
        return builder.withCredentials(new StaticCredentialsProvider(awsCredentials));
    }

    @Bean
    public AmazonS3 amazonS3(Environment environment) {
        // Configure authentication
        final String authentication = environment.getProperty("content-service.store.s3.authentication", EC2_AUTHENTICATION)
                .toUpperCase();
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        switch (authentication) {
        case EC2_AUTHENTICATION:
            builder = configureEC2Authentication(builder);
            break;
        case TOKEN_AUTHENTICATION:
            builder = configureTokenAuthentication(environment, builder);
            break;
        default:
            throw new IllegalArgumentException("Authentication '" + authentication + "' is not supported.");
        }

        // Configure region (optional)
        final String region = environment.getProperty("content-service.store.s3.region");
        if (StringUtils.isNotBlank(region)) {
            builder = builder.withRegion(region);
        }

        // All set!
        return builder.build();
    }

    @Bean
    public ResourceResolver s3PathResolver(ResourcePatternResolver resolver, //
                                           AmazonS3 amazonS3, //
                                           @Value("${content-service.store.s3.bucket}") String bucket, //
                                           SimpleStorageResourceLoader resourceLoader, //
                                           PathMatchingSimpleStorageResourcePatternResolver patternResolver) {
        return new S3ResourceResolver(patternResolver, amazonS3, bucket);
    }

    @Bean
    public PathMatchingSimpleStorageResourcePatternResolver getPathMatchingResourcePatternResolver(AmazonS3 amazonS3, //
            SimpleStorageResourceLoader resourceLoader, //
            ResourcePatternResolver resolver) {
        return new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, resourceLoader, resolver);
    }

    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader(AmazonS3 amazonS3) {
        return new SimpleStorageResourceLoader(amazonS3);
    }
}
