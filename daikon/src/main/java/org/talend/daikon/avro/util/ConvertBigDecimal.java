package org.talend.daikon.avro.util;

import java.math.BigDecimal;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.daikon.avro.AvroConverter;

/** TODO(rskraba): Use the logical type correctly here! */
public class ConvertBigDecimal implements AvroConverter<BigDecimal, String> {

    @Override
    public Schema getSchema() {
        return Schema.create(Type.STRING);
    }

    @Override
    public Class<BigDecimal> getDatumClass() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal convertToDatum(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @Override
    public String convertToAvro(BigDecimal value) {
        return value == null ? null : value.toString();
    }
}