package org.talend.daikon.logging.http.headers;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
@RestController
public class TestController {

    static final Map<String, Map<String, String>> MDCs = new LinkedHashMap<>();

    @RequestMapping("/{id}")
    public String test(@PathVariable("id") String id) {
        System.out.println("Received request with test id: " + id);
        MDCs.put(id, MDC.getCopyOfContextMap());
        return "test:" + id;
    }
}
