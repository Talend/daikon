package org.talend.daikon.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * <p>
 * Scans for {@link org.talend.daikon.annotation.Service} annotated classes, as well as
 * {@link org.talend.daikon.annotation.ServiceImplementation} annotated classes.
 * </p>
 * <p>
 * The classpath is scanned starting from the package of the class annotated with
 * {@link org.talend.daikon.annotation.EnableServices} and checks all sub-packages.
 * </p>
 * 
 * @see org.talend.daikon.annotation.EnableServices
 * @see org.talend.daikon.annotation.Service
 * @see org.talend.daikon.annotation.ServiceImplementation
 * @see ServiceTypeFilter
 */
public class ServiceRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // Create the annotation-based context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, false);
        scanner.addIncludeFilter(new ServiceTypeFilter());
        final String basePackage = StringUtils.substringBeforeLast(importingClassMetadata.getClassName(), ".");
        scanner.scan(basePackage);
        // Import scanned services in current registry
        final String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            final BeanDefinition definition = context.getBeanDefinition(name);
            registry.registerBeanDefinition(name, definition);
        }

    }
}
