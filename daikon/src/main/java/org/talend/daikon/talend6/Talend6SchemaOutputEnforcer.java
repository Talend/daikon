// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.talend6;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.IndexedRecordAdapterFactory.UnmodifiableAdapterException;

/**
 * This class acts as a wrapper around an arbitrary Avro {@link IndexedRecord} to coerce the output type to the exact
 * Java objects expected by the Talend 6 Studio (which will copy the fields into a POJO in generated code).
 * 
 * A wrapper like this should be attached to an input component, for example, to ensure that its outgoing data meets the
 * Schema constraints imposed by the Studio, including:
 * <ul>
 * <li>Coercing the types of the returned objects to *exactly* the type required by the Talend POJO.</li>
 * <li>Placing all of the unresolved columns between the wrapped schema and the output schema in the Dynamic column.</li>
 * </ul>
 * 
 * One instance of this object can be created per outgoing schema and reused via the {@link #setWrapped(IndexedRecord)}
 * method.
 */
public class Talend6SchemaOutputEnforcer implements IndexedRecord, Talend6SchemaConstants {

    /** True if columns from the incoming schema are matched to the outgoing schema exclusively by position. */
    final private boolean byIndex;

    /** The outgoing schema that determines which Java objects are produced. */
    final private Schema outgoing;

    /**
     * The incoming IndexedRecord currently wrapped by this enforcer. This can be swapped out for new data as long as
     * they keep the same schema.
     */
    private IndexedRecord wrapped;

    /**
     * The position of the dynamic column in the outgoing schema. This is -1 if there is no dynamic column. There can be
     * a maximum of one dynamic column in the schema.
     */
    private final int dynamicColumn;

    /**
     * The name and position of fields in the wrapped record that need to be put into the dynamic column of the output
     * record.
     */
    private Map<String, Integer> dynamicColumnSources;

    public static final String TALEND6_DYNAMIC_TYPE = "id_Dynamic"; //$NON-NLS-1$

    public Talend6SchemaOutputEnforcer(Schema outgoing, boolean byIndex) {
        this.outgoing = outgoing;
        this.byIndex = byIndex;

        // Find the dynamic column, if any.
        int dynamic = -1;
        for (Field f : outgoing.getFields()) {
            if (TALEND6_DYNAMIC_TYPE.equals(f.getProp(TALEND6_COLUMN_TALEND_TYPE))) {
                if (dynamic != -1) {
                    // This is enforced by the Studio.
                    throw new UnsupportedOperationException("Too many dynamic columns."); //$NON-NLS-1$
                }
                dynamic = f.pos();
            }
        }
        dynamicColumn = dynamic;
    }

    /**
     * @param wrapped Sets the internal, actual index record that needs to be coerced to the outgoing schema.
     */
    public void setWrapped(IndexedRecord wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Schema getSchema() {
        return outgoing;
    }

    @Override
    public void put(int i, Object v) {
        throw new UnmodifiableAdapterException();
    }

    @Override
    public Object get(int i) {

        // If we are asking for the dynamic column, then all of the fields that don't match the outgoing schema are
        // added to a map.
        if (i == dynamicColumn) {
            if (byIndex) {
                return getDynamicMapByIndex();
            } else {
                return getDynamicMapByName();
            }
        }

        // If we are not asking for the dynamic column, then get the input field that corresponds to the position.
        int wrappedIndex = i;
        if (byIndex && i > dynamicColumn) {
            // If the requested index is after the dynamic column and we are matching by index, then the actual index
            // should be counted from the end of the fields.
            wrappedIndex = getSchema().getFields().size() - getNumberOfDynamicColumns() + i + 1;
        }
        if (!byIndex) {
            wrappedIndex = wrapped.getSchema().getField(getSchema().getFields().get(i).name()).pos();
        }

        Field outField = getSchema().getFields().get(i);
        Field wrappedField = wrapped.getSchema().getFields().get(wrappedIndex);
        Object value = wrapped.get(wrappedIndex);
        return transformValue(value, wrappedField, outField);
    }

    /**
     * @param value
     * @param wrappedField
     * @param outField
     * @return
     */
    private Object transformValue(Object value, Field wrappedField, Field outField) {
        String talendType = outField.getProp(TALEND6_COLUMN_TALEND_TYPE);
        if (null == talendType || null == value) {
            return value;
        }
        // TODO(rskraba): A full list of type conversion to coerce to Talend-compatible types.
        if ("id_Short".equals(talendType)) { //$NON-NLS-1$
            return value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(String.valueOf(value));
        } else if ("id_Byte".equals(talendType)) { //$NON-NLS-1$
            return value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(String.valueOf(value));
        }
        return value;
    }

    /** @return the number of columns that will be placed in the dynamic holder. */
    private int getNumberOfDynamicColumns() {
        int dynColN = wrapped.getSchema().getFields().size() - getSchema().getFields().size() + 1;
        if (dynColN < 0) {
            throw new UnsupportedOperationException(
                    "The incoming data does not have sufficient columns to create a dynamic column."); //$NON-NLS-1$
        }
        return dynColN;
    }

    /**
     * @return A map of all of the unresolved columns, when the unresolved columns are determined by the position of the
     * Dynamic column in enforced schema.
     */
    private Map<String, Object> getDynamicMapByIndex() {
        int dynColN = getNumberOfDynamicColumns();
        Map<String, Object> result = new HashMap<>();
        for (int j = 0; j < dynColN; j++) {
            result.put(wrapped.getSchema().getFields().get(dynamicColumn + j).name(), wrapped.get(dynamicColumn + j));
        }
        return result;
    }

    /**
     * @return A map of all of the unresolved columns, when the unresolved columns are determined by the names of the
     * resolved column in enforced schema.
     */
    private Map<String, Object> getDynamicMapByName() {
        // Lazy initialization of source position by name.
        if (dynamicColumnSources == null) {
            dynamicColumnSources = new HashMap<>();
            for (Schema.Field wrappedField : wrapped.getSchema().getFields()) {
                Schema.Field outField = getSchema().getField(wrappedField.name());
                if (outField == null || outField.pos() == dynamicColumn) {
                    dynamicColumnSources.put(wrappedField.name(), wrappedField.pos());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Integer> e : dynamicColumnSources.entrySet()) {
            result.put(e.getKey(), wrapped.get(e.getValue()));
        }
        return result;
    }

    /**
     * @Return true if the Avro Field has been tagged with a type, and the type is DYNAMIC.
     */
    public static boolean isDynamic(Field f) {
        return TALEND6_DYNAMIC_TYPE.equals(f.getProp(TALEND6_COLUMN_TALEND_TYPE));
    }
}