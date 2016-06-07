package org.talend.daikon.avro.converter;

import java.math.BigDecimal;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

public class ConvertBigDecimal implements AvroConverter<BigDecimal, String> {

    @Override
    public Schema getSchema() {
        // TODO is it ok to use string type rather than logical decimal type? logical type need two parameters,
        // precision and scale
        return SchemaBuilder.builder().stringBuilder().prop(SchemaConstants.JAVA_CLASS_FLAG, getDatumClass().getCanonicalName())
                .endString();
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