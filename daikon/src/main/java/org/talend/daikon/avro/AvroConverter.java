package org.talend.daikon.avro;

import org.apache.avro.Schema;

/**
 * Callback for code that knows how to convert between a datum type and an Avro-compatible type.
 */
public interface AvroConverter<DatumT, AvroT> {

    /** @return the Avro Schema that is compatible with the AvroT type. */
    public Schema getSchema();

    /** @return the class of the specific type that this converter knows how to convert from. */
    public Class<DatumT> getDatumClass();

    /** Takes the avro type and converts to the specific type. */
    public DatumT convertToDatum(AvroT value);

    /** Takes the specific type and converts to the avro type. */
    public AvroT convertToAvro(DatumT value);
}
