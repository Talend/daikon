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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.strings.ToStringIndentUtil;

/**
 * A property that is part of a {@link Properties}.
 */
public class Property extends SimpleNamedThing implements AnyProperty {

    private static final String I18N_PROPERTY_PREFIX = "property."; //$NON-NLS-1$

    public enum Type {
        STRING,
        BOOLEAN,
        INT,
        DATE,
        DATETIME,
        DECIMAL,
        FLOAT,
        DOUBLE,
        BYTE_ARRAY,
        ENUM,
        DYNAMIC,
        GROUP,
        SCHEMA
    }

    public static int INFINITE = -1;

    protected EnumSet<Flags> flags;

    private Map<String, Object> taggedValues = new HashMap<>();

    private Object storedValue;

    transient private PropertyValueEvaluator propertyValueEvaluator;

    public enum Flags {
        /**
         * Encrypt this when storing the {@link Properties} into a serializable form.
         */
        ENCRYPT,
        /**
         * Don't log this value in any logs.
         */
        SUPPRESS_LOGGING;
    };

    private Type type;

    private int size;

    private int occurMinTimes;

    private int occurMaxTimes;

    // Number of decimal places - DI
    private int precision;

    // Used for date conversion - DI
    private String pattern;

    private String defaultValue;

    private boolean nullable;

    private Class<?> enumClass;

    private List<?> possibleValues;

    protected List<Property> children = new ArrayList<>();;

    public Property(String name, String title) {
        this(null, name, title);
    }

    public Property(Type type, String name, String title) {
        setName(name);
        setType(type == null ? Type.STRING : type);
        setTitle(title);
        setSize(-1);
    }

    public Property(Type type, String name) {
        this(type, name, null);
    }

    @Override
    public String getName() {
        return name;
    }

    public Property setName(String name) {
        this.name = name;
        return this;
    }

    public Property setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public Property setTitle(String title) {
        this.title = title;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Property setType(Type type) {
        this.type = type;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Property setSize(int size) {
        this.size = size;
        return this;
    }

    public boolean isSizeUnbounded() {
        if (size == -1) {
            return true;
        }
        return false;
    }

    public int getOccurMinTimes() {
        return occurMinTimes;
    }

    public Property setOccurMinTimes(int times) {
        this.occurMinTimes = times;
        return this;
    }

    public int getOccurMaxTimes() {
        return occurMaxTimes;
    }

    public Property setOccurMaxTimes(int times) {
        this.occurMaxTimes = times;
        return this;
    }

    public boolean isRequired() {
        return occurMinTimes > 0;
    }

    public Property setRequired() {
        return setRequired(true);
    }

    public Property setRequired(boolean required) {
        setOccurMinTimes(1);
        setOccurMaxTimes(1);
        return this;
    }

    public int getPrecision() {
        return precision;
    }

    public Property setPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public Property setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Property setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Property setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public Property setEnumClass(Class<?> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public List<?> getPossibleValues() {
        return possibleValues;
    }

    public Property setPossibleValues(List<?> possibleValues) {
        this.possibleValues = possibleValues;
        return this;
    }

    public Property setPossibleValues(Object... values) {
        this.possibleValues = Arrays.asList(values);
        return this;
    }

    public List<Property> getChildren() {
        return children;
    }

    public Property setChildren(List<Property> children) {
        this.children = children;
        return this;
    }

    public Property addChild(Property child) {
        children.add(child);
        return this;
    }

    public Property getChild(String name) {
        if (children != null) {
            for (Property child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public Map<String, Property> getChildMap() {
        Map<String, Property> map = new HashMap<>();
        for (Property se : getChildren()) {
            map.put(se.getName(), se);
        }
        return map;
    }

    public Property(String name) {
        this(name, null);
    }

    public EnumSet<Flags> getFlags() {
        return flags;
    }

    public Property setFlags(EnumSet<Flags> flags) {
        this.flags = flags;
        return this;
    }

    public boolean isFlag(Flags flag) {
        if (flags == null) {
            return false;
        }
        return flags.contains(flag);
    }

    public void setValue(Object value) {
        Object valueToSet = value;
        if (value != null && getType() == Type.SCHEMA && value instanceof Schema) {
            valueToSet = value.toString();
        }
        storedValue = valueToSet;
    }

    public Object getStoredValue() {
        return storedValue;
    }

    /**
     * @return the value of the property. This value may not be the one Stored with setValue(), it may be evaluated with
     * {@link PropertyValueEvaluator}.
     * 
     * 
     */
    public Object getValue() {
        if (propertyValueEvaluator != null) {
            return propertyValueEvaluator.evaluate(this, storedValue);
        } // else not evaluator so return the storedValue
        return storedValue;
    }

    /**
     * @return cast the getValue() into a boolean or return false if null.
     */
    public boolean getBooleanValue() {
        return Boolean.valueOf(String.valueOf(getValue()));
    }

    /**
     * @return cast the getValue() into a String.
     */
    public String getStringValue() {
        Object value = getValue();
        if (value != null) {
            // Schemas are serialized to JSON String via their built-in toString() method.
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * @return convert the storedValue to an int, return 0 if values is null.
     */
    public int getIntValue() {
        Object value = getValue();
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public Calendar getCalendarValue() {
        Object value = getValue();
        if (value instanceof Calendar) {
            return (Calendar) value;
        }
        if (value instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) value);
            return calendar;
        }
        throw new IllegalStateException(this + " is not instance of Date nor Calendar");
    }

    @Override
    public String toString() {
        return "Property: " + getName();
    }

    /**
     * If no displayName was specified then the i18n key : {@value Property#I18N_PROPERTY_PREFIX}.name_of_this_property.
     * {@value SimpleNamedThing#I18N_DISPLAY_NAME_SUFFIX} to find the value from the i18n.
     */
    @Override
    public String getDisplayName() {
        return displayName != null ? displayName : getI18nMessage(I18N_PROPERTY_PREFIX + name + I18N_DISPLAY_NAME_SUFFIX);
    }

    /**
     * This store a value with the given key in a map this will be serialized with the component. This may be used to
     * identify the context of the value, whether is may be some java string or some context value or system properties.
     * Use this tag a will as long as the value is serializable.
     * 
     * @param key, key to store the object with
     * @param value, any serializable object.
     */
    public void setTaggedValue(String key, Object value) {
        taggedValues.put(key, value);
    }

    /**
     * return the previously stored value using {@link Property#setTaggedValue(String, Object)} and the given key.
     * 
     * @param key, identify the value to be fetched
     * @return the object stored along with the key or null if none found.
     */
    public Object getTaggedValue(String key) {
        return taggedValues.get(key);
    }

    public void setValueEvaluator(PropertyValueEvaluator ve) {
        this.propertyValueEvaluator = ve;
    }

    @Override
    public void accept(AnyPropertyVisitor visitor, Properties parent) {
        visitor.visit(this, parent);
    }

    public String toStringIndent(int indent) {
        return ToStringIndentUtil.indentString(indent) + getName();
    }

}
