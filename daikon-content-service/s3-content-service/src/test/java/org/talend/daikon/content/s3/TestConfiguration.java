package org.talend.daikon.content.s3;

import java.io.File;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.SocketUtils;
import org.talend.daikon.content.DeletablePathResolver;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import io.findify.s3mock.S3Mock;

@Configuration
public class TestConfiguration implements InitializingBean, DisposableBean {

    private S3Mock s3Mock;

    private int s3MockPort;

    @Bean
    public AmazonS3 amazonS3() {
        final AmazonS3Client client = new AmazonS3Client(new AnonymousAWSCredentials());
        client.setEndpoint("http://127.0.0.1:" + s3MockPort);
        client.createBucket("s3-content-service");

        // Amazon S3 reads region from endpoint (127.0.0.1...)
        return new AmazonS3TestWrapper(client);
    }

    @Bean
    public DeletablePathResolver s3PathResolver(ResourcePatternResolver defaultResourcePatternResolver, //
            SimpleStorageResourceLoader resolver, //
            AmazonS3 amazonS3) {
        final ResourcePatternResolver patternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, resolver,
                defaultResourcePatternResolver);
        return new S3DeletablePathResolver(patternResolver, amazonS3, "s3-content-service");
    }

    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader(AmazonS3 amazonS3) {
        return new SimpleStorageResourceLoader(amazonS3);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        s3MockPort = SocketUtils.findAvailableTcpPort();
        s3Mock = S3Mock.create(s3MockPort, new File(".").getAbsolutePath() + "/target");
        s3Mock.start();
    }

    @Override
    public void destroy() throws Exception {
        s3Mock.stop();
    }
}
