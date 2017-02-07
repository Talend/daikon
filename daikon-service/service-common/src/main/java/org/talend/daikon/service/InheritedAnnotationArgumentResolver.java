package org.talend.daikon.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.talend.daikon.annotation.Service;

/**
 * A {@link HandlerMethodArgumentResolver} implementation that can look up for the annotations
 * defined in interface annotated with {@link Service} (e.g. {@link org.springframework.web.bind.annotation.PathVariable},
 * {@link org.springframework.web.bind.annotation.RequestBody}, ...).
 *
 * @see Service
 */
class InheritedAnnotationArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(InheritedAnnotationArgumentResolver.class);

    private final ApplicationContext context;

    private List<HandlerMethodArgumentResolver> cachedContextResolvers;

    InheritedAnnotationArgumentResolver(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findContextResolver(parameter) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        final Resolution resolution = findContextResolver(parameter);
        if (resolution != null) {
            return resolution.resolver.resolveArgument(resolution.parameter, mavContainer, webRequest, binderFactory);
        } else {
            return null;
        }
    }

    // Method to get all known HandlerMethodArgumentResolver instances in context, cache result for efficiency to
    // prevent too many calls to context.getBean().
    private List<HandlerMethodArgumentResolver> getContextArgumentResolvers() {
        if (cachedContextResolvers == null) {
            final RequestMappingHandlerAdapter adapter;
            try {
                adapter = context.getBean(RequestMappingHandlerAdapter.class);
            } catch (BeansException e) {
                LOGGER.error("Unable to find instance of RequestMappingHandlerAdapter. Are you running in a web context?", e);
                return Collections.emptyList();
            }
            cachedContextResolvers = adapter.getArgumentResolvers();
        }
        return cachedContextResolvers;
    }

    private Resolution findContextResolver(MethodParameter parameter) {
        final Optional<Class> serviceDefinition = Stream.of(((Class) parameter.getContainingClass()).getInterfaces()) //
                .filter(c -> AnnotationUtils.findAnnotation(c, Service.class) != null) //
                .findFirst();
        if (serviceDefinition.isPresent()) {
            final Method parameterMethod = parameter.getMethod();
            final Method method = ReflectionUtils.findMethod(serviceDefinition.get(), parameterMethod.getName(),
                    parameterMethod.getParameterTypes());

            Resolution resolution = null;
            for (HandlerMethodArgumentResolver contextArgumentResolver : getContextArgumentResolvers()) {
                if (contextArgumentResolver == this) {
                    // Prevent stack overflow
                    continue;
                }
                final MethodParameter methodParameter = new MethodParameter(parameter) {

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public AnnotatedElement getAnnotatedElement() {
                        return method.getParameters()[parameter.getParameterIndex()];
                    }

                    @Override
                    public Annotation[] getParameterAnnotations() {
                        return getAnnotatedElement().getAnnotations();
                    }
                };
                if (contextArgumentResolver.supportsParameter(methodParameter)) {
                    resolution = new Resolution(methodParameter, contextArgumentResolver);
                    break;
                }
            }
            return resolution;
        } else {
            return null;
        }
    }

    class Resolution {

        final private MethodParameter parameter;

        final private HandlerMethodArgumentResolver resolver;

        Resolution(MethodParameter parameter, HandlerMethodArgumentResolver resolver) {
            this.parameter = parameter;
            this.resolver = resolver;
        }
    }

}
