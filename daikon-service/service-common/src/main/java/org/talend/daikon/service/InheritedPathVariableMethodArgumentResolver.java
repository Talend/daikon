package org.talend.daikon.service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.talend.daikon.annotation.Service;

/**
 * A {@link PathVariableMethodArgumentResolver} implementation that can look up for the {@link PathVariable} annotation
 * defined in interface annotated with {@link Service}.
 *
 * @see Service
 * @see PathVariable
 */
class InheritedPathVariableMethodArgumentResolver extends PathVariableMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return getParameterDefinition(parameter) != null;
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        final Parameter parameterDefinition = getParameterDefinition(parameter);
        PathVariable annotation = AnnotationUtils.findAnnotation(parameterDefinition, PathVariable.class);
        return new NamedValueInfo(annotation.value(), true, ValueConstants.DEFAULT_NONE);
    }

    /**
     * Returns the parameter annotated with {@link PathVariable} in service definition or <code>null</code> if not
     * found.
     * 
     * @param parameter A parameter in a method annotated with {@link org.springframework.web.bind.annotation.RequestMapping}.
     * @return The parameter from service definition or <code>null</code> if not found.
     */
    private Parameter getParameterDefinition(MethodParameter parameter) {
        final Optional<Class> serviceDefinition = Stream.of(((Class) parameter.getContainingClass()).getInterfaces()) //
                .filter(c -> AnnotationUtils.findAnnotation(c, Service.class) != null) //
                .findFirst();
        if (serviceDefinition.isPresent()) {
            final Method parameterMethod = parameter.getMethod();
            final Method method = ReflectionUtils.findMethod(serviceDefinition.get(), parameterMethod.getName(),
                    parameterMethod.getParameterTypes());
            return method.getParameters()[parameter.getParameterIndex()];
        } else {
            return null;
        }
    }

}
