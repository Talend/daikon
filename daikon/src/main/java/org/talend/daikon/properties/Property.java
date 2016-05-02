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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.strings.ToStringIndentUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A property that is part of a {@link Properties}.
 */
public class Property<T> extends SimpleNamedThing implements AnyProperty {

    private static final String I18N_PROPERTY_PREFIX = "property."; //$NON-NLS-1$

    // public enum Type {
    // STRING,
    // BOOLEAN,
    // INT,
    // DATE,
    // DATETIME,
    // DECIMAL,
    // FLOAT,
    // DOUBLE,
    // BYTE_ARRAY,
    // ENUM,
    // DYNAMIC,
    // GROUP,
    // SCHEMA
    // }

    public static final int INFINITE = -1;

    protected EnumSet<Flags> flags;

    private Map<String, Object> taggedValues = new HashMap<>();

    protected Object storedValue;

    transient protected PropertyValueEvaluator propertyValueEvaluator;

    public enum Flags {
        /**
         * Encrypt this when storing the {@link Properties} into a serializable form.
         */
        ENCRYPT,
        /**
         * Don't log this value in any logs.
         */
        SUPPRESS_LOGGING,
        /**
         * Used only at design time, not necessary for runtime.
         */
        DESIGN_TIME_ONLY,
        /**
         * Hidden at runtime. Normally automatically set by
         * {@link org.talend.daikon.properties.presentation.Widget#setHidden(boolean)} However, this can also be set or
         * cleared independently. This is used to cause properties to not be visible and processed at runtime if
         * necessary.
         */
        HIDDEN;

    }

    private int size;

    private int occurMinTimes;

    private int occurMaxTimes;

    // Number of decimal places - DI
    private int precision;

    // Used for date conversion - DI
    private String pattern;

    private boolean nullable;

    private List<?> possibleValues;

    protected List<Property<?>> children = new ArrayList<>();

    private Class<T> currentType;;

    public Property(Class<T> type, String name, String title) {
        this.currentType = type;
        setName(name);
        setTitle(title);
        setSize(-1);
    }

    public Property(Class<T> type, String name) {
        this(type, name, null);
    }

    @Override
    public String getName() {
        return name;
    }

    public Property<T> setName(String name) {
        this.name = name;
        return this;
    }

    public Property<T> setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public Property<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public Class<T> getType() {
        return currentType;
    }

    public int getSize() {
        return size;
    }

    public Property<T> setSize(int size) {
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

    public Property<T> setOccurMinTimes(int times) {
        this.occurMinTimes = times;
        return this;
    }

    public int getOccurMaxTimes() {
        return occurMaxTimes;
    }

    public Property<T> setOccurMaxTimes(int times) {
        this.occurMaxTimes = times;
        return this;
    }

    public boolean isRequired() {
        return occurMinTimes > 0;
    }

    public Property<T> setRequired() {
        return setRequired(true);
    }

    public Property<T> setRequired(boolean required) {
        setOccurMinTimes(1);
        setOccurMaxTimes(1);
        return this;
    }

    public int getPrecision() {
        return precision;
    }

    public Property<T> setPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public Property<T> setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Property<T> setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public List<?> getPossibleValues() {
        return possibleValues;
    }

    @JsonIgnore
    // to avoid swagger to fail because of the 2 similar following methods.
    public Property<T> setPossibleValues(List<?> possibleValues) {
        this.possibleValues = possibleValues;
        return this;
    }

    public Property<T> setPossibleValues(Object... values) {
        this.possibleValues = Arrays.asList(values);
        return this;
    }

    public List<Property<?>> getChildren() {
        return children;
    }

    public Property<T> setChildren(List<Property<?>> children) {
        this.children = children;
        return this;
    }

    public Property<T> addChild(Property<?> child) {
        children.add(child);
        return this;
    }

    public Property<?> getChild(String aName) {
        if (children != null) {
            for (Property<?> child : children) {
                if (child.getName().equals(aName)) {
                    return child;
                }
            }
        }
        return null;
    }

    public Map<String, Property<?>> getChildMap() {
        Map<String, Property<?>> map = new HashMap<>();
        for (Property<?> se : getChildren()) {
            map.put(se.getName(), se);
        }
        return map;
    }

    public EnumSet<Flags> getFlags() {
        return flags;
    }

    public Property<T> setFlags(EnumSet<Flags> flags) {
        this.flags = flags;
        return this;
    }

    public boolean isFlag(Flags flag) {
        if (flags == null) {
            return false;
        }
        return flags.contains(flag);
    }

    public void addFlag(Flags flag) {
        if (flags == null) {
            flags = EnumSet.of(flag);
        } else {
            // Work around https://github.com/jdereg/json-io/issues/72
            EnumSet<Flags> newFlags = EnumSet.of(flag);
            newFlags.addAll(flags);
            flags = newFlags;
        }

    }

    public void removeFlag(Flags flag) {
        if (flags != null) {
            flags.remove(flag);
        }
    }

    public Property<T> setValue(Object value) {
        storedValue = value;
        return this;
    }

    public Object getStoredValue() {
        return storedValue;
    }

    /**
     * 
     * @return the value of the property. This value may not be the one Stored with setValue(), it may be evaluated with
     * {@link PropertyValueEvaluator}.
     * @exception throw a ClassCastException is the stored value is not of the property type and no
     * PropertyValueEvaluator has been set.
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (propertyValueEvaluator != null) {
            return propertyValueEvaluator.evaluate(this, storedValue);
        } // else not evaluator so return the storedValue
        return (T) storedValue;
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

    @Override
    public String toString() {
        return "Property: " + getName();
    }

    /**
     * If no displayName was specified then the i18n key : {@value Property#I18N_PROPERTY_PREFIX}.name_of_this_property.
     * {@value NamedThing#I18N_DISPLAY_NAME_SUFFIX} to find the value from the i18n.
     */
    @Override
    public String getDisplayName() {
        return displayName != null ? displayName
                : getI18nMessage(I18N_PROPERTY_PREFIX + name + NamedThing.I18N_DISPLAY_NAME_SUFFIX);
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

    /**
     * copy all tagged values from the otherProp into this.
     */
    public void copyTaggedValues(Property otherProp) {
        taggedValues.putAll(otherProp.taggedValues);
    }

}
