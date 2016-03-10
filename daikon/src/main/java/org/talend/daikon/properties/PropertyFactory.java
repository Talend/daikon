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

/**
 * Make new {@link Property} objects.
 */
public class PropertyFactory {

    public static Property newProperty(String name) {
        return new Property(name);
    }

    public static Property newProperty(String name, String title) {
        return new Property(name, title);
    }

    public static Property newProperty(Property.Type type, String name, String title) {
        return new Property(type, name, title);
    }

    public static Property newProperty(Property.Type type, String name) {
        return new Property(type, name);
    }

    public static Property newString(String name) {
        return new Property(Property.Type.STRING, name);
    }

    public static Property newInteger(String name) {
        return new Property(Property.Type.INT, name);
    }

    public static Property newInteger(String name, String defaultValue) {
        Property property = newInteger(name);
        property.setDefaultValue(defaultValue);
        return property;
    }

    public static Property newInteger(String name, Integer defaultValue) {
        return newInteger(name, defaultValue.toString());
    }

    public static Property newDouble(String name) {
        return new Property(Property.Type.DOUBLE, name);
    }

    public static Property newDouble(String name, String defaultValue) {
        Property property = newDouble(name);
        property.setDefaultValue(defaultValue);
        return property;
    }

    public static Property newDouble(String name, Double defaultValue) {
        return newDouble(name, defaultValue.toString());
    }

    public static Property newFloat(String name) {
        return new Property(Property.Type.FLOAT, name);
    }

    public static Property newFloat(String name, String defaultValue) {
        Property property = newFloat(name);
        property.setDefaultValue(defaultValue);
        return property;
    }

    public static Property newFloat(String name, Float defaultValue) {
        return newFloat(name, defaultValue.toString());
    }

    public static Property newBoolean(String name) {
        return new Property(Property.Type.BOOLEAN, name);
    }

    public static Property newBoolean(String name, String defaultValue) {
        Property property = newBoolean(name);
        property.setDefaultValue(defaultValue);
        return property;
    }

    public static Property newBoolean(String name, Boolean defaultValue) {
        return newBoolean(name, defaultValue.toString());
    }

    public static Property newDate(String name) {
        return new Property(Property.Type.DATE, name);
    }

    public static Property newEnum(String name) {
        return new Property(Property.Type.ENUM, name);
    }

    public static Property newEnum(String name, Object... values) {
        Property property = new Property(Property.Type.ENUM, name);
        property.setPossibleValues(values);
        return property;
    }

}
