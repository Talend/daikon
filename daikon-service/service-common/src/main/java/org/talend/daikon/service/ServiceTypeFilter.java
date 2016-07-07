package org.talend.daikon.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.talend.daikon.annotation.Call;
import org.talend.daikon.annotation.Service;
import org.talend.daikon.annotation.ServiceImplementation;

/**
 * A {@link TypeFilter} implementation that matches on beans that implements an interface with {@link Service} annotation.
 */
class ServiceTypeFilter implements TypeFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTypeFilter.class);

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        final ClassMetadata classMetadata = metadataReader.getClassMetadata();
        try {
            final Class<?> clazz = Class.forName(classMetadata.getClassName());
            final ServiceImplementation implementationAnnotation = AnnotationUtils.findAnnotation(clazz,
                    ServiceImplementation.class);
            final Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (AnnotationUtils.getAnnotation(method, Call.class) != null && !Modifier.isNative(method.getModifiers())) {
                    LOGGER.error(
                            "Method '{}' in '{}' is annotated with @{} but not declared as native: code inside method will be ignored.",
                            method.getName(), clazz.getName(), Call.class.getName());
                }
            }

            final String[] interfaceNames = classMetadata.getInterfaceNames();
            boolean hasMatch = false;
            for (String interfaceName : interfaceNames) {
                final Service annotation = Class.forName(interfaceName).getAnnotation(Service.class);
                if (annotation != null) {
                    if (implementationAnnotation == null) {
                        LOGGER.warn("Service '{}' is not annotated with @{}, and will not be exposed as REST service",
                                classMetadata.getClassName(), ServiceImplementation.class.getName());
                    }
                    hasMatch = true;
                    break;
                }
            }
            return hasMatch;
        } catch (Throwable e) { // NOSONAR
            if (!LOGGER.isDebugEnabled()) {
                LOGGER.error("Unable to filter class {}.", classMetadata.getClassName());
            } else {
                LOGGER.debug("Unable to filter class {}.", classMetadata.getClassName(), e);
            }
        }
        return false;
    }
}
