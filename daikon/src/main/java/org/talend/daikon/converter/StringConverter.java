package org.talend.daikon.converter;

import java.nio.ByteBuffer;

public class StringConverter extends Converter<String> {

    @Override
    public String convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof ByteBuffer) {
            return new String(((ByteBuffer) value).array());
        } else {
            return value.toString();
        }
    }
}
