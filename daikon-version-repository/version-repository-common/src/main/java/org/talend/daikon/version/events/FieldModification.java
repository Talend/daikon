/*
 * ============================================================================
 *
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */

package org.talend.daikon.version.events;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Provides details about a field modification on a resource.
 * </p>
 *
 * <p>
 * A field is identified by its canonical access path from the resource's root
 * using the Java Beans notation.
 * </p>
 */
public class FieldModification {

    /**
     * The separator character used in path.
     */
    public static final char PATH_PARTS_SEPARATOR = '.';

    public static final char ARRAY_INDEX_START = '[';

    public static final char ARRAY_INDEX_END = ']';

    /**
     * the access path of the field within the resource
     */
    private String path;

    /**
     * The type of change made on this field
     */
    private ChangeType type;

    /**
     * The eventual previous value of the field
     */
    private Object previousValue;

    /**
     * The eventual new value of the field.
     */
    private Object newValue;

    /**
     * Creates a new FieldModification representing an update on a field
     * 
     * @param path path of the field
     * @param previousValue previous value
     * @param newValue new value
     * @return an update FieldModification, if previousValue is null, returns a creation FieldModification
     * if newValue is null, returns a deletion FieldModification
     */
    public static FieldModification createFieldUpdate(String path, Object previousValue, Object newValue) {
        if (previousValue == null && newValue != null) {
            return createFieldCreation(path, newValue);
        }
        if (previousValue != null && newValue == null) {
            return createFieldDeletion(path, previousValue);
        }
        FieldModification modification = new FieldModification();
        modification.setType(ChangeType.UPDATE);
        modification.setPath(path);
        modification.setPreviousValue(previousValue);
        modification.setNewValue(newValue);
        return modification;
    }

    /**
     * Creates a new FieldModification representing a field creation
     * 
     * @param path path of the field
     * @param newValue new value
     * @return returns a creation FieldModification
     */
    public static FieldModification createFieldCreation(String path, Object newValue) {
        FieldModification modification = new FieldModification();
        modification.setType(ChangeType.CREATE);
        modification.setPath(path);
        modification.setNewValue(newValue);
        return modification;
    }

    /**
     * Creates a new FieldModification representing a field deletion
     * 
     * @param path path of the field
     * @param previousValue the previous value
     * @return returns a deletion FieldModification
     */
    public static FieldModification createFieldDeletion(String path, Object previousValue) {
        FieldModification modification = new FieldModification();
        modification.setType(ChangeType.DELETE);
        modification.setPath(path);
        modification.setPreviousValue(previousValue);
        return modification;
    }

    /**
     * Gets path
     *
     * @return value of path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path
     *
     * @param path Path of the modifications.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public ChangeType getType() {
        return type;
    }

    /**
     * Sets type
     *
     * @param type Type of change for the field modification.
     */
    public void setType(ChangeType type) {
        this.type = type;
    }

    /**
     * Gets previousValue
     *
     * @return value of previousValue
     */
    public Object getPreviousValue() {
        return previousValue;
    }

    /**
     * Sets previousValue
     *
     * @param previousValue The previous value
     */
    public void setPreviousValue(Object previousValue) {
        this.previousValue = previousValue;
    }

    /**
     * Gets newValue
     *
     * @return value of newValue
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Sets newValue
     *
     * @param newValue The new value for the field.
     */
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public void undo(Object item) {
        invoke(item, StringUtils.split(path, PATH_PARTS_SEPARATOR), previousValue);
    }

    private Object invoke(final Object o, final String[] path, final Object value) {
        try {
            Object currentObject = o;
            for (int i = 0; i < path.length - 1; i++) {
                final Field field = o.getClass().getField(path[i]);
                field.setAccessible(true);
                currentObject = field.get(currentObject);
            }
            final Field fieldToModify = currentObject.getClass().getDeclaredField(path[path.length - 1]);
            fieldToModify.setAccessible(true);
            fieldToModify.set(currentObject, value);
            return currentObject;
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Override
    public String toString() {
        return "FieldModification{" + "path='" + path + '\'' + ", type=" + type + ", previousValue=" + previousValue
                + ", newValue=" + newValue + '}';
    }
}
