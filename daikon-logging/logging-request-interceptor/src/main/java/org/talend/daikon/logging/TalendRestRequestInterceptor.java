package org.talend.daikon.logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * TalendRequestInterceptor
 * 
 * @author sdiallo
 *
 */
public class TalendRestRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendRestRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
        } catch (Throwable t) {
            throw t;
        }

        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("requestURI=" + request.getURI());
            LOGGER.trace("requestMethod=" + request.getMethod());
            LOGGER.trace("requestHeader=" + request.getHeaders());
            LOGGER.trace("requestBody=" + TraceRequestUtil.getRequestBody(body));
        }
    }

}
