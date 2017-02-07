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
import org.talend.daikon.properties.service.Repository;

/**
 * Helpers for the implementation methods of the {@link org.talend.daikon.properties.service.PropertiesService}.
 */
public class PropertiesDynamicMethodHelper {

    static private boolean REQUIRED = true;

    static private Method findMethod(Object obj, String type, String propertyName, boolean required) {
        if (propertyName == null || "".equals(propertyName)) {
            throw new IllegalArgumentException(
                    "The ComponentService was used to access a property with a null(or empty) property name. Type: " + type
                            + " Properties: " + obj);
        }
        String propName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String methodName = type + propName;
        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        if (required) {
            throw new IllegalStateException("Method: " + methodName + " not found");
        }
        return null;
    }

    static private void doInvoke(Properties props, Method m) throws Throwable {
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

    static public void validateProperty(Properties props, String propName) throws Throwable {
        Method m = findMethod(props, Properties.METHOD_VALIDATE, propName, REQUIRED);
        try {
            m.setAccessible(true);
            ((PropertiesImpl) props).setValidationResult((ValidationResult) m.invoke(props));
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    static public void beforePropertyActivate(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED));
    }

    static public void beforePropertyPresent(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE, propName, REQUIRED));
    }

    static public void afterProperty(Properties props, String propName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER, propName, REQUIRED));
    }

    static public void beforeFormPresent(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_BEFORE_FORM, formName, REQUIRED));
    }

    static public void afterFormNext(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_NEXT, formName, REQUIRED));
    }

    static public void afterFormBack(Properties props, String formName) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_BACK, formName, REQUIRED));
    }

    static public void afterFormFinish(Properties props, String formName, Repository repostory) throws Throwable {
        doInvoke(props, findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, formName, REQUIRED), repostory);
    }

    static public void setFormLayoutMethods(Properties props, String property, Form form) {
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
        m = findMethod(props, Properties.METHOD_AFTER_FORM_FINISH, property, !REQUIRED);
        if (m != null) {
            form.setCallAfterFormFinish(true);
        }
    }

    static public void setWidgetLayoutMethods(Properties props, String property, Widget widget) {
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
