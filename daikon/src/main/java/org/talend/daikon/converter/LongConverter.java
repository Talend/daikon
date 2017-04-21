package org.talend.daikon.converter;

public class LongConverter extends Converter<Long> {

    @Override
    public Long convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            return Long.parseLong(value.toString());
        }
    }
}
