package org.talend.daikon.avro.converter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

public class ConvertByte implements AvroConverter<Byte, Integer> {

    @Override
    public Schema getSchema() {
        return SchemaBuilder.builder().intBuilder().prop(SchemaConstants.JAVA_CLASS_FLAG, getDatumClass().getCanonicalName())
                .endInt();
    }

    @Override
    public Class<Byte> getDatumClass() {
        return Byte.class;
    }

    @Override
    public Byte convertToDatum(Integer value) {
        return value == null ? null : value.byteValue();
    }

    @Override
    public Integer convertToAvro(Byte value) {
        return value == null ? null : value.intValue();
    }
}
