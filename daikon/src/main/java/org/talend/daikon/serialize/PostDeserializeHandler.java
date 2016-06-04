package org.talend.daikon.serialize;

/**
 * Used to provide code to update the state of an object when it is deserialized.
 * 
 * The version number is provided so that older versions of the object can be reacted to. The version number is set upon
 * serialization using {@link PersistenceSetVersion#getVersionNumber()}.
 * 
 */
public interface PostDeserializeHandler extends DeserializeMarker {

    /**
     * Called after an object has been deserialized. The object will have been fully materialized.
     *
     * @param version the version number of the object being deserialized (set using
     * {@link PersistenceSetVersion#getVersionNumber()}).
     * @return true if the object was changed, false if not. This is used to indicate something changed in the object
     * (because it was deserialized from an older version) so that the environment can take an appropriate action, just
     * as notifying the user of the change, or saving the object in the new format.
     */
    boolean postDeserialize(int version);

}
