package org.talend.daikon.spring.audit.logs.service;

/**
 * Audit log response extractor interface, to support different networking libraries
 */
public interface ResponseExtractor {

    /**
     *
     * @param responseObject object returned by endpoint
     * @return response status
     */
    int getStatusCode(Object responseObject);

    /**
     *
     * @param responseObject object returned by endpoint
     * @return extracted response body
     */
    Object getResponseBody(Object responseObject);
}
