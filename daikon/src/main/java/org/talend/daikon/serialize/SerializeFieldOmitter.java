package org.talend.daikon.serialize;

/**
 * Used to specify a fields to be omitted in serialization.
 * 
 * It may be desirable to skip fields during serialization, depending on the purpose of the serialization. For example, for a
 * persistent serialization, fields related to I18N or presentation might be skipped as they are re-created after the
 * deserialization.
 */
public interface SerializeFieldOmitter {

    /**
     * Asks if the given field is to be skipped during serialization.
     * 
     * @param fieldName the name of the field
     * @param persistent true if this is a persistent serialzation, false if transient
     * @return true if the field is to be omitted
     */
    boolean omitField(String fieldName, boolean persistent);

}
