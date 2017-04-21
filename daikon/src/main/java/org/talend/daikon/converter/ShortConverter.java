package org.talend.daikon.converter;

public class ShortConverter extends Converter<Short> {

    @Override
    public Short convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else {
            return Short.parseShort(value.toString());
        }
    }
}
