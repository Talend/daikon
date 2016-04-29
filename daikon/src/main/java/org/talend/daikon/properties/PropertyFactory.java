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

import java.util.Date;

import org.apache.avro.Schema;

/**
 * Make new {@link Property} objects.
 */
public class PropertyFactory {

    public static Property<String> newProperty(String name) {
        return new Property<>(String.class, name);
    }

    public static Property<String> newProperty(String name, String title) {
        return new Property<>(String.class, name, title);
    }

    public static Property<String> newString(String name) {
        return newProperty(name);
    }

    public static Property<String> newString(String name, String initialValue) {
        Property<String> property = newString(name);
        property.setValue(initialValue);
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
        return new Property<>(Double.class, name);
    }

    public static Property<Double> newDouble(String name, String initialValue) {
        return newDouble(name).setValue(Double.valueOf(initialValue));
    }

    public static Property<Double> newDouble(String name, Double initialValue) {
        return newDouble(name).setValue(initialValue);
    }

    public static Property<Float> newFloat(String name) {
        return new Property<>(Float.class, name);
    }

    public static Property<Float> newFloat(String name, String initialValue) {
        return newFloat(name).setValue(Float.valueOf(initialValue));
    }

    public static Property<Float> newFloat(String name, Float initialValue) {
        return newFloat(name).setValue(initialValue);
    }

    public static Property<Boolean> newBoolean(String name) {
        return new Property<>(Boolean.class, name).setValue(Boolean.FALSE);
    }

    public static Property<Boolean> newBoolean(String name, String initialValue) {
        return newBoolean(name).setValue(Boolean.valueOf(initialValue));
    }

    public static Property<Boolean> newBoolean(String name, Boolean initialValue) {
        return newBoolean(name).setValue(initialValue);
    }

    public static Property<Date> newDate(String name) {
        return new Property<>(Date.class, name);
    }

    public static <T extends Enum<T>> Property<T> newEnum(String name, Class<T> zeEnumType) {
        Property<T> enumProperty = new Property<>(zeEnumType, name);
        // set the possible values accoording with all the enum types.
        // I don't think it is necessary though.
        Enum<T>[] enumConstants = zeEnumType.getEnumConstants();
        enumProperty.setPossibleValues(enumConstants);
        return enumProperty;
    }

    // public static Property newEnum(String name, Object... values) {
    // Property property = new Property(Property.Type.ENUM, name);
    // property.setPossibleValues(values);
    // return property;
    // }

    public static Property<Schema> newSchema(String name) {
        return new SchemaProperty(name);
    }

    public static <T> Property<T> newProperty(Class<T> type, String name) {
        return new Property<>(type, name);
    }

    public static <T> Property<T> newProperty(Class<T> type, String name, String title) {
        return new Property<>(type, name).setTitle(title);
    }

}
