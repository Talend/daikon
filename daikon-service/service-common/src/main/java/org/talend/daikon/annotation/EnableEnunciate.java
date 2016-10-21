package org.talend.daikon.annotation;

import org.springframework.context.annotation.Import;
import org.talend.daikon.documentation.DocumentationController;
import org.talend.daikon.service.ServiceRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Enunciate services documentation. When application is annotated with this:
 * <ul>
 * <li>Enable "/docs" for documentation (see {@link DocumentationController})</li>
 * </ul>
 * 
 * @see ServiceRegistrar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DocumentationController.class)
public @interface EnableEnunciate {
}
