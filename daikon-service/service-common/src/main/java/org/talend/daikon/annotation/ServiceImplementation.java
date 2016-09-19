package org.talend.daikon.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.web.bind.annotation.RestController;

/**
 * This annotation indicates a {@link Service service} implementation. For a proper service registration (and make the service
 * implementation accessible through REST), service implementation must be annotated with this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@RestController
public @interface ServiceImplementation {
}
