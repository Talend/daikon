package org.talend.daikon.annotation;

import org.springframework.cloud.openfeign.FeignClient;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotate an interface to expose it as a Daikon service.
 * @see FeignClient
 */
@Retention(RetentionPolicy.RUNTIME)
@FeignClient
@Inherited
public @interface Service {

    /**
     * @return A unique name for the service.
     */
    String name();
}
