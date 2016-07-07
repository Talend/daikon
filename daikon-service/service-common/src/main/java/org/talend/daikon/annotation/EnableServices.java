package org.talend.daikon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.talend.daikon.service.ServiceRegistrar;

/**
 * Enable all Daikon services needed configuration. When application is annotated with this:
 * <ul>
 *     <li>Enable Feign clients ({@link EnableFeignClients}.</li>
 *     <li>Scans for classes annotated with {@link Service}.</li>
 * </ul>
 * @see ServiceRegistrar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ServiceRegistrar.class)
@EnableFeignClients
public @interface EnableServices {
}
