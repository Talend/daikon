package org.talend.daikon.serialize;

import org.talend.daikon.serialize.migration.PostDeserializeHandler;

/**
 * Interface that allows code to be injected into the post-deserialization processing.
 */
public interface PostDeserializeSetup {

    /**
     * Performs post-deserialization setup as required by the {@link PostDeserializeHandler}.
     */
    void setup(Object deserializingObject);
}
