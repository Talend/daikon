package org.talend.daikon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.talend.daikon.client.ClientService;
import org.talend.daikon.service.ServiceRegistrar;

/**
 * Enable all Daikon services needed configuration. When application is annotated with this:
 * <ul>
 *     <li>Enable Feign clients ({@link EnableFeignClients}.</li>
 *     <li>Scans for classes annotated with {@link Service}.</li>
 *     <li>Adds a {@link ClientService} to the context's available beans.</li>
 * </ul>
 * @see ServiceRegistrar
 * @see EnableEnunciate
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ServiceRegistrar.class, ClientService.class})
@EnableFeignClients
public @interface EnableServices {
}
