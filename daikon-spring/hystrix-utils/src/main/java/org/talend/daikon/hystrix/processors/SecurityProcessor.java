package org.talend.daikon.hystrix.processors;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * A interface to implement to add the security related headers to the HTTP headers.
 */
public interface SecurityProcessor {

    /**
     * Add all needed security-related headers to the HTTP request.
     * @param request The HTTP request to be processed.
     */
    void addSecurityHeaders(HttpRequestBase request);

}
