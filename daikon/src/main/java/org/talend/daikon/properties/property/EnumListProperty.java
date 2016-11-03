package org.talend.daikon.properties.property;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.daikon.exception.TalendRuntimeException;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * Property that contains a list of enum
 */
public class EnumListProperty<T extends Enum<T>> extends Property<List<T>> {

    public EnumListProperty(TypeLiteral<List<T>> type, String name) {
        super(type, name, null);
        // set the possible values accoording with all the enum types.
        T[] enumConstants = (T[]) ((Class) ((Type[]) ((ParameterizedTypeImpl) type.value).getActualTypeArguments())[0])
                .getEnumConstants();
        this.setPossibleValues(enumConstants);
    }

    /*
     * This is package protected because this constructor should only be used when copying a Property at runtime, so it
     * does not need to be typed.
     */
    EnumListProperty(String type, String name) {
        super(type, name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getValue() {
        if (storedValue == null) {
            return null;
        }
        List value = (List) storedValue;
        if (propertyValueEvaluator != null) {
            value = propertyValueEvaluator.evaluate(this, storedValue);
        } // else not evaluator so return the storedValue
          // try to convert the String to the enum
        List convertedValue = new ArrayList();
        boolean converted = false;
        try {
            String enumClassName = getType().substring("java.util.List<".length(), getType().length() - 1);
            for (Object v : value) {
                if (v instanceof String) {
                    converted = true;
                    Class<T> enumClass = (Class<T>) ClassUtils.getClass(enumClassName);
                    convertedValue.add(Enum.valueOf(enumClass, (String) v));
                }
            }
        } catch (ClassNotFoundException e) {
            TalendRuntimeException.unexpectedException(e);
        }
        return converted ? convertedValue : (List<T>) value;
    }

    /*
     * this will replace the "." with a "$" for all String starting with a Capital letter except for the first one.
     */
    String convertToInnerClassString(String type) {
        return StringUtils.replacePattern(type, "([a-z0-9]*\\\\.[A-Z][^.]*)?((\\\\.)([A-Z][a-z0-9]*))", "$1\\$$4");
    }
}
