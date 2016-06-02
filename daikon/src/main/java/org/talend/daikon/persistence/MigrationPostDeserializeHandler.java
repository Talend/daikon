package org.talend.daikon.persistence;

/**
 * Used to provide code to update the state of an object if it was deserialized from a previous version.
 * 
 * The version number is provided using {@link MigrationSetVersion#getVersionNumber()}.
 * 
 */
public interface MigrationPostDeserializeHandler extends MigrationDeserializeMarker {

    /**
     * Called when an object has been deserialized to allow any changes from previous versions to be handled. The object
     * will have been fully materialized.
     *
     * @param version the version number of the object being deserialized (set using
     * {@link MigrationSetVersion#getVersionNumber()}).
     * @return true if the object was changed, false if not. This is used to indicate something changed in the object
     * (because it was deserialized from an older version) so that the environment can take an appropriate action, just
     * as notifying the user of the change, or saving the object in the new format.
     */
    boolean postDeserialize(int version);

}
