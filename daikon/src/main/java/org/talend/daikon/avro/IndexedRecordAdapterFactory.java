package org.talend.daikon.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 * 
 */
public interface IndexedRecordAdapterFactory<SpecificT, IndexedRecordT extends IndexedRecord> extends
        AvroConverter<SpecificT, IndexedRecordT> {

    /**
     * @return the Avro Schema for the {@link IndexedRecord} that this knows how to create. This must be a Type.RECORD,
     * and might be inferred from the specific record, known in advance or unknown. If the schema is null, it should be
     * inferred from the incoming data.
     */
    @Override
    Schema getSchema();

    /**
     * If the Avro Schema is known, this can be called to prevent it from being inferred from the specific incoming
     * record. This can be set to null to re-infer the schema from the next incoming datum.
     */
    void setSchema(Schema schema);

    /**
     * When a factory only supports one way conversion between a specific class of data to an IndexedRecord, it can
     * throw this exception on the unsupported convert operation.
     * 
     * This is a {@link RuntimeException}. Developers should ensure that the classes they support are capable of doing
     * the conversions they require.
     */
    public static class UnmodifiableAdapterException extends UnsupportedOperationException {

        /** Default serial version UID. */
        private static final long serialVersionUID = 1L;

        public UnmodifiableAdapterException() {
            super();
        }

        public UnmodifiableAdapterException(String message) {
            super(message);
        }
    }
}
