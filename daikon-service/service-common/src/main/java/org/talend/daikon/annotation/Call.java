package org.talend.daikon.annotation;

import java.lang.annotation.*;

import org.apache.commons.lang.StringUtils;

import com.netflix.hystrix.HystrixCommand;

/**
 * Use this annotation on any method in a class already annotated with {@link ServiceImplementation}. When a method is
 * annotated with @Call, its body will be ignored and replaced by an invocation of:
 * <ul>
 * <li>A service: using {@link #service()} and {@link #operation()}</li>
 * <li>A Hystrix command: using {@link #using()}</li>
 * </ul>
 * Please note {@link #using()} overrides all other settings (although a warning will be issued in log that the Hystrix command is
 * used but service and operation are too.
 * @see org.talend.daikon.service.GatewayOperations
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Call {

    /**
     * @return The service class to invoke
     */
    Class service() default DefaultService.class;

    /**
     * @return The operation to invoke (must be a method name within service class).
     */
    String operation() default StringUtils.EMPTY;

    /**
     * @return A class that extends {@link HystrixCommand} and will be used to invoke the service.
     */
    Class<? extends HystrixCommand> using() default DefaultHystrixCommand.class;

}
