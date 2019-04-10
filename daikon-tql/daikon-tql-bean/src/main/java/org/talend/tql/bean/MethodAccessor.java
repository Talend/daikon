package org.talend.tql.bean;

import java.util.Set;

/**
 * A utility to access method return objects and encapsulate different behaviors to apply in case of iterable values or
 * unary values.
 *
 * @see MethodAccessorFactory
 */
public interface MethodAccessor {

    /**
     * Apply the method on <b>all</b> input values and return all the values returned by method invocations on
     * <code>o</code>.
     *
     * @param o All the values to apply.
     * @return A new {@link Set} that contains values of method invocations on <code>o</code>.
     */
    Set<Object> getValues(Set<Object> o);

    /**
     * @return The class of the element returned by method. For Iterable based accessors, this should return the
     * Iterable
     * item type instead of the Iterable itself.
     */
    Class getReturnType();
}
