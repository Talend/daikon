package org.talend.daikon.spring.audit.logs.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateAuditLog {

    String application();

    String eventType();

    String eventCategory();

    String eventOperation();

    boolean includeBodyResponse() default true;

    Class<? extends AuditContextFilter> filter() default NoOpAuditContextFilter.class;
}
