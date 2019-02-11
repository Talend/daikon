package org.talend.daikon.logging;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * TalendRequestInterceptor
 * 
 * @author sdiallo
 *
 */
public class TalendFeignRequestInterceptor implements RequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendFeignRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        traceRequest(template.request(), template.body());
    }

    private void traceRequest(Request request, byte[] body) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("requestURI=" + request.url());
            LOGGER.trace("requestMethod=" + request.method());
            LOGGER.trace("requestHeader=" + request.headers());
            try {
                LOGGER.trace("requestBody=" + TraceRequestUtil.getRequestBody(body));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
