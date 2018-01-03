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
public class TalendFeignRequestInterceptor implements RequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendFeignRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        traceRequest(template.request(), template.body());
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

}
