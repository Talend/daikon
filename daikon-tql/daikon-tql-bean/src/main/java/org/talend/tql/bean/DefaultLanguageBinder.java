package org.talend.tql.bean;

import static org.talend.tql.bean.MethodAccessorFactory.build;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Default {@link LanguageBinder} implementation.
 */
public class DefaultLanguageBinder implements LanguageBinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLanguageBinder.class);

    private Class targetClass;

    public DefaultLanguageBinder(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public MethodAccessor[] getMethods(String field) {
        StringTokenizer tokenizer = new StringTokenizer(field, ".");
        List<String> methodNames = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            methodNames.add(tokenizer.nextToken());
        }

        Class currentClass = targetClass;
        LinkedList<MethodAccessor> methods = new LinkedList<>();
        for (String methodName : methodNames) {
            if ("_class".equals(methodName)) {
                try {
                    methods.add(build(Class.class.getMethod("getClass")));
                    methods.add(build(Class.class.getMethod("getName")));
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Unable to get methods for class' name.", e);
                }
            } else {
                String[] getterCandidates = new String[] { "get" + WordUtils.capitalize(methodName), //
                        methodName, //
                        "is" + WordUtils.capitalize(methodName) };

                final int beforeFind = methods.size();

                // Current class type is a map, the next method will be get(key)
                if (Map.class.isAssignableFrom(currentClass)) {
                    try {
                        Method method = currentClass.getMethod("get", Object.class);
                        methods.add(new MapMethodAccessor(method, methodName));
                    } catch (Exception e) {
                        LOGGER.debug("Can't find get '{}'.", field, e);
                    }
                } else {
                    for (String getterCandidate : getterCandidates) {
                        try {
                            methods.add(build(currentClass.getMethod(getterCandidate)));
                            break;
                        } catch (Exception e) {
                            LOGGER.debug("Can't find getter '{}'.", field, e);
                        }
                    }
                }

                // No method found, try using @JsonProperty
                if (beforeFind == methods.size()) {
                    LOGGER.debug("Unable to find method, try using @JsonProperty for '{}'.", methodName);
                    final Method[] currentClassMethods = currentClass.getMethods();
                    for (Method currentClassMethod : currentClassMethods) {
                        final JsonProperty jsonProperty = currentClassMethod.getAnnotation(JsonProperty.class);
                        if (jsonProperty != null && methodName.equals(jsonProperty.value())
                                && !void.class.equals(currentClassMethod.getReturnType())) {
                            LOGGER.debug("Found method '{}' using @JsonProperty.", currentClassMethod);
                            methods.add(build(currentClassMethod));
                        }
                    }
                }

                // Check before continue
                if (beforeFind == methods.size()) {
                    throw new UnsupportedOperationException("Can't find getter '" + field + "'.");
                } else {
                    currentClass = methods.getLast().getReturnType();
                }
            }
        }
        return methods.toArray(new MethodAccessor[0]);
    }

    @Override
    public String valueOf(Object value) {
        return String.valueOf(value);
    }
}
