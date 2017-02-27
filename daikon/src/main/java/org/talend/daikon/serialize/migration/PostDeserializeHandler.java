package org.talend.daikon.serialize.migration;

import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.SerializerDeserializer;

/**
 * Used to provide code to update the state of an object when it is deserialized.
 * 
 * The version number is provided so that older versions of the object can be reacted to. The version number is set upon
 * serialization using {@link SerializeSetVersion#getVersionNumber()}.
 * 
 */
public interface PostDeserializeHandler extends DeserializeMarker {

    /**
     * Called after an object has been deserialized. The object will have been fully materialized.
     *
     * @param version the version number of the object being deserialized (set using
     * {@link SerializeSetVersion#getVersionNumber()}).
     * @param setup an option implementation of {@link PostDeserializeSetup} which is used to inject post-deserialization setup
     * code.
     * @param persistent see {@link SerializerDeserializer#PERSISTENT} and {@link SerializerDeserializer#PERSISTENT}.
     * @return true if the object was changed, false if not. This is used to indicate something changed in the object
     * (because it was deserialized from an older version) so that the environment can take an appropriate action, just
     * as notifying the user of the change, or saving the object in the new format.
     */
    boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent);

}
