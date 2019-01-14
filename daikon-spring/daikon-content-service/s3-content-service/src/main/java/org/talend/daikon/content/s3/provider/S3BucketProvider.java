package org.talend.daikon.content.s3.provider;

/**
 * An interface to be implemented to supply bucket names. Examples of implementation include:
 * <ul>
 * <li>Static bucket name (read from {@link org.springframework.core.env.Environment}).</li>
 * <li>Runtime-defined bucket name (for multi tenant use cases).</li>
 * </ul>
 */
public interface S3BucketProvider {

    /**
     * @return The tenant bucket name.
     */
    String getBucketName();

    /**
     * @return The root directory name to use inside the bucket.
     */
    String getRoot();
}
