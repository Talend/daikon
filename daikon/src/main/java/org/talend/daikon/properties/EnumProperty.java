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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * Specifc property for enums
 */
public class EnumProperty<T extends Enum<T>> extends Property<T> implements AnyProperty {

    public EnumProperty(Class<T> zeEnumType, String name) {
        super(zeEnumType, name, null);
        // set the possible values accoording with all the enum types.
        Enum<?>[] enumConstants = zeEnumType.getEnumConstants();
        this.setPossibleValues(enumConstants);

    }

    // this is used for deserialization
    EnumProperty(String zeEnumTypeStr, String name) {
        super(zeEnumTypeStr, name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue() {
        Object value = storedValue;
        if (propertyValueEvaluator != null) {
            value = propertyValueEvaluator.evaluate(this, storedValue);
        } // else not evaluator so return the storedValue
          // try to convert the String to the enum
        if (value instanceof String) {
            try {
                Class<T> enumClass = (Class<T>) ClassUtils.getClass(getType());
                return Enum.valueOf(enumClass, (String) value);
            } catch (ClassNotFoundException e) {
                TalendRuntimeException.unexpectedException(e);
            }
        }
        return (T) value;
    }

    /**
     * this will replace the . with a $ for all String starting with a Capital letter except for the first one.
     */
    String convertToInnerClassString(String type) {
        return StringUtils.replacePattern(type, "([a-z0-9]*\\\\.[A-Z][^.]*)?((\\\\.)([A-Z][a-z0-9]*))", "$1\\$$4");
    }

}
