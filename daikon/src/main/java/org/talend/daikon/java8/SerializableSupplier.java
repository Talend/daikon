package org.talend.daikon.java8;

import java.io.Serializable;

/** Adds Serializable to the Supplier interface. */
public interface SerializableSupplier<T> extends Supplier<T>, Serializable {

}