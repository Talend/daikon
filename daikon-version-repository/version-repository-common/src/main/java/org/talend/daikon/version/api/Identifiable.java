package org.talend.daikon.version.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A interface to be implemented for item handled by {@link VersionedRepository}.
 */
@JsonSerialize(using = IdentifiableSerializer.class)
public interface Identifiable {

    /**
     * @return The id of the resource.
     */
    String id();
}
