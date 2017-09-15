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
package org.talend.daikon.properties.property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;

/**
 * Make new {@link Property} objects.
 *
 * This is the onlyh way {@code Property} objects should be created.
 */
public class PropertyFactory {

    public static TypeLiteral<List<String>> LIST_STRING_ELEMENT = new TypeLiteral<List<String>>() {
        // This object is here to define the type used in the next property
    };

    private PropertyFactory() {
    }

    public static StringProperty newProperty(String name) {
        return newString(name);
    }

    public static StringProperty newString(String name) {
        return new StringProperty(name);
    }

    public static StringProperty newString(String name, String initialValue) {
        StringProperty property = newString(name);
        property.setValue(initialValue);
        return property;
    }

    public static StringProperty newString(String name, String initialValue, String defaultValue) {
        StringProperty property = newString(name);
        property.setValue(initialValue);
        property.setDefaultValue(defaultValue);
        return property;
    }

    public static Property<Integer> newInteger(String name) {
        return new Property<>(Integer.class, name);
    }

    public static Property<Integer> newInteger(String name, String initialValue) {
        return newInteger(name).setValue(Integer.valueOf(initialValue));
    }

    public static Property<Integer> newInteger(String name, Integer initialValue) {
        return newInteger(name).setValue(initialValue);
    }

    public static Property<Double> newDouble(String name) {
        return new Property<>(new TypeLiteral<Double>() {// left empty on purpose
        }, name);
    }

    public static Property<Double> newDouble(String name, String initialValue) {
        return newDouble(name).setValue(Double.valueOf(initialValue));
    }

    public static Property<Double> newDouble(String name, Double initialValue) {
        return newDouble(name).setValue(initialValue);
    }

    public static Property<Float> newFloat(String name) {
        return new Property<>(new TypeLiteral<Float>() {// left empty on purpose
        }, name);
    }

    public static Property<Float> newFloat(String name, String initialValue) {
        return newFloat(name).setValue(Float.valueOf(initialValue));
    }

    public static Property<Float> newFloat(String name, Float initialValue) {
        return newFloat(name).setValue(initialValue);
    }

    public static Property<Boolean> newBoolean(String name) {
        return new Property<>(new TypeLiteral<Boolean>() {// left empty on purpose
        }, name).setValue(Boolean.FALSE);
    }

    public static Property<Boolean> newBoolean(String name, String initialValue) {
        return newBoolean(name).setValue(Boolean.valueOf(initialValue));
    }

    public static Property<Boolean> newBoolean(String name, Boolean initialValue) {
        return newBoolean(name).setValue(initialValue);
    }

    public static Property<Date> newDate(String name) {
        return new Property<>(new TypeLiteral<Date>() {// left empty on purpose
        }, name);
    }

    public static Property<List<String>> newStringList(String name) {
        return new Property<>(LIST_STRING_ELEMENT, name).setValue(new ArrayList<String>());
    }

    public static <T> Property<List<T>> newList(String name, Class<T> zeListElementType) {
        TypeLiteral<List<T>> literalType = new TypeLiteral<List<T>>() {
            // This object is here to define the type used in the next property
        };
        return new Property<>(literalType, name).setValue(new ArrayList<T>());
    }

    public static <T extends Enum<T>> EnumProperty<T> newEnum(String name, Class<T> zeEnumType) {
        return new EnumProperty<>(zeEnumType, name);
    }

    public static <T extends Enum<T>> EnumListProperty<T> newEnumList(String name, TypeLiteral<List<T>> type) {
        return new EnumListProperty<T>(type, name);
    }

    public static Property<Schema> newSchema(String name) {
        return new SchemaProperty(name);
    }

    public static <T> Property<T> newProperty(Class<T> type, String name) {
        return new Property<>(type, name);
    }

    public static <T> Property<T> newProperty(Class<T> type, String name, String title) {
        return new Property<>(type, name).setTitle(title);
    }

    public static <T> Property<T> newProperty(TypeLiteral<T> type, String name) {
        return new Property<>(type, name);
    }

    public static <T> Property<T> newProperty(TypeLiteral<T> type, String name, String title) {
        return new Property<>(type, name).setTitle(title);
    }

}
