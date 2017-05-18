package org.talend.daikon.hystrix;

import java.util.function.Function;

import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommandGroupKey;

// Test command
@Component
@Scope("prototype")
class TestCommand extends GenericCommand<String> {

    protected TestCommand(String url, Function<Exception, RuntimeException> errorHandling) {
        super(HystrixCommandGroupKey.Factory.asKey("dataset"));
        execute(() -> new HttpGet(url));
        onError(errorHandling);
        on(HttpStatus.OK).then(Defaults.asString());
    }
}
