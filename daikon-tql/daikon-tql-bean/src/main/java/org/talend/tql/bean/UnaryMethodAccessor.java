package org.talend.tql.bean;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link MethodAccessor} implementation to handle method that returns a single value (not an {@link Iterable}).
 *
 * @see IterableMethodAccessor
 * @see MapMethodAccessor
 */
class UnaryMethodAccessor implements MethodAccessor {

    private final Method method;

    UnaryMethodAccessor(Method method) {
        this.method = method;
    }

    @Override
    public Set<Object> getValues(Set<Object> o) {
        return o.stream().map(value -> {
            try {
                return method.invoke(value);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Not able to retrieve values", e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public Class getReturnType() {
        return method.getReturnType();
    }
}
