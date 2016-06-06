package org.talend.daikon.serialize;

/**
 * Used to provide code to translate from an older persisted version of an object into the current version of the
 * object.
 * 
 * The methods in this interface contain the name of the field to be processed, using the pattern: {@code fieldDeleted_
 * <fieldName>}. They are discovered using reflection. See the comment below for the signature information of the
 * method.
 */
public interface DeserializeDeletedFieldHandler extends DeserializeMarker {

    public static final String FIELD_DELETED_PREFIX = "fieldDeleted_";

    /*
     * Called when a field is present in the serialized data, but is not present in the object being deserialized.
     * 
     * @param value the value of the deleted field in the old object
     * @return true if the object is considered to have migrated (it was modified from the serialized version)
     */

    // Example method name:
    //
    // boolean fieldDeleted_field1(Object value);
}
