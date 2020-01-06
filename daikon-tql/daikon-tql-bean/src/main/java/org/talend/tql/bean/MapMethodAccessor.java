package org.talend.tql.bean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.stream.Collectors;
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
                return StreamSupport.stream(((Iterable<Object>) method.invoke(value, key)).spliterator(), false);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Not able to retrieve values", e);
            }
        }).collect(Collectors.toSet());

    }

    @Override
    public Class getReturnType() {
        try {
            final ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
            return Class.forName(returnType.getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException|ArrayIndexOutOfBoundsException e) {
            throw new UnsupportedOperationException("Can't find collection return type '" + method + "'.");
        }
    }
}
