package org.talend.daikon.hystrix.processors;

import java.util.function.Function;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public interface ErrorProcessor {
    void rethrowError(HttpRequestBase request, HttpResponse response, Function<Exception, RuntimeException> onError);
}
