package org.talend.daikon.java8;

/** Stand in for java.util.function.Function for pre-Java 8 code. */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}