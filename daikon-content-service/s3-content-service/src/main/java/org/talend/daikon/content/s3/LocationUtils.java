package org.talend.daikon.content.s3;

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
        if (location.startsWith("/") && location.length() > 1) {
            return location.substring(1);
        } else {
            return location;
        }
    }
}
