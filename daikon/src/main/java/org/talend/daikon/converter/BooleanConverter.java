package org.talend.daikon.converter;

import org.apache.commons.lang3.StringUtils;

public class BooleanConverter extends Converter<Boolean> {

    @Override
    public Boolean convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            if (StringUtils.isBlank((String) value)) {
                return returnDefaultValue();
            } else if ("1".equals(value)) {
                return true;
            } else {
                return Boolean.parseBoolean((String) value);
            }
        } else {
            return Boolean.valueOf(value.toString());
        }
    }
}
