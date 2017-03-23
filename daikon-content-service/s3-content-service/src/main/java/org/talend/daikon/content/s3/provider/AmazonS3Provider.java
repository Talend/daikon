package org.talend.daikon.content.s3.provider;

import com.amazonaws.services.s3.AmazonS3;

/**
 * An interface to be implemented to supply Amazon S3 clients. Examples of implementation include:
 * <ul>
 * <li>Static bucket name (read from {@link org.springframework.core.env.Environment}).</li>
 * <li>Runtime-defined bucket name (for multi tenant use cases).</li>
 * </ul>
 */
@FunctionalInterface
public interface AmazonS3Provider {

    /**
     * @return A configured {@link AmazonS3 S3 client} ready for use.
     */
    AmazonS3 getAmazonS3Client();
}
