package org.talend.daikon.converter;

public class ByteConverter extends Converter<Byte> {

    @Override
    public Byte convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else if (value instanceof Boolean) {
            return (byte) (((Boolean) value) ? 1 : 0);
        } else if (value instanceof String) {
            return Byte.decode((String) value).byteValue();
        } else {
            return Byte.valueOf(value.toString());
        }
    }
}
