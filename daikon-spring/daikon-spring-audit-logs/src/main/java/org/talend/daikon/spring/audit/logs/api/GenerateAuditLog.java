package org.talend.daikon.spring.audit.logs.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generate audit log annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateAuditLog {

    /**
     * Application from which the log is generated
     * 
     * @return the name of the application
     */
    String application();

    /**
     * Type of the generated event
     * 
     * @return event type
     */
    String eventType();

    /**
     * Category of the generated event
     * 
     * @return Event catrgory
     */
    String eventCategory();

    /**
     * Operation of the generated event
     * 
     * @return Event operation
     */
    String eventOperation();

    /**
     * Should include or not the HTTP response body in the generated audit log
     * 
     * @return boolean indicating if the HTTP response body is included or not
     */
    boolean includeBodyResponse() default true;

    /**
     * Filter whose purpose is to filter HTTP request/response before generation
     * 
     * @return Filter class
     */
    Class<? extends AuditContextFilter> filter() default NoOpAuditContextFilter.class;
}
