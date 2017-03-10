package org.talend.daikon.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.talend.daikon.annotation.Call;
import org.talend.daikon.annotation.DefaultHystrixCommand;
import org.talend.daikon.annotation.DefaultService;
import org.talend.daikon.client.ClientService;

import com.netflix.hystrix.HystrixCommand;

/**
 * An aspect to handle all method invocation when annotated with {@link Call}.
 */
@Configuration
@Aspect
public class GatewayOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayOperations.class);

    @Autowired
    ApplicationContext context;

    @Autowired
    ClientService clientService;

    /**
     * Handle {@link Call}-annotated methods.
     * @param pjp The wrapped method
     * @return The result of the service call as configured by {@link Call}.
     * @throws Throwable No exception/error is handled in aspect.
     */
    @Around("@annotation(org.talend.daikon.annotation.Call)")
    public Object call(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method m = ms.getMethod();
        final Call callAnnotation = AnnotationUtils.getAnnotation(m, Call.class);
        if (callAnnotation != null) {
            final Object[] args = pjp.getArgs();
            if (!callAnnotation.using().equals(DefaultHystrixCommand.class)) {
                if (!ClassUtils.isAssignable(callAnnotation.service(), DefaultService.class) || StringUtils.isEmpty(callAnnotation.operation())) {
                    LOGGER.warn("Method '{}' use custom invocation but also sets service and operation name", m.getName());
                }
                return handleCustomExecution(callAnnotation, args);
            } else {
                return handleServiceForward(callAnnotation, args);
            }
        }
        return pjp.proceed();
    }

    private Object handleServiceForward(Call callAnnotation, Object[] args) throws IllegalAccessException, InvocationTargetException {
        final Class<?> serviceClass = callAnnotation.service();
        final List<? extends Class<?>> argList = Stream.of(args).map(Object::getClass).collect(Collectors.toList());
        final Class[] objects = argList.toArray(new Class[argList.size()]);
        final Method method = ReflectionUtils.findMethod(serviceClass, callAnnotation.operation(), objects);
        if (method == null) {
            throw new IllegalArgumentException("Method '" + callAnnotation.operation() + "' does not exist.");
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (args.length != parameterTypes.length) {
            throw new IllegalArgumentException("Not same number of parameters.");
        }
        for (int i = 0; i < args.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                throw new IllegalArgumentException("Invalid parameter type.");
            }
        }
        final Object client = clientService.of(serviceClass);
        return method.invoke(client, args);
    }

    private Object handleCustomExecution(Call callAnnotation, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final String[] hystrixBeans = context.getBeanNamesForType(callAnnotation.using());
        if (hystrixBeans.length == 1) {
            final HystrixCommand bean = (HystrixCommand) context.getBean(hystrixBeans[0], args);
            return bean.execute();
        } else {
            final HystrixCommand newInstance = (HystrixCommand) ConstructorUtils.invokeConstructor(callAnnotation.using(), args);
            return newInstance.execute();
        }
    }

}
