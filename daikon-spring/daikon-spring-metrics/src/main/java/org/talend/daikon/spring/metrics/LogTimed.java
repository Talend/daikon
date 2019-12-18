// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.spring.metrics;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allow to use AOP with AspectJ for logging.
 *
 * <p>
 * It identifies a method which duration should be log.
 * A log starting message is added, as well as a log stopping message with the duration.
 * This messages are customizable.
 * Two other optional arguments can be used :
 * - an additional specific message which is empty by default
 * - a log level from the enum org.slf4j.event.Level
 * </p>
 *
 * <p>
 * <b>[Note]</b> Note
 * Due to the proxy-based nature of Spring's AOP framework, protected methods are by definition not intercepted, neither
 * for JDK proxies (where this isn't applicable) nor for CGLIB proxies (where this is technically possible but not
 * recommendable for AOP purposes). As a consequence, any given pointcut will be matched against public methods only!
 * </p>
 *
 * @see org.talend.daikon.spring.metrics.config.Aspects
 * @See http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/aop.html#aop-introduction-
 * spring-defn
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.METHOD })
public @interface LogTimed {

    /**
     * @return a Boolean that indicates whether or not a start message should be displayed
     */
    boolean displayStartingMessage() default true;

    /**
     * @return a start message to be displayed before the execution of the method
     */
    String startMessage() default StringUtils.EMPTY;

    /**
     * @return an end message to be displayed after the execution of the method
     */
    String endMessage() default StringUtils.EMPTY;

    /**
     * @return an optional additional message to be displayed before the execution of the method
     */
    String additionalMessage() default StringUtils.EMPTY;

    /**
     * @return the log level of the different messages : start, additional and end message
     */
    Level logLevel() default Level.DEBUG;

}
