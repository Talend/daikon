package org.talend.daikon.converter;

public class FloatConverter extends Converter<Float> {

    @Override
    public Float convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            return Float.parseFloat(value.toString());
        }
    }
}
