package org.talend.daikon.content.s3;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;
import org.talend.daikon.content.s3.provider.AmazonS3Provider;
import org.talend.daikon.content.s3.provider.S3BucketProvider;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

import io.findify.s3mock.S3Mock;

@Configuration
public class TestConfiguration implements InitializingBean, DisposableBean {

    final static AtomicInteger clientNumber = new AtomicInteger(0);

    private S3Mock s3Mock;

    private int s3MockPort;

    @Bean
    public AmazonS3Provider amazonS3Provider() {
        return () -> {
            final AmazonS3Client client = new AmazonS3Client(new AnonymousAWSCredentials());
            client.setEndpoint("http://127.0.0.1:" + s3MockPort);
            client.createBucket("s3-content-service1");
            client.createBucket("s3-content-service2");

            // Amazon S3 reads region from endpoint (127.0.0.1...)
            return new AmazonS3TestWrapper(client);
        };
    }

    @Bean
    public S3BucketProvider s3BucketProvider() {
        return new S3BucketProvider() {

            @Override
            public String getBucketName() {
                if (clientNumber.get() == 0) {
                    return "s3-content-service1";
                } else {
                    return "s3-content-service2";
                }
            }

            @Override
            public String getRoot() {
                if (clientNumber.get() == 0) {
                    return "app1";
                } else if (clientNumber.get() == 1) {
                    return "app2";
                } else {
                    return "";
                }
            }
        };
    }

    @Override
    public void afterPropertiesSet() {
        s3MockPort = SocketUtils.findAvailableTcpPort();
        s3Mock = S3Mock.create(s3MockPort, new File(".").getAbsolutePath() + "/target/s3");
        s3Mock.start();
    }

    @Override
    public void destroy() {
        s3Mock.stop();
    }
}
