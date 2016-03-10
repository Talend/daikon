package org.talend.daikon.avro.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroConverter;

/**
 * Provides a wrapper around {@link Map} of specific input data to view them as Avro data.
 * 
 * @param <DatumT> The specific elements of the input list.
 * @param <AvroT> The Avro-compatible type that the elements should be seen as.
 */
public class ConvertAvroMap<DatumT, AvroT> implements HasNestedAvroConverter<Map<String, DatumT>, Map<String, AvroT>> {

    private Schema schema;

    private Class<Map<String, DatumT>> datumClass;

    private final AvroConverter<DatumT, AvroT> elementConverter;

    public ConvertAvroMap(Class<Map<String, DatumT>> datumClass, Schema schema, AvroConverter<DatumT, AvroT> elementConverter) {
        this.datumClass = datumClass;
        this.schema = schema;
        this.elementConverter = elementConverter;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public Class<Map<String, DatumT>> getDatumClass() {
        return datumClass;
    }

    @Override
    public Map<String, DatumT> convertToDatum(Map<String, AvroT> value) {
        return Collections.unmodifiableMap(new MappedValueMap<>(value, new ConvertAvroList.LambdaConvertToDatumFunction<>(
                elementConverter), new ConvertAvroList.LambdaConvertToAvroFunction<>(elementConverter)));
    }

    @Override
    public Map<String, AvroT> convertToAvro(Map<String, DatumT> value) {
        return Collections.unmodifiableMap(new MappedValueMap<>(value, new ConvertAvroList.LambdaConvertToAvroFunction<>(
                elementConverter), new ConvertAvroList.LambdaConvertToDatumFunction<>(elementConverter)));
    }

    @Override
    public Iterable<AvroConverter<?, ?>> getNestedAvroConverters() {
        return Arrays.<AvroConverter<?, ?>> asList(elementConverter);
    }

}
