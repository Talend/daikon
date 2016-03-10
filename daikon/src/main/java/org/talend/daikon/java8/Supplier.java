package org.talend.daikon.java8;

/** Stand in for java.util.function.Supplier for pre-Java 8 code. */
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}