package org.talend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.talend.daikon.annotation.EnableServices;

/**
 * A default {@link SpringBootApplication spring boot application} that may be reused by all services if they don't do anything specific in Spring context
 * creation.
 * 
 * @see EnableServices
 */
@SpringBootApplication
@EnableServices
public class Application {

    public static void main(String[] args) { //NOSONAR
        SpringApplication.run(Application.class, args); //NOSONAR
    }
}
