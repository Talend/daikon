package org.talend.daikon.serialize;

/**
 * Used to provide code to translate from an older persisted version of an object into the current version of the the
 * object.
 * 
 * The methods in this interface contain the name of the field to be processed, using the pattern: {@code fieldDeleted_
 * <fieldName>}. They are discovered using reflection. See the comment below for the signature information of the
 * method.
 */
public interface DeserializeDeletedFieldHandler extends DeserializeMarker {

    /*
     * Called when a field is present in the serialized data, but is not present in the object being deserialized.
     * 
     * @param version the version number of the object being deserialized (set using {@link
     * MigrationSetVersion#getVersionNumber()}).
     * 
     * @param value the value of the deleted field in the old object
     */

    // Example method name:
    //
    // void fieldDeleted_field1(int version, Object value);
}
