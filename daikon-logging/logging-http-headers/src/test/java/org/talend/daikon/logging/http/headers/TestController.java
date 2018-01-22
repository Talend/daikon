package org.talend.daikon.logging.http.headers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class TestController {

    static final Map<String, Map<String, String>> MDCs = new LinkedHashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public String test(@PathVariable("id") String id) {
        System.out.println("Received request with test id: " + id);
        MDCs.put(id, MDC.getCopyOfContextMap());
        return "test:" + id;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{id}")
    public Callable<String> async(@PathVariable("id") String id) {
        return () -> {
            MDCs.put(id, MDC.getCopyOfContextMap());
            return "async:" + id;
        };
    }
}
