package org.talend.daikon.hystrix.processors;

import java.io.IOException;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.json.JsonErrorCode;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A default implementation of {@link ErrorProcessor}.
 */
public class DefaultSecurityProcessor implements ErrorProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityProcessor.class);

    private final ObjectMapper objectMapper;

    public DefaultSecurityProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void rethrowError(HttpRequestBase request, HttpResponse response, Function<Exception, RuntimeException> onError) {
        LOGGER.trace("request on error {} -> {}", request, response.getStatusLine());
        final int statusCode = response.getStatusLine().getStatusCode();
        try {
            String content = IOUtils.toString(response.getEntity().getContent());
            LOGGER.debug("Content is ", content);
            JsonErrorCode code = objectMapper.readerFor(JsonErrorCode.class).readValue(content);
            code.setHttpStatus(statusCode);
            final TalendRuntimeException cause = new TalendRuntimeException(code);
            throw onError.apply(cause);
        } catch (JsonMappingException e) {
            LOGGER.debug("Cannot parse response content as JSON.", e);
            // Failed to parse JSON error, returns an unexpected code with returned HTTP code
            final TalendRuntimeException exception = new TalendRuntimeException(createUnexpectedException(statusCode), e);
            throw onError.apply(exception);
        } catch (IOException e) {
            throw new TalendRuntimeException(createUnexpectedException(statusCode), e);
        } finally {
            request.releaseConnection();
        }
    }

    private JsonErrorCode createUnexpectedException(int statusCode) {
        return new JsonErrorCode() {

            @Override
            public String getProduct() {
                return InternalErrorCodes.UNEXPECTED_EXCEPTION.getProduct();
            }

            @Override
            public String getCode() {
                return InternalErrorCodes.UNEXPECTED_EXCEPTION.getCode();
            }

            @Override
            public int getHttpStatus() {
                return statusCode;
            }
        };
    }

}
