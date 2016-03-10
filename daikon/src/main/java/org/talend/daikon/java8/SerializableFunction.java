package org.talend.daikon.java8;

import java.io.Serializable;

/** Adds Serializable to the Function interface. */
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

}