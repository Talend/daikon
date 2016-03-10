package org.talend.daikon.avro.container;

/**
 * Callback for code that knows how to write a specific type to a container.
 */
public interface ContainerWriterByIndex<WriteContainerT, T> {

    public void writeValue(WriteContainerT app, int index, T value);
}
