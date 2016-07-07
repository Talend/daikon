package org.talend.daikon.client;

/**
 * Indicate the way an API is accessed. Depending on available resources,
 * @see ClientService
 */
public enum Access {
    /**
     * Finds the service inside the available Spring beans.
     */
    LOCAL,
    /**
     * Finds the service using an HTTP communication.
     */
    REMOTE
}
