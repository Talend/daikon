package org.talend.daikon.content.s3;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageProtocolResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.s3.provider.AmazonS3Provider;
import org.talend.daikon.content.s3.provider.S3BucketProvider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
@ConditionalOnProperty(name = "content-service.store", havingValue = "s3")
public class S3ContentServiceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ContentServiceConfiguration.class);

    private static final String EC2_AUTHENTICATION = "EC2";

    private static final String TOKEN_AUTHENTICATION = "TOKEN";

    private static final String CUSTOM_AUTHENTICATION = "CUSTOM";

    private static final String MINIO_AUTHENTICATION = "MINIO";

    static final String S3_ENDPOINT_URL = "content-service.store.s3.endpoint_url";

    static final String S3_ENABLE_PATH_STYLE = "content-service.store.s3.enable_path_style";

    private static AmazonS3ClientBuilder configureEC2Authentication(AmazonS3ClientBuilder builder) {
        LOGGER.info("Using EC2 authentication");
        return builder.withCredentials(new EC2ContainerCredentialsProviderWrapper());
    }

    private static AmazonS3ClientBuilder configureTokenAuthentication(Environment environment, AmazonS3ClientBuilder builder) {
        LOGGER.info("Using Token authentication");
        final String key = environment.getProperty("content-service.store.s3.accessKey");
        final String secret = environment.getProperty("content-service.store.s3.secretKey");
        AWSCredentials awsCredentials = new BasicAWSCredentials(key, secret);
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
            builder = configurePathStyleAccess(environment, builder, false);
            break;
        case TOKEN_AUTHENTICATION:
            builder = configureTokenAuthentication(environment, builder);
            builder = configurePathStyleAccess(environment, builder, false);
            break;
        case MINIO_AUTHENTICATION:
            // Nothing to do to standard builder, but check "content-service.store.s3.endpoint_url" is set.
            if (!environment.containsProperty(S3_ENDPOINT_URL)) {
                throw new InvalidConfiguration("Missing '" + S3_ENDPOINT_URL + "' configuration");
            }
            builder = configurePathStyleAccess(environment, builder, true);
            break;
        case CUSTOM_AUTHENTICATION:
            try {
                final AmazonS3Provider amazonS3Provider = applicationContext.getBean(AmazonS3Provider.class);
                return amazonS3Provider.getAmazonS3Client();
            } catch (NoSuchBeanDefinitionException e) {
                throw new InvalidConfigurationMissingBean("No S3 client provider in context", AmazonS3Provider.class, e);
            }
        default:
            throw new IllegalArgumentException("Authentication '" + authentication + "' is not supported.");
        }

        // Configure region (optional)
        final String region = environment.getProperty("content-service.store.s3.region", Regions.US_EAST_1.name());
        if (environment.containsProperty("content-service.store.s3.region")) {
            builder = builder.withRegion(region);
        }

        // Configure endpoint url (optional)
        final String endpointUrl = environment.getProperty(S3_ENDPOINT_URL);
        if (StringUtils.isNotBlank(endpointUrl)) {
            builder = builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, region));
        }

        // All set
        return builder.build();
    }

    private static AmazonS3ClientBuilder configurePathStyleAccess(Environment environment, AmazonS3ClientBuilder builder,
            boolean defaultValue) {
        final boolean enablePathStyle = environment.getProperty(S3_ENABLE_PATH_STYLE, Boolean.class, defaultValue);
        builder = builder.withPathStyleAccessEnabled(enablePathStyle);
        return builder;
    }

    @Bean
    public ResourceResolver s3PathResolver(AmazonS3 amazonS3, Environment environment, ApplicationContext applicationContext,
            PathMatchingSimpleStorageResourcePatternResolver resolver) {
        if (isMultiTenancyEnabled(environment)) {
            try {
                final S3BucketProvider s3BucketProvider = applicationContext.getBean(S3BucketProvider.class);
                return new S3ResourceResolver(resolver, amazonS3, s3BucketProvider);
            } catch (NoSuchBeanDefinitionException e) {
                throw new InvalidConfigurationMissingBean("No S3 bucket name provider in context", S3BucketProvider.class, e);
            }
        } else {
            final String staticBucketName = environment.getProperty("content-service.store.s3.bucket", String.class);
            final S3BucketProvider provider = new S3BucketProvider() {

                @Override
                public String getBucketName() {
                    return staticBucketName;
                }

                @Override
                public String getRoot() {
                    return StringUtils.EMPTY;
                }
            };
            return new S3ResourceResolver(resolver, amazonS3, provider);
        }
    }

    @Bean
    public PathMatchingSimpleStorageResourcePatternResolver getPathMatchingResourcePatternResolver(AmazonS3 amazonS3) {
        return new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, simpleStorageResourceLoader(amazonS3));
    }

    /*
     * TODO this method must be changed when https://github.com/spring-cloud/spring-cloud-aws/issues/348 is fixed.
     */
    private PathMatchingResourcePatternResolver simpleStorageResourceLoader(AmazonS3 amazonS3) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        SimpleStorageProtocolResolver resolver = new SimpleStorageProtocolResolver(amazonS3);
        resolver.afterPropertiesSet();
        resourceLoader.addProtocolResolver(resolver);
        return new PathMatchingResourcePatternResolver(resourceLoader);
    }

    @Bean
    public FailureAnalyzer incorrectMultiTenant() {
        return new IncorrectS3ConfigurationAnalyzer();
    }

    class InvalidConfigurationMissingBean extends RuntimeException {

        private final Class missingBeanClass;

        InvalidConfigurationMissingBean(String message, Class missingBeanClass, Throwable cause) {
            super(message, cause);
            this.missingBeanClass = missingBeanClass;
        }

        Class getMissingBeanClass() {
            return missingBeanClass;
        }
    }

    class InvalidConfiguration extends RuntimeException {

        InvalidConfiguration(String message) {
            super(message);
        }

    }

}
