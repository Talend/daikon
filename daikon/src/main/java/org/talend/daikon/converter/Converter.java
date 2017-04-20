package org.talend.daikon.converter;

import java.util.HashMap;
import java.util.Map;

public abstract class Converter<T> {

    public static String DEFAULT_VALUE = "defaultValue";

    protected Map<String, Object> properties = new HashMap<>();

    public abstract T convert(Object value);

    public Converter<T> with(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Converter<T> withDefaultValue(T value) {
        properties.put(DEFAULT_VALUE, value);
        return this;
    }

    public Object get(String key) {
        return properties.get(key);
    }

    protected T returnDefaultValue() {
        if (properties.containsKey(DEFAULT_VALUE)) {
            return (T) properties.get(DEFAULT_VALUE);
        } else {
            return null;
        }

    }
}
