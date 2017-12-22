package org.talend.daikon.logging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * TalendRequestInterceptor
 * @author sdiallo
 *
 */
public class TalendRequestInterceptor implements ClientHttpRequestInterceptor, RequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendRequestInterceptor.class);

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

    @Override
    public void apply(RequestTemplate template) {
        traceRequest(template.request(), template.body());
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        LOGGER.trace("requestURI : " + request.getURI());
        LOGGER.trace("requestMethod : " + request.getMethod());
        LOGGER.trace("requestHeader : " + request.getHeaders());
        LOGGER.trace("requestBody : " + getRequestBody(body));
    }

    private String getRequestBody(byte[] body) throws UnsupportedEncodingException {
        if (body != null && body.length > 0) {
            return getBodyAsJson(new String(body, "UTF-8"));
        } else {
            return null;
        }
    }

    private String getBodyAsJson(String bodyString) {
        if (bodyString == null || bodyString.length() == 0) {
            return null;
        } else {
            if (isValidJSON(bodyString)) {
                return bodyString;
            } else {
                bodyString.replaceAll("\"", "\\\"");
                return "\"" + bodyString + "\"";
            }
        }
    }

    public boolean isValidJSON(final String json) {
        boolean valid = false;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json);
        } catch (IOException e) {
            valid = false;
        }

        return valid;
    }

    private void traceRequest(Request request, byte[] body) {
        LOGGER.trace("requestURI : " + request.url());
        LOGGER.trace("requestMethod : " + request.method());
        LOGGER.trace("requestHeader : " + request.headers());
        try {
            LOGGER.trace("requestBody : " + getRequestBody(body));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
