package org.talend.daikon.content.s3;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.ContentServiceEnabled;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.s3.provider.AmazonS3Provider;
import org.talend.daikon.content.s3.provider.S3BucketProvider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
@ConditionalOnProperty(name = "content-service.store", havingValue = "s3")
public class S3ContentServiceConfiguration implements ContentServiceEnabled {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ContentServiceConfiguration.class);

    private static final String EC2_AUTHENTICATION = "EC2";

    private static final String TOKEN_AUTHENTICATION = "TOKEN";

    private static final String CUSTOM_AUTHENTICATION = "CUSTOM";

    private static AmazonS3ClientBuilder configureEC2Authentication(AmazonS3ClientBuilder builder) {
        LOGGER.info("Using EC2 authentication");
        return builder.withCredentials(new EC2ContainerCredentialsProviderWrapper());
    }

    private static AmazonS3ClientBuilder configureTokenAuthentication(Environment environment, AmazonS3ClientBuilder builder) {
        LOGGER.info("Using Token authentication");
        final String key = environment.getProperty("content-service.store.s3.accessKey");
        final String secret = environment.getProperty("content-service.store.s3.secretKey");
        final AWSCredentials awsCredentials = new BasicAWSCredentials(key, secret);
        return builder.withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
    }

    private static boolean isMultiTenancyEnabled(Environment environment) {
        return environment.getProperty("multi-tenancy.s3.active", Boolean.class, Boolean.FALSE);
    }

    @Bean
    public AmazonS3 amazonS3(Environment environment, ApplicationContext applicationContext) {
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
        case CUSTOM_AUTHENTICATION:
            try {
                final AmazonS3Provider amazonS3Provider = applicationContext.getBean(AmazonS3Provider.class);
                return amazonS3Provider.getAmazonS3Client();
            } catch (NoSuchBeanDefinitionException e) {
                throw new InvalidConfiguration("No S3 client provider in context", AmazonS3Provider.class, e);
            }
        default:
            throw new IllegalArgumentException("Authentication '" + authentication + "' is not supported.");
        }

        // Configure region (optional)
        final String region = environment.getProperty("content-service.store.s3.region");
        if (StringUtils.isNotBlank(region)) {
            builder = builder.withRegion(region);
        }

        // All set
        return builder.build();
    }

    @Bean
    public ResourceResolver s3PathResolver(AmazonS3 amazonS3, Environment environment, ApplicationContext applicationContext,
            PathMatchingSimpleStorageResourcePatternResolver resolver) {
        if (isMultiTenancyEnabled(environment)) {
            try {
                final S3BucketProvider s3BucketProvider = applicationContext.getBean(S3BucketProvider.class);
                return new S3ResourceResolver(resolver, amazonS3, s3BucketProvider);
            } catch (NoSuchBeanDefinitionException e) {
                throw new InvalidConfiguration("No S3 bucket name provider in context", S3BucketProvider.class, e);
            }
        } else {
            final String staticBucketName = environment.getProperty("content-service.store.s3.bucket", String.class);
            return new S3ResourceResolver(resolver, amazonS3, () -> staticBucketName);
        }
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

    @Bean
    public FailureAnalyzer incorrectMultiTenant() {
        return new IncorrectS3ConfigurationAnalyzer();
    }

    class InvalidConfiguration extends RuntimeException {

        private final Class missingBeanClass;

        InvalidConfiguration(String message, Class missingBeanClass, Throwable cause) {
            super(message, cause);
            this.missingBeanClass = missingBeanClass;
        }

        Class getMissingBeanClass() {
            return missingBeanClass;
        }
    }

}
