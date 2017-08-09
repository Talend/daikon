// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.talend.daikon.exception.TalendRuntimeException;

public class PropertiesUtils {

    public static <P extends Properties, V> void injectObject(P props, V injectable) {
        injectObject(props, injectable, new HashSet<>());
    }

    public static <P extends Properties, V> void injectObject(P props, V injectable, Set<Object> alreadyUpdated) {
        if (!alreadyUpdated.add(props)) {
            return;
        }
        if (props == null) {
            return;
        }
        Class<?> cls = props.getClass();
        while (cls != Object.class) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                Annotation ann = field.getAnnotation(Inject.class);
                injectField(ann, field, props, injectable, alreadyUpdated);
            }
            cls = cls.getSuperclass();
        }
    }

    private static <P extends Properties, V> void injectField(Annotation ann, Field field, P props, V injectable,
            Set<Object> alreadyUpdated) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        if (ann != null && field.getType().isAssignableFrom(injectable.getClass())) {
            try {
                if (field.get(props) == null) {
                    field.set(props, injectable);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw TalendRuntimeException.createUnexpectedException(e);
            }
        } else if (Properties.class.isAssignableFrom(field.getType())) {
            try {
                injectObject((Properties) field.get(props), injectable, alreadyUpdated);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw TalendRuntimeException.createUnexpectedException(e);
            }
        }
    }

}
