package org.talend.daikon.client;

/**
 * An interface to specify ways to find a service class client.
 */
public interface ServiceFinder {

    /**
     * Finds a service that implements the given <code>serviceClass</code>.
     * 
     * @param serviceClass The service interface.
     * @param <T> A generic parameter to prevent cast in calling code.
     * @return An instance of the service class that can immediately be used to invoke service's operations,
     * <code>null</code> if not found.
     */
    <T> T find(Class<T> serviceClass);

    /**
     * Test method to check if service finder supports this type of {@link Access access}.
     * 
     * @param access One of available {@link Access access modes}.
     * @return <code>true</code> if service finder implementation supports the mode, <code>false</code> otherwise.
     */
    boolean allow(Access access);
}
