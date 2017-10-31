// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.error.PropertiesErrorCode;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.runtime.RuntimeContext;
import org.talend.daikon.properties.service.Repository;

/**
 * Helpers for the implementation methods of the {@link org.talend.daikon.properties.service.PropertiesService}.
 */
public final class PropertiesDynamicMethodHelper {

    static private boolean REQUIRED = true;

    private PropertiesDynamicMethodHelper() {
        // Utility class should not have public constructor
    }

    /**
     * Finds public trigger method of specified <code>type</code> for specified property. If method is <code>required</code>,
     * but method wasn't found, then {@link IllegalArgumentException} is thrown. If method is not <code>required</code> and it
     * wasn't
     * found, then <code>null</code> is returned
     * 
     * @param instance instance whose method is being searched for
     * @param methodType method type, i.e. prefix used before property name part in method name
     * @param propertyName name of property, which owns the trigger
     * @param required specify whether method is required
     * @param the list of parameter
     * @return found method or <code>null</code> if method wasn't found and not required
     */
    static Method findMethod(Object instance, String methodType, String propertyName, boolean required,
            Class<?>... parameterTypes) {
        if (instance == null) {
            throw new NullPointerException("Instance whose method is being searched for should not be null");
        }
        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException(
                    "The ComponentService was used to access a property with a null(or empty) property name. Type: " + methodType
                            + " Properties: " + instance);
        }
        String methodName = methodType + capitalizeFirstLetter(propertyName);
        try {
            return instance.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (required) {
                throw new IllegalArgumentException("Method: " + methodName + " not found", e);
            } else {
                return null;
            }
        }
    }

    /**
     * Capitalizes first letter of specified <code>str</code>
     * E.g. "test" becomes "Test"
     * 
     * @param str string to capitalize
     * @return string with first letter capitalized
     */
    private static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static void doInvoke(Properties props, Method m) throws Throwable {
        try {
            m.setAccessible(true);
            Object result = m.invoke(props);
            PropertiesDynamicMethodHelper.storeResult(props, result);
        } catch (InvocationTargetException e) {
            throw new TalendRuntimeException(PropertiesErrorCode.FAILED_INVOKE_METHOD, e, ExceptionContext.withBuilder()
                    .put("class", m.getDeclaringClass().getCanonicalName()).put("method", m.getName()).build());
        }
    }

    public static void storeResult(Properties props, Object result) {
        if (result instanceof ValidationResult) {
            ((PropertiesImpl) props).setValidationResult((ValidationResult) result);
        } else {
            ((PropertiesImpl) props).setValidationResult(ValidationResult.OK);
        }
    }

    static void doInvoke(Properties props, Method m, Object... arguments) throws Throwable {
        try {
            m.setAccessible(true);
            Object result = m.invoke(props, arguments);
            storeResult(props, result);
        } catch (IllegalArgumentException | InvocationTargetException e) {
            throw new TalendRuntimeException(PropertiesErrorCode.FAILED_INVOKE_METHOD, e, ExceptionContext.withBuilder()
                    .put("class", m.getDeclaringClass().getCanonicalName()).put("method", m.getName()).build());
        }
    }

    public static void validateProperty(Properties props, String propName) throws Throwable {
        Method m = findMethod(props, Properties.METHOD_VALIDATE, propName, REQUIRED);
        try {
            m.setAccessible(true);
            ((PropertiesImpl) props).setValidationResult((ValidationResult) m.invoke(props));
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * Finds {@code validate<PropertyName>} overloaded method with {@link RuntimeContext} parameter and calls it using Reflection
     * API
     * 
     * @param props {@link Properties} instance
     * @param propName property name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code validate<PropertyName>} method
     */

    /**
     * Checks whether {@code validate<PropertyName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code validate<PropertyName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code validate<PropertyName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method.
     * Sets the result of {@code validate<PropertyName>} method invocation as {@link Properties} instance {@link ValidationResult}
     * 
     * @param props {@link Properties} instance
     * @param propName property name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code after<PropertyName>} method
     */
    public static void validateProperty(Properties props, String propName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_VALIDATE, propName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_VALIDATE, propName, !REQUIRED, RuntimeContext.class);
        try {
            ValidationResult result;
            if (callbackWithParameters != null) {
                result = (ValidationResult) callbackWithParameters.invoke(props, context);
            } else {
                result = (ValidationResult) callbackWithoutParameters.invoke(props);
            }
            ((PropertiesImpl) props).setValidationResult(result);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public static void beforePropertyActivate(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED));
    }

    /**
     * Checks whether {@code before<PropertyName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code before<PropertyName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code before<PropertyName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param propName property name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code before<PropertyName>} method
     */
    public static void beforePropertyActivate(Properties props, String propName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_BEFORE, propName, !REQUIRED, RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void beforePropertyPresent(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED));
    }

    /**
     * Checks whether {@code before<PropertyName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code before<PropertyName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code before<PropertyName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param propName property name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code before<PropertyName>} method
     */
    public static void beforePropertyPresent(Properties props, String propName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_BEFORE, propName, !REQUIRED, RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void afterProperty(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER, propName, REQUIRED));
    }

    /**
     * Checks whether {@code after<PropertyName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code after<PropertyName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code after<PropertyName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param propName property name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code after<PropertyName>} method
     */
    public static void afterProperty(Properties props, String propName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_AFTER, propName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_AFTER, propName, !REQUIRED, RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void beforeFormPresent(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE_FORM, formName, REQUIRED));
    }

    /**
     * Checks whether {@code beforeFormPresent<FormName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code beforeFormPresent<FormName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code beforeFormPresent<FormName>} method with {@link RuntimeContext}
     * parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param formName {@link Form} name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code beforeFormPresent<FormName>} method
     */
    public static void beforeFormPresent(Properties props, String formName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_BEFORE_FORM, formName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_BEFORE_FORM, formName, !REQUIRED,
                RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void afterFormNext(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_NEXT, formName, REQUIRED));
    }

    /**
     * Checks whether {@code afterFormNext<FormName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code afterFormNext<FormName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code afterFormNext<FormName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param formName {@link Form} name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code afterFormNext<FormName>} method
     */
    public static void afterFormNext(Properties props, String formName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_AFTER_FORM_NEXT, formName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_AFTER_FORM_NEXT, formName, !REQUIRED,
                RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void afterFormBack(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_BACK, formName, REQUIRED));
    }

    /**
     * Checks whether {@code afterFormBack<FormName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code afterFormBack<FormName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code afterFormBack<FormName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param formName {@link Form} name to which trigger method is related
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code afterFormBack<FormName>} method
     */
    public static void afterFormBack(Properties props, String formName, RuntimeContext context) throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_AFTER_FORM_BACK, formName, REQUIRED);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_AFTER_FORM_BACK, formName, !REQUIRED,
                RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            doInvoke(props, callbackWithoutParameters);
        }
    }

    public static void afterFormFinish(Properties props, String formName, Repository repostory) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, formName, REQUIRED, Repository.class), repostory);
    }

    /**
     * Checks whether {@code afterFormFinish<FormName>} method without parameters is present in {@link Properties} class.
     * It must be present, when {@code afterFormFinish<FormName>} with {@link RuntimeContext} parameter is present in the class.
     * If it is not present {@link IllegalArgumentException} is thrown to indicate that method not found.
     * If it is present, than method tries to find {@code afterFormFinish<FormName>} method with {@link RuntimeContext} parameter.
     * If it finds overloaded method, it calls it. Otherwise, it calls original method
     * 
     * @param props {@link Properties} instance
     * @param formName {@link Form} name to which trigger method is related
     * @param repository {@link Repository} instance which is used to save properties
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code afterFormFinish<FormName>} method
     */
    public static void afterFormFinish(Properties props, String formName, Repository repostory, RuntimeContext context)
            throws Throwable {
        Method callbackWithoutParameters = findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, formName, REQUIRED,
                Repository.class);
        Method callbackWithParameters = findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, formName, !REQUIRED,
                Repository.class, RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, repostory, context);
        } else {
            doInvoke(props, callbackWithoutParameters, repostory);
        }
    }

    public static void afterReference(Properties props, ReferenceProperties<Properties> refProp) throws Throwable {
        Method afterRefCallback = findMethod(props, Properties.METHOD_AFTER, refProp.getName(), !REQUIRED);
        if (afterRefCallback != null) {
            doInvoke(props, afterRefCallback);
        } // else not method to call back so ignores it
    }

    /**
     * Tries to find {@code after<ReferencePropertyName>} method with parameters in {@link Properties} class.
     * If it is present, it is called. If it is not present, then finds method without parameters and call it.
     * If it is also not present, then nothing happens.
     * 
     * @param props {@link Properties} instance
     * @param refProp instance of {@link ReferenceProperties}
     * @param context {@link RuntimeContext} provided by Platform which is calling this service
     * @throws Throwable any Exception thrown by {@code after<ReferencePropertyName>} method
     */
    public static void afterReference(Properties props, ReferenceProperties<Properties> refProp, RuntimeContext context)
            throws Throwable {
        Method callbackWithParameters = findMethod(props, Properties.METHOD_AFTER, refProp.getName(), !REQUIRED,
                RuntimeContext.class);
        if (callbackWithParameters != null) {
            doInvoke(props, callbackWithParameters, context);
        } else {
            Method callbackWithoutParameters = findMethod(props, Properties.METHOD_AFTER, refProp.getName(), !REQUIRED);
            if (callbackWithoutParameters != null) {
                doInvoke(props, callbackWithoutParameters);
            }
        } // else no method to call back, so ignore it
    }

    public static void setFormLayoutMethods(Properties props, String property, Form form) {
        Method m;
        m = findMethod(props, Properties.METHOD_BEFORE_FORM, property, !REQUIRED);
        if (m != null) {
            form.setCallBeforeFormPresent(true);
        }
        m = findMethod(props, Properties.METHOD_AFTER_FORM_BACK, property, !REQUIRED);
        if (m != null) {
            form.setCallAfterFormBack(true);
        }
        m = findMethod(props, Properties.METHOD_AFTER_FORM_NEXT, property, !REQUIRED);
        if (m != null) {
            form.setCallAfterFormNext(true);
        }
        m = findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, property, !REQUIRED, Repository.class);
        if (m != null) {
            form.setCallAfterFormFinish(true);
        }
    }

    public static void setWidgetLayoutMethods(Properties props, String property, Widget widget) {
        Method m;
        m = findMethod(props, Properties.METHOD_BEFORE, property, !REQUIRED);
        if (m != null) {
            widget.setCallBefore(true);
        }
        m = findMethod(props, Properties.METHOD_AFTER, property, !REQUIRED);
        if (m != null) {
            widget.setCallAfter(true);
        }
        m = findMethod(props, Properties.METHOD_VALIDATE, property, !REQUIRED);
        if (m != null) {
            widget.setCallValidate(true);
        }
    }

}
