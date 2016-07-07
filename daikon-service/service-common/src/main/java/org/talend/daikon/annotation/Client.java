package org.talend.daikon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.talend.daikon.client.Access;

/**
 * Use this method to inject a service client in a class field (instead of using {@link org.talend.daikon.client.ClientService}).
 * @see org.talend.daikon.client.ClientInjector
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Client {

    /**
     * @return The preferred access for the client (defaults to {@link Access#LOCAL} then {@link Access#REMOTE}).
     */
    Access[] access() default { Access.LOCAL, Access.REMOTE };
}
