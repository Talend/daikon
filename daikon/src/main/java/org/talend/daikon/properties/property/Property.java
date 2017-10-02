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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.AnyProperty;
import org.talend.daikon.properties.AnyPropertyVisitor;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.validation.Validator;
import org.talend.daikon.strings.ToStringIndentUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A property that is part of a {@link Properties}.
 *
 * In order to resolve the actual values of properties from an indirect definition, like a context variable or system
 * property, the concept of a "stored value" is support. The stored value could not be the actual value, but a
 * (typically string) key which is resolved to provide the actual value when needed. The {@link PropertyValueEvaluator}
 * is the mechanism which translates the stored value to the actual value as returned by {@link #getValue()}.
 */
public class Property<T> extends SimpleNamedThing implements AnyProperty {

    public static final String I18N_PROPERTY_POSSIBLE_VALUE_PREFIX = "property.possiblevalue."; //$NON-NLS-1$

    public static final int INFINITE = -1;

    private static final long serialVersionUID = 6151130865421353629L;

    private static final String I18N_PROPERTY_PREFIX = "property."; //$NON-NLS-1$

    protected EnumSet<Flags> flags;

    protected Object storedValue;

    protected Object storedDefaultValue;

    protected transient Validator<T> validator;

    protected transient PropertyValueEvaluator propertyValueEvaluator;

    protected List<Property<?>> children = new ArrayList<>();

    private Map<String, Object> taggedValues = new HashMap<>();

    private int size;

    private int occurMinTimes;

    private int occurMaxTimes;

    // Number of decimal places - DI
    private int precision;

    // Used for date conversion - DI
    private String pattern;

    private boolean nullable;

    private List<?> possibleValues;

    private String currentType;

    /**
     * For internal use only.
     */
    Property(TypeLiteral<T> type, String name, String title) {
        this(type.getType(), name, title);
    }

    /**
     * For internal use only.
     */
    Property(TypeLiteral<T> type, String name) {
        this(type, name, null);
    }

    /**
     * For internal use only.
     */
    Property(Type type, String name, String title) {
        // we cannot store the type as is because of a serialization issue that will serialised
        // sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl and will fail with jsonio
        // and also is not portable across different jvm vendors.
        this(TypeUtils.toString(type), name, title);
    }

    /**
     * For internal use only.
     */
    Property(String type, String name) {
        this(type, name, null);
    }

    /**
     * For internal use only.
     */
    Property(String type, String name, String title) {
        currentType = type;
        setName(name);
        setTitle(title);
        setSize(-1);
    }

    Property(Class<T> type, String name) {
        this(type, name, null);
    }

    Property(Class<T> type, String name, String title) {
        this((Type) type, name, title);
    }

    @Override
    public String getName() {
        return name;
    }

    public Property<T> setName(String name) {
        this.name = name;
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

    /**
     * Return the String representing the current type of the object. this string is the same value as
     * {@link TypeUtils#toString(Type)}
     */
    public String getType() {
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
        return size == -1;
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

    public Property<T> setRequired(boolean required) {
        if (required) {
            setOccurMinTimes(1);
        } else {
            setOccurMinTimes(0);
        }
        setOccurMaxTimes(1);
        return this;
    }

    public Property<T> setRequired() {
        return setRequired(true);
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

    /**
     * Return a list of possible values for this property. If the property is a simple type or an enum this will return
     * a list of element with the same type as the Property. But for convenience if this Property is a collection type
     * such as {@link Map<T>} this is used to simply return a list of T and not a List<Map<T>>. This will not be
     * enforced at all, just a convention.
     */
    public List<?> getPossibleValues() {
        return possibleValues == null ? (List<T>) Collections.emptyList() : possibleValues;
    }

    @JsonIgnore
    // to avoid swagger failure because of the 2 similar following methods.
    public Property<T> setPossibleValues(List<?> possibleValues) {
        this.possibleValues = possibleValues;
        return this;
    }

    public Property<T> setPossibleValues(Object... values) {
        this.possibleValues = Arrays.asList(values);
        return this;
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

    /**
     * Return the internal value used for serialization.
     */
    public Object getStoredValue() {
        return storedValue;
    }

    /**
     * Set the value that will be stored and serialized into this Property. Sometimes the value will not match the type
     * of the property. For example it may be a string that refers to the value using some context mechanism. In this
     * case a {@link PropertyValueEvaluator} is used so that when the (@link {@link Property#getValue()} is called, it
     * is converted to the proper type.
     */
    public Property<T> setStoredValue(Object value) {
        storedValue = value;
        return this;
    }

    /**
     * Get the actual value of the property, resolving the stored value if required.
     *
     * @return the value of the property. This value may not be the one Stored with setValue(), it may be evaluated with
     * {@link PropertyValueEvaluator}.
     * @exception ClassCastException is the stored value is not of the property type and no
     * {@code PropertyValueEvaluator} has been set.
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (propertyValueEvaluator != null) {
            return propertyValueEvaluator.evaluate(this, storedValue);
        } // else not evaluator so return the storedValue
        return (T) storedValue;
    }

    /**
     * Get the default value of the property, resolving the stored value if required.
     *
     * @return the value of the property. This value may not be the one Stored with setValue(), it may be evaluated with
     * {@link PropertyValueEvaluator}.
     * @exception ClassCastException is the stored value is not of the property type and no {@code PropertyValueEvaluator} has
     * been set.
     */
    @SuppressWarnings("unchecked")
    public T getDefaultValue() {
        if (propertyValueEvaluator != null) {
            return propertyValueEvaluator.evaluate(this, storedDefaultValue);
        } // else not evaluator so return the storedValue
        return (T) storedDefaultValue;
    }

    /**
     * Set the stored value for this property.
     *
     * @see #setStoredValue(Object)
     */
    public Property<T> setValue(T value) {
        storedValue = value;
        return this;
    }

    /**
     * Set the default stored value for this property.
     */
    protected Property<T> setDefaultValue(T value) {
        storedDefaultValue = value;
        return this;
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
     * Cast the default value to a string
     */
    public String getStringDefaultValue() {
        if (getDefaultValue() == null) {
            return null;
        } else {
            return String.valueOf(getDefaultValue());
        }
    }

    @Override
    public String toString() {
        return "Property: " + getName();
    }

    /**
     * If no displayName was specified then the i18n key, then {@link Property#I18N_PROPERTY_PREFIX} +
     * {@code name_of_this_property} + {@link NamedThing#I18N_DISPLAY_NAME_SUFFIX} to find the value from the i18n.
     */
    @Override
    public String getDisplayName() {
        return displayName != null ? displayName
                : getI18nMessage(I18N_PROPERTY_PREFIX + name + NamedThing.I18N_DISPLAY_NAME_SUFFIX);
    }

    public Property<T> setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Return a i18n String for a given possible value. It will automatically look for the key
     * {@value Property#I18N_PROPERTY_PREFIX}.possibleValue.getDisplayName(). {@value NamedThing#I18N_DISPLAY_NAME_SUFFIX}. if the
     * key
     * is not found it returns the possibleValue.toString().
     *
     * @return a I18n value or possibleValue.toString if the value is not found.
     * @exception TalendRuntimeException with {@link CommonErrorCodes#UNEXPECTED_ARGUMENT} if the possible value does not belong
     * to possible values
     */
    public String getPossibleValuesDisplayName(Object possibleValue) {
        // first check that the possibleValue is part of the possible values
        if (!isAPossibleValue(possibleValue)) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                    ExceptionContext.build().put("argument", "possibleValues").put("value", possibleValue));
        }
        if (possibleValue != null) {
            String possibleValueDisplayName = possibleValue.toString();
            if (NamedThing.class.isAssignableFrom(possibleValue.getClass())) {
                possibleValueDisplayName = ((NamedThing) possibleValue).getDisplayName();
            }
            String i18nMessage = getI18nMessage(
                    I18N_PROPERTY_POSSIBLE_VALUE_PREFIX + possibleValueDisplayName + NamedThing.I18N_DISPLAY_NAME_SUFFIX);
            if (i18nMessage.endsWith(NamedThing.I18N_DISPLAY_NAME_SUFFIX)) {
                return possibleValueDisplayName;
            } else {
                return i18nMessage;
            }
        } else {
            return "null";
        }
    }

    protected boolean isAPossibleValue(Object possibleValue) {
        if (getPossibleValues() != null) {
            return getPossibleValues().contains(possibleValue);
        } // no possible values so this should not be called, return false.
        return false;
    }

    /**
     * This stores a value with the given key in a map this will be serialized with the component. This may be used to
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
     * Return the previously stored value using {@link Property#setTaggedValue(String, Object)} and the given key.
     *
     * @param key, identify the value to be fetched
     * @return the object stored along with the key or null if none found.
     */
    public Object getTaggedValue(String key) {
        return taggedValues.get(key);
    }

    public PropertyValueEvaluator getValueEvaluator() {
        return this.propertyValueEvaluator;
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
     * Copy all tagged values from the otherProp into this.
     */
    public void copyTaggedValues(Property otherProp) {
        taggedValues.putAll(otherProp.taggedValues);
    }

    /**
     * Encrypt the stored value or do nothing if there is not implementation for the given type.
     */
    public void encryptStoredValue(boolean encrypt) {
        // do nothing by default see StringProperty for an example
    }

    /**
     * Validate this Property value. Validation can be done in {@link Property#validator} set to this Property field or this
     * method can be overriden to make some validation.
     * 
     * @return Validation result for this Property value
     */
    public ValidationResult validate() {
        if (validator == null) {
            return ValidationResult.OK;
        }
        return validator.validate(getValue());
    }

    /**
     * Set validator which will be used to validate value set to this Property.
     */
    public Property<T> setValidator(Validator<T> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.storedValue == null) ? 0 : this.storedValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Property<?> other = (Property<?>) obj;

        return new EqualsBuilder().appendSuper(super.equals(obj)).append(storedValue, other.storedValue).isEquals();
    }

    public enum Flags {
        /**
         * Encrypt this when storing the {@link Properties} into a persistent serializable form.
         */
        ENCRYPT,
        /**
         * Don't log this value in any logs (used for sensitive information).
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
        HIDDEN

    }
}
