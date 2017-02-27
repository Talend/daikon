package org.talend.daikon.serialize.migration;

/**
 * Used to specify a version number when required for serialization.
 * 
 * A version number is required only in rare special cases where it is necessary to distinguish different content for a
 * given field. Implement this interface only when the version number is required.
 */
public interface SerializeSetVersion {

    /**
     * Return the version number to be kept with the persisted version of this object.
     * 
     * @return the version number, any number higher than zero. Zero should not be used as it's the same as no version
     * number.
     */
    int getVersionNumber();
}
