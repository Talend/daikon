package org.talend.daikon.serialize.migration;

/**
 * Used to provide code to translate from an older persisted version of an object into the current version of the
 * object.
 * 
 */
public interface DeserializeDeletedFieldHandler extends DeserializeMarker {

    /**
     * Called when a field is present in the serialized data, but is not present in the object being deserialized.
     * An action may be taken based on the value of the deleted field.
     *
     * @param fieldName the name of the deleted field
     * @param value the value of the deleted field in the old object, this may be null if the deserialization failed to create the
     * an object.
     * @return true if the object is considered to have migrated (it was modified from the serialized version)
     */
    boolean deletedField(String fieldName, Object value);
}
