package org.talend.daikon.logging.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleApplication.class, args); // NOSONAR
    }

    @RestController
    public static class SimpleEndpoint {

        @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
        String get(@RequestParam(name = "q", defaultValue = "daikon") String query) {
            return new StringBuilder().append("{\"message\":\"hello").append(query).append("\"}").toString();
        }
    }
}
