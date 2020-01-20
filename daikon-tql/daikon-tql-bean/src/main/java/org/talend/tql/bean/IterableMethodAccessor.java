package org.talend.tql.bean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A {@link MethodAccessor} implementation to handle method that returns an {@link Iterable}.
 *
 * @see UnaryMethodAccessor
 * @see MapMethodAccessor
 */
class IterableMethodAccessor implements MethodAccessor {

    private final Method method;

    IterableMethodAccessor(Method method) {
        this.method = method;
    }

    @Override
    public Set<Object> getValues(Set<Object> o) {
        return o.stream().flatMap(value -> {
            try {
                return StreamSupport.stream(((Iterable<Object>) method.invoke(value)).spliterator(), false);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Not able to retrieve values", e);
            }
        }).collect(Collectors.toSet());

    }

    @Override
    public Class getReturnType() {
        try {
            final ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
            if (returnType.getActualTypeArguments().length == 0) {
                return Object.class;
            } else {
                return Class.forName(returnType.getActualTypeArguments()[0].getTypeName());
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Can't find collection return type '" + method + "'.");
        }
    }
}
