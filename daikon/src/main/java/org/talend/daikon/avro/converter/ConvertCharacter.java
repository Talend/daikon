package org.talend.daikon.avro.converter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

public class ConvertCharacter implements AvroConverter<Character, String> {

    @Override
    public Schema getSchema() {
        return SchemaBuilder.builder().stringBuilder().prop(SchemaConstants.JAVA_CLASS_FLAG, getDatumClass().getCanonicalName())
                .endString();
    }

    @Override
    public Class<Character> getDatumClass() {
        return Character.class;
    }

    @Override
    public Character convertToDatum(String value) {
        return null;
    }

    @Override
    public String convertToAvro(Character value) {
        return null;
    }
}
