package org.talend.logging.audit;

import java.lang.annotation.*;

/**
 * This annotation should be used on method declarations in an interface which extends {@link EventAuditLogger} to
 * provide metadata information for the event, such as category, message, log level.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AuditEvent {

    /**
     * @return
     * Category of the event. For example: activity, security, etc. The value will be converted to lower case.
     */
    String category();

    /**
     * @return
     * Text message for the event. It can include placeholders to be replaced with values from MDC context.
     * For example: "User {user.name} has logged out". The part "{user.name}" will be replaced with the value
     * from MDC if it exists.
     */
    String message() default "";

    //CHECKSTYLE:OFF
    /**
     * @return
     * Level to log this message with. Possible values: INFO, WARNING, ERROR.
     */
    //CHECKSTYLE:ON
    LogLevel level() default LogLevel.INFO;
}
