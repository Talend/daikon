package org.talend.daikon.security.token;

/**
 * Use this interface to declare an endpoint that will be protected using {@link TokenAuthenticationFilter} (with same
 * protection configuration as for Actuator endpoints).
 */
@FunctionalInterface
public interface TokenProtectedPath {

    /**
     * @return The path of the endpoint to secure. Please note wildcards are supported (e.g. '/api/secured/**').
     */
    String getProtectedPath();
}
