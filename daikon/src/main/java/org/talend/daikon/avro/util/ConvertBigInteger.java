package org.talend.daikon.avro.util;

import java.math.BigInteger;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.daikon.avro.AvroConverter;

/** TODO(rskraba): Use the logical type correctly here! */
public class ConvertBigInteger implements AvroConverter<BigInteger, String> {

    @Override
    public Schema getSchema() {
        return Schema.create(Type.STRING);
    }

    @Override
    public Class<BigInteger> getDatumClass() {
        return BigInteger.class;
    }

    @Override
    public BigInteger convertToDatum(String value) {
        return value == null ? null : new BigInteger(value);
    }

    @Override
    public String convertToAvro(BigInteger value) {
        return value == null ? null : value.toString();
    }
}