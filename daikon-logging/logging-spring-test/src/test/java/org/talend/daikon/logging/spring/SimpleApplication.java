package org.talend.daikon.logging.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleApplication.class, args); // NOSONAR
    }

    @RestController
    public static class SimpleEndpoint {

        @GetMapping("/hello")
        String get() {
            return "hello daikon";
        }
    }
}
