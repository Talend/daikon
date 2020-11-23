package org.talend.daikon.content.s3;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for processing location in order to align them with S3's internals.
 */
class LocationUtils {

    /**
     * Private constructor for utility class.
     */
    private LocationUtils() {
        // nothing to do
    }

    /**
     * Strips off the first '/' (if more characters than just a '/') to have proper tree structure in S3 bucket.
     * 
     * @param location The S3 location to be potentially cleaned up.
     * @return The location ready to processed in all the S3 related classes.
     */
    public static String toS3Location(String location) {
        if (location == null) {
            return null;
        }
        if (location.startsWith("/") && location.length() > 1) {
            return location.substring(1);
        } else {
            return location;
        }
    }

    public static class S3PathBuilder {

        private final StringBuilder path = new StringBuilder();

        private final String bucket;

        private S3PathBuilder(String bucket) {
            this.bucket = bucket;
        }

        public static S3PathBuilder builder(String bucket) {
            return new S3PathBuilder(bucket);
        }

        public static S3PathBuilder builder() {
            return new S3PathBuilder(StringUtils.EMPTY);
        }

        public S3PathBuilder append(String path) {
            String trimmedPath = StringUtils.trim(path);
            if (StringUtils.isEmpty(trimmedPath)) {
                return this;
            }
            if (trimmedPath.startsWith("/")) {
                this.path.append(trimmedPath);
            } else {
                this.path.append('/').append(trimmedPath);
            }
            return this;
        }

        public String build() {
            return bucket + path;
        }

    }
}
