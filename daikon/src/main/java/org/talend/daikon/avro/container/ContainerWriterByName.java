package org.talend.daikon.avro.container;

/**
 * Callback for code that knows how to write a specific type to a container.
 */
public interface ContainerWriterByName<WriteContainerT, T> {

    public void writeValue(WriteContainerT app, String name, T value);
}
