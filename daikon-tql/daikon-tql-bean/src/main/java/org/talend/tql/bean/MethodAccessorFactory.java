package org.talend.tql.bean;

import java.lang.reflect.Method;

/**
 * A factory for {@link MethodAccessor} that selects the right implementation based on {@link Method#getReturnType()}.
 */
public class MethodAccessorFactory {

    private MethodAccessorFactory() {
    }

    public static MethodAccessor build(Method method) {
        if (Iterable.class.isAssignableFrom(method.getReturnType())) {
            return new IterableMethodAccessor(method);
        } else {
            return new UnaryMethodAccessor(method);
        }
    }
}
