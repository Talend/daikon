package org.talend.daikon.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.cloud.netflix.feign.FeignClient;

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
