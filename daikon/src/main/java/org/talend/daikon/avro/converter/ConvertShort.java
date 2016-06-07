package org.talend.daikon.avro.converter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

public class ConvertShort implements AvroConverter<Short, Integer> {

    @Override
    public Schema getSchema() {
        return SchemaBuilder.builder().intBuilder().prop(SchemaConstants.JAVA_CLASS_FLAG, getDatumClass().getCanonicalName())
                .endInt();
    }

    @Override
    public Class<Short> getDatumClass() {
        return Short.class;
    }

    @Override
    public Short convertToDatum(Integer value) {
        return null;
    }

    @Override
    public Integer convertToAvro(Short value) {
        return null;
    }
}
