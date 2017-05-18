// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.hystrix;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.hystrix.processors.ErrorProcessor;
import org.talend.daikon.hystrix.processors.SecurityProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Base Hystrix command request for all DataPrep commands.
 * 
 * @param <T> Command result type.
 */
@Component
@Scope("request")
public class GenericCommand<T> extends HystrixCommand<T> {

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericCommand.class);

    private static final HttpStatus[] SUCCESS_STATUS = Stream.of(HttpStatus.values()) //
            .filter(HttpStatus::is2xxSuccessful) //
            .collect(Collectors.toList()) //
            .toArray(new HttpStatus[0]);

    private static final HttpStatus[] REDIRECT_STATUS = Stream.of(HttpStatus.values()) //
            .filter(HttpStatus::is3xxRedirection) //
            .collect(Collectors.toList()) //
            .toArray(new HttpStatus[0]);

    private static final HttpStatus[] INFO_STATUS = Stream.of(HttpStatus.values()) //
            .filter(HttpStatus::is1xxInformational) //
            .collect(Collectors.toList()) //
            .toArray(new HttpStatus[0]);

    /** Behaviours map. */
    private final Map<HttpStatus, BiFunction<HttpRequestBase, HttpResponse, T>> behavior = new EnumMap<>(HttpStatus.class);

    private final Map<String, String> headers = new HashMap<>();

    /** The http client. */
    @Autowired
    protected HttpClient client;

    /** Spring application context. */
    @Autowired
    protected ApplicationContext context;

    /** Jackson object mapper to handle json. */
    @Autowired
    private ObjectMapper objectMapper;

    /** DataPrep security holder. */
    @Autowired
    private SecurityProcessor security;

    @Autowired
    private ErrorProcessor errorProcessor;

    private String authenticationToken;

    private Supplier<HttpRequestBase> httpCall;

    /** Headers of the response received by the command. Set in the run command. */
    private Header[] commandResponseHeaders = new Header[0];

    /** Default onError behaviour. */
    private Function<Exception, RuntimeException> onError = Defaults.passthrough();

    private HttpStatus status;

    /**
     * Protected constructor.
     *
     * @param group the command group.
     */
    protected GenericCommand(final HystrixCommandGroupKey group) {
        super(group);
    }

    protected GenericCommand(final HystrixCommandGroupKey group, final Map<String, String> headers) {
        this(group);
        this.headers.putAll(headers);
    }

    @Override
    protected Throwable decomposeException(Exception e) {
        Throwable current = e;
        while (current.getCause() != null) {
            if (current instanceof TalendRuntimeException) {
                break;
            }
            current = current.getCause();
        }
        if (current instanceof TalendRuntimeException) {
            return current;
        } else {
            return super.decomposeException(e);
        }
    }

    /**
     * Runs a data prep command with the following steps:
     * <ul>
     * <li>Gets the HTTP command to execute (see {@link #execute(Supplier)}.</li>
     * <li>Gets the behavior to adopt based on returned HTTP code (see {@link #on(HttpStatus...)}).</li>
     * <li>If no behavior was defined for returned code, returns an error as defined in {@link #onError(Function)}</li>
     * <li>If a behavior was defined, invokes defined behavior.</li>
     * </ul>
     *
     * @return A instance of <code>T</code>.
     * @throws Exception If command execution fails.
     */
    @Override
    protected T run() throws Exception {
        final HttpRequestBase request = httpCall.get();

        // insert all the provided headers in the request
        if (headers.size() > 0) {
            headers.forEach(request::addHeader);
        }

        // update request header with security token
        security.addSecurityHeaders(request);

        final HttpResponse response;
        try {
            LOGGER.trace("Requesting {} {}", request.getMethod(), request.getURI());
            response = client.execute(request);
        } catch (Exception e) {
            throw onError.apply(e);
        }
        commandResponseHeaders = response.getAllHeaders();

        status = HttpStatus.valueOf(response.getStatusLine().getStatusCode());

        // do we have a behavior for this status code (even an error) ?
        // if yes use it
        BiFunction<HttpRequestBase, HttpResponse, T> function = behavior.get(status);
        if (function != null) {
            try {
                return function.apply(request, response);
            } catch (Exception e) {
                throw onError.apply(e);
            }
        }

        // handle response's HTTP status
        if (status.is4xxClientError() || status.is5xxServerError()) {
            // Http status >= 400 so apply onError behavior
            return callOnError(onError).apply(request, response);
        } else {
            // Http status is not error so apply onError behavior
            return behavior.getOrDefault(status, missingBehavior()).apply(request, response);
        }
    }

    /**
     * @return the CommandResponseHeader
     */
    public Header[] getCommandResponseHeaders() {
        return commandResponseHeaders;
    }

    /**
     * @return The HTTP status of the executed request.
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * @return A {@link BiFunction} to handle missing behavior definition for HTTP response's code.
     */
    private BiFunction<HttpRequestBase, HttpResponse, T> missingBehavior() {
        return (req, res) -> {
            LOGGER.error("Unable to process message for request {} (response code: {}).", req,
                    res.getStatusLine().getStatusCode());
            req.releaseConnection();
            return Defaults.<T> asNull().apply(req, res);
        };
    }

    /**
     * @param onError The {@link Supplier} to handle error cases (to throw custom exceptions).
     * @return A {@link BiFunction} that throws a {@link TalendRuntimeException exception} for proper HTTP response.
     * @see Defaults#passthrough()
     */
    private BiFunction<HttpRequestBase, HttpResponse, T> callOnError(Function<Exception, RuntimeException> onError) {
        return (req, res) -> {
            errorProcessor.rethrowError(req, res, onError);
            return null; // Not expected to get to this line.
        };
    }

    /**
     * Declares what exception should be thrown in case of error.
     *
     * @param onError A {@link Function function} that returns a {@link RuntimeException}.
     * @see org.talend.daikon.exception.TalendRuntimeException
     */
    protected void onError(Function<Exception, RuntimeException> onError) {
        this.onError = onError;
    }

    /**
     * Declares which {@link HttpRequestBase http request} to execute in command.
     *
     * @param call The {@link Supplier} to provide the {@link HttpRequestBase} to execute.
     */
    protected void execute(Supplier<HttpRequestBase> call) {
        httpCall = call;
    }

    /**
     * Starts declaration of behavior(s) to adopt when HTTP response has status code <code>status</code>.
     *
     * @param status One of more HTTP {@link HttpStatus status(es)}.
     * @return A {@link BehaviorBuilder builder} to continue behavior declaration for the HTTP status(es).
     * @see BehaviorBuilder#then(BiFunction)
     */
    protected BehaviorBuilder on(HttpStatus... status) {
        return new BehaviorBuilder(status);
    }

    /**
     * Starts declaration of behavior(s) to adopt when HTTP response has status code of 1xx.
     *
     * @return A {@link BehaviorBuilder builder} to continue behavior declaration for the HTTP status(es).
     * @see BehaviorBuilder#then(BiFunction)
     */
    protected BehaviorBuilder onInfo() {
        return on(INFO_STATUS);
    }

    /**
     * Starts declaration of behavior(s) to adopt when HTTP response has status code of 2xx.
     *
     * @return A {@link BehaviorBuilder builder} to continue behavior declaration for the HTTP status(es).
     * @see BehaviorBuilder#then(BiFunction)
     */
    protected BehaviorBuilder onSuccess() {
        return on(SUCCESS_STATUS);
    }

    /**
     * Starts declaration of behavior(s) to adopt when HTTP response has status code of 3xx.
     *
     * @return A {@link BehaviorBuilder builder} to continue behavior declaration for the HTTP status(es).
     * @see BehaviorBuilder#then(BiFunction)
     */
    protected BehaviorBuilder onRedirect() {
        return on(REDIRECT_STATUS);
    }

    // A intermediate builder for behavior definition.
    protected class BehaviorBuilder {

        private final HttpStatus[] status;

        public BehaviorBuilder(HttpStatus[] status) {
            this.status = status;
        }

        /**
         * Declares what action should be performed for the given HTTP status(es).
         *
         * @param action A {@link BiFunction function} to be executed for given HTTP status(es).
         * @see Defaults
         */
        public void then(BiFunction<HttpRequestBase, HttpResponse, T> action) {
            for (HttpStatus currentStatus : status) {
                GenericCommand.this.behavior.put(currentStatus, action);
            }
        }
    }
}
