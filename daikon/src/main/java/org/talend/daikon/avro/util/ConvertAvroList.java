package org.talend.daikon.avro.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.daikon.java8.SerializableFunction;
import org.talend.daikon.avro.AvroConverter;

/**
 * Provides a wrapper around {@link List} of input data to view them as Avro data.
 * 
 * @param <DatumT> The specific elements of the input list.
 * @param <AvroT> The Avro-compatible type that the elements should be viewed as.
 */
public class ConvertAvroList<DatumT, AvroT> implements HasNestedAvroConverter<List<DatumT>, List<AvroT>> {

    /**
     * The Avro Schema corresponding to this list. This should be an Schema.Type.ARRAY.
     */
    private Schema schema;

    /** The specific list class that this convert will wrap. */
    private Class<List<DatumT>> datumClass;

    /** An AvroConverter to and from the element values. */
    private final AvroConverter<DatumT, AvroT> elementConverter;

    public ConvertAvroList(Class<List<DatumT>> datumClass, Schema schema, AvroConverter<DatumT, AvroT> elementConverter) {
        this.datumClass = datumClass;
        this.schema = schema;
        this.elementConverter = elementConverter;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public Class<List<DatumT>> getDatumClass() {
        return datumClass;
    }

    @Override
    public List<DatumT> convertToDatum(List<AvroT> value) {
        return Collections.unmodifiableList(new MappedList<>(value, new LambdaConvertToDatumFunction<>(elementConverter),
                new LambdaConvertToAvroFunction<>(elementConverter)));
    }

    @Override
    public List<AvroT> convertToAvro(List<DatumT> value) {
        return Collections.unmodifiableList(new MappedList<>(value, new LambdaConvertToAvroFunction<>(elementConverter),
                new LambdaConvertToDatumFunction<>(elementConverter)));
    }

    @Override
    public List<AvroConverter<?, ?>> getNestedAvroConverters() {
        return Arrays.<AvroConverter<?, ?>> asList(elementConverter);
    }

    /**
     * Java 7 implementation for a lambda SerializableFunction.
     */
    public static class LambdaConvertToDatumFunction<AvroT, DatumT> implements SerializableFunction<AvroT, DatumT> {

        /** Default serial version UID. */
        private static final long serialVersionUID = 1L;

        private final AvroConverter<DatumT, AvroT> elementConverter;

        public LambdaConvertToDatumFunction(AvroConverter<DatumT, AvroT> elementConverter) {
            this.elementConverter = elementConverter;
        }

        @Override
        public DatumT apply(AvroT value) {
            return elementConverter.convertToDatum(value);
        }
    }

    /**
     * Java 7 implementation for a lambda SerializableFunction.
     */
    public static class LambdaConvertToAvroFunction<DatumT, AvroT> implements SerializableFunction<DatumT, AvroT> {

        /** Default serial version UID. */
        private static final long serialVersionUID = 1L;

        private final AvroConverter<DatumT, AvroT> elementConverter;

        public LambdaConvertToAvroFunction(AvroConverter<DatumT, AvroT> elementConverter) {
            this.elementConverter = elementConverter;
        }

        @Override
        public AvroT apply(DatumT value) {
            return elementConverter.convertToAvro(value);
        }
    }
}
