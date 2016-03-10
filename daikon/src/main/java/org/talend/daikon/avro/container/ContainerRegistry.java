package org.talend.daikon.avro.container;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper object for managing callbacks that can read and write from container objects for a specific input/output
 * types.
 */
public class ContainerRegistry<KeyT, ReadContainerT, WriteContainerT> {

    /** Helper adapters for reading data from containers. */
    private final Map<KeyT, ContainerReaderByIndex<ReadContainerT, ?>> mReaders = new HashMap<>();

    /** Helper adapters for writing data to containers. */
    private final Map<KeyT, ContainerWriterByIndex<WriteContainerT, ?>> mWriters = new HashMap<>();

    public ContainerReaderByIndex<ReadContainerT, ?> getReader(KeyT key) {
        return mReaders.get(key);
    }

    public ContainerWriterByIndex<WriteContainerT, ?> getWriter(KeyT key) {
        return mWriters.get(key);
    }

    public void registerReader(KeyT type, ContainerReaderByIndex<ReadContainerT, ?> reader) {
        mReaders.put(type, reader);
    }

    public void registerWriter(KeyT type, ContainerWriterByIndex<WriteContainerT, ?> writer) {
        mWriters.put(type, writer);
    }
}
