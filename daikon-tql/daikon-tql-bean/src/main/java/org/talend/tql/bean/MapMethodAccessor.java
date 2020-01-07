package org.talend.tql.bean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MapMethodAccessor implements MethodAccessor {

    private final Method method;

    private final Object key;

    MapMethodAccessor(Method method, Object key) {
        this.method = method;
        this.key = key;
    }

    @Override
    public Set<Object> getValues(Set<Object> o) {
        return o.stream().flatMap(value -> {
            try {
                Object result = method.invoke(value, key);
                if (result != null && Iterable.class.isAssignableFrom(result.getClass())) {
                    return StreamSupport.stream(((Iterable<Object>) result).spliterator(), false);
                } else {
                    return Stream.of(result);
                }
            } catch (Exception e) {
                throw new UnsupportedOperationException("Not able to retrieve values", e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public Class getReturnType() {
        return Object.class;
    }
}
