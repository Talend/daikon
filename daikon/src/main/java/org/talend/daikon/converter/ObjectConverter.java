package org.talend.daikon.converter;

public class ObjectConverter extends Converter<Object> {

    @Override
    public Object convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else {
            return value;
        }
    }
}
