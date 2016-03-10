package org.talend.daikon.avro.util;

import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.daikon.avro.AvroConverter;

/**
 * TODO(rskraba): Use the logical type here? Or just leave it as long for processing...?
 */
public class ConvertDate implements AvroConverter<Date, Long> {

    @Override
    public Schema getSchema() {
        return Schema.create(Type.LONG);
    }

    @Override
    public Class<Date> getDatumClass() {
        return Date.class;
    }

    @Override
    public Date convertToDatum(Long value) {
        return value == null ? null : new Date(value);
    }

    @Override
    public Long convertToAvro(Date value) {
        return value == null ? null : value.getTime();
    }

}