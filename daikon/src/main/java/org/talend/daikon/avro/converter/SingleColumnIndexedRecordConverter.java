package org.talend.daikon.avro.converter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;

/**
 * A factory for creating {@link IndexedRecord} adapters from any object. The output records always have a single column
 * with a predefined {@link Schema} from the constructor, with the given value.
 * 
 * This is a useful mechanism to permit single, primitive values to be passed between components, or to pass non-record
 * values between components (if the top-level data is an ARRAY for example).
 */
public class SingleColumnIndexedRecordConverter<DatumT>
        implements IndexedRecordConverter<DatumT, SingleColumnIndexedRecordConverter.PrimitiveAsIndexedRecordAdapter<DatumT>> {

    private final Class<DatumT> mDatumClass;

    /** The schema of the {@link IndexedRecord}s that this factory generates. */
    private final Schema mSchema;

    /**
     * @param datumClass The class of the instances that this factory knows how to create IndexedRecords for. This must
     * be an Avro-compatible class since it's instances will be directly inserted into the output records without
     * validation.
     * @param schema The schema that the datum class can be converted to. This will be the schema of the single field in
     * the generated {@link IndexedRecord}s.
     */
    public SingleColumnIndexedRecordConverter(Class<DatumT> datumClass, Schema schema) {
        this.mDatumClass = datumClass;
        // Construct a record name that is compatible with Avro.
        String recordName = datumClass.getSimpleName() + "Record"; //$NON-NLS-1$
        if (datumClass.isArray())
            recordName = datumClass.getComponentType().getName() + "ArrayRecord"; //$NON-NLS-1$
        this.mSchema = SchemaBuilder.record(recordName) //
                .fields().name("field1").type(schema).noDefault() // //$NON-NLS-1$
                .endRecord();
    }

    @Override
    public Class<DatumT> getDatumClass() {
        return mDatumClass;
    }

    @Override
    public Schema getSchema() {
        return mSchema;
    }

    @Override
    public void setSchema(Schema s) {
        throw new UnmodifiableAdapterException();
    }

    @Override
    public PrimitiveAsIndexedRecordAdapter<DatumT> convertToAvro(DatumT value) {
        return new PrimitiveAsIndexedRecordAdapter<>(mSchema, value);
    }

    @Override
    public DatumT convertToDatum(PrimitiveAsIndexedRecordAdapter<DatumT> value) {
        return value.mValue;
    }

    /**
     * An {@link IndexedRecord} adapter that makes a single value (usually a primitive) look like a one-columned
     * IndexedRecord.
     * 
     * @param <T> The primitive type to wrap.
     */
    public static class PrimitiveAsIndexedRecordAdapter<T> implements IndexedRecord {

        private final T mValue;

        private final Schema mSchema;

        private PrimitiveAsIndexedRecordAdapter(Schema schema, T value) {
            mSchema = schema;
            mValue = value;
        }

        @Override
        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public T get(int i) {
            return mValue;
        }

        @Override
        public void put(int i, Object v) {
            throw new UnmodifiableAdapterException();
        }
    }
}