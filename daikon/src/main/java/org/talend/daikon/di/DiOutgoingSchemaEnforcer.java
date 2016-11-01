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
package org.talend.daikon.di;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.IndexedRecordConverter.UnmodifiableAdapterException;
import org.talend.daikon.avro.converter.SingleColumnIndexedRecordConverter;

/**
 * This class acts as a wrapper around an arbitrary Avro {@link IndexedRecord} to coerce the output type to the exact
 * Java objects expected by the Talend 6 Studio (which will copy the fields into a POJO in generated code).
 * <p>
 * A wrapper like this should be attached to an input component, for example, to ensure that its outgoing data meets the
 * Schema constraints imposed by the Studio, including:
 * <ul>
 * <li>Coercing the types of the returned objects to *exactly* the type required by the Talend POJO.</li>
 * <li>Placing all of the unresolved columns between the wrapped schema and the output schema in the Dynamic column.</li>
 * </ul>
 * <p>
 * One instance of this object can be created per outgoing schema and reused via the {@link #setWrapped(IndexedRecord)}
 * method.
 * <p>
 * This class accepts so-called design schema as an argument for constructor. Design schema is specified by user in Schema Editor.
 * It contains data fields, which user wants to retrieve from data source. Schema Editor creates schema in old manner. It creates instance 
 * of MetadataTable. For new components this instance is converted to avro {@link Schema} instance by MetadataToolAvroHelper.
 * <p>
 * There could be a situation when user doesn't know all fields of data, but he wants to retrieve them all. In this case user should specify
 * some field as dynamic. Dynamic means it is not known at design time how much actual fields will be retrieved. Dynamic field aggregates
 * all unknown fields. Note, design avro {@link Schema} doesn't contain dynamic field. It contain special properties, which describe 
 * dynamic field (its name, position in schema etc).
 * <p>
 * Consider following example:
 * User specified schema with following fields (field name, type):
 * <ul>
 * <li>(name, String)</li>
 * <li>(dynamic, Dynamic)</li>
 * <li>(address, String)</li>
 * </ul>
 * After conversion with MetadataToolAvroHelper avro {@link Schema} will contain only:
 * <ul>
 * <li>(name, String)</li>
 * <li>(address, String)</li>
 * </ul>
 * and properties, which describes dynamic field
 * <p>
 * There is one more thing, which should be mentioned. Both old and TCOMP components could be used in one Job in Di (Studio). To make them 
 * compatible there is special handling in codegen plugin. TCOMP component output (IndexedRecord and its Schema) is converted to old Di objects.
 * Row2Struct (also known as POJO) corresponds to IndexedRecord. Its fields correspond to data fields. Important note is that Row2Struct contains
 * also a field for dynamic field. {@link DiOutgoingSchemaEnforcer} goal is to convert avro-styled data to Talend-styled. 
 * {@link DiOutgoingSchemaEnforcer#get(int)} is the main functionality of this class. This class is used in codegen plugin.
 * See, component_util_indexedrecord_to_rowstruct.javajet. Note, get() is called for each field in Row2Struct. When user specified dynamic column,
 * Row2Struct will contain one more field than desigh avro schema.
 */
public class DiOutgoingSchemaEnforcer implements IndexedRecord, DiSchemaConstants {

    /**
     * Denotes column retrieving mode: by index or by name
     * True if columns from the incoming schema are matched to the outgoing schema exclusively by position.
     * False if columns are matched by name
     */
    private boolean byIndex;

    /**
     * {@link Schema} which was specified by user during setting component properties (at design time)
     * This schema may contain di-specific properties
     */
    private final Schema designSchema;
    
    /**
     * A {@link List} of design schema {@link Field}s
     * It is stored as separate field to accelerate access to them 
     */
    private final List<Field> designFields;
    
    /**
     * Number of fields in design schema
     */
    private final int designSchemaSize;

    /**
     * {@link IndexedRecord} currently wrapped by this enforcer. This can be swapped out for new data as long as
     * they keep the same schema. This {@link IndexedRecord} contains another {@link Schema} which is called actual or runtime
     * schema. Runtime schema can't contain dynamic columns.
     */
    private IndexedRecord wrappedRecord;
    
    /**
     * Actual schema of {@link IndexedRecord}. It is retrieved from first incoming {@link IndexedRecord}
     * All subsequent records should have the same schema
     */
    private Schema runtimeSchema;
    
    /**
     * A {@link List} of runtime schema {@link Field}s
     * It is stored as separate field to accelerate access to them 
     */
    private List<Field> runtimeFields;
    
    /**
     * Number of fields in runtime schema
     */
    private int runtimeSchemaSize;
    
    /**
     * Specifies whether design schema contains dynamic columns
     */
    private final boolean hasDynamic;

    /**
     * Dynamic column position in the design schema. Schema can contain 0 or 1 dynamic columns.
     * -1 value means there is no dynamic column in schema
     */
    private final int dynamicColumnPosition;
    
    /**
     * Number of dynamic columns. This values computed, when first {@link IndexedRecord} wrapped
     */
    private int dynamicColumns;

    /**
     * The {@link Schema} of dynamic columns. It will be calculated only once and be used for initial
     * routines.system.DynamicMetadata
     */
    private Schema dynamicColumnsSchema;

    /**
     * The name and position of fields in the wrapped record that need to be put into the dynamic column of the output
     * record.
     */
    private Map<String, Integer> dynamicColumnSources;
    
    /**
     * State of this {@link DiOutgoingSchemaEnforcer}, which denotes whether first {@link IndexedRecord} was already
     * wrapped and values were computed
     */
    private boolean firstRecordProcessed = false;
    
    /**
     * Maps design field indexes to runtime field indexes.
     * Design indexes are indexed of this array and runtime indexed are values
     * This map is computed once for the first incoming record and then used for all subsequent records
     * -1 value is used to to denote that design field corresponds to dynamic field
     */
    private int[] indexMap;

    /**
     * Constructor sets design schema and column retrieving mode.
     * Note, index map size is greater then design schema by one, because additional index is appeared in 
     * case of dynamic
     * 
     * @param designSchema schema specified by user
     * @param byIndex column retrieval mode. True for by index, false for by name
     */
    public DiOutgoingSchemaEnforcer(Schema designSchema, boolean byIndex) {
        this.designSchema = designSchema;
        designFields = designSchema.getFields();
        designSchemaSize = designFields.size();
        indexMap = new int[designSchemaSize+1];
        this.byIndex = byIndex;

        hasDynamic = AvroUtils.isIncludeAllFields(designSchema);
        if (hasDynamic) {
            dynamicColumnPosition = Integer.valueOf(designSchema.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION));
        } else {
            dynamicColumnPosition = -1;
        }
    }

    /**
     * Wraps {@link IndexedRecord} inside this {@link DiOutgoingSchemaEnforcer}
     * 
     * @param record {@link IndexedRecord} to be wrapped
     */
    public void setWrapped(IndexedRecord record) {
        // TODO: This matches the salesforce and file-input single output components. Is this sufficient logic
        // for all components?
        if (record instanceof SingleColumnIndexedRecordConverter.PrimitiveAsIndexedRecordAdapter) {
            byIndex = true;
        }
        wrappedRecord = record;
        if (!firstRecordProcessed) {
            processFirstRecord();
        }
    }
    
    /**
     * All records must have the same {@link Schema}. Several values should be computed after first {@link IndexRecord} was
     * wrapped. Then these values could be reused for all subsequent records
     */
    private void processFirstRecord() {
        setRuntimeSchema();
        if (hasDynamic) {
            computeDynamicColumnsNumber();
            createDynamicColumnsSchema();
        }
        if (byIndex) {
            computeIndexMapByIndex();
        } else {
            computeIndexMapByName();
        }
        
    }
    
    /**
     * Computes index map using field indexes
     */
    private void computeIndexMapByIndex() {

        for (int i = 0; i < designSchemaSize + 1; i++) {
            if (hasDynamic && i >= dynamicColumnPosition) {
                if (i == dynamicColumnPosition) {
                    indexMap[i] = -1;
                } else {
                    indexMap[i] = dynamicColumns + i - 1;
                }
            } else {
                indexMap[i] = i;
            }
        }
    }
    
    /**
     * Computes index map using field names
     */
    private void computeIndexMapByName() {

        for (int i = 0; i < designSchemaSize; i++) {
            Field designField = designFields.get(i);
            String fieldName = designField.name();
            Field runtimeField = runtimeSchema.getField(fieldName);
            if (hasDynamic && i >= dynamicColumnPosition) {
                if (i == dynamicColumnPosition) {
                    indexMap[i] = -1;
                    indexMap[i + 1] = runtimeField.pos();
                } else {
                    indexMap[i + 1] = runtimeField.pos();
                }
            } else {
                indexMap[i] = runtimeField.pos();
            }
        }
    }

    /**
     * Sets runtime schema, its fields and size
     */
    private void setRuntimeSchema() {
        runtimeSchema = wrappedRecord.getSchema();
        runtimeFields = runtimeSchema.getFields();
        runtimeSchemaSize = runtimeFields.size();
    }

    /**
     * Creates schema, which contains only dynamic columns.
     * It is used to create routines.system.DynamicMetadata.
     * It could be computed only once for first incoming record and then reused
     */
    private void createDynamicColumnsSchema() {
        List<Schema.Field> copyFieldList;
        if (byIndex) {
            copyFieldList = getDynamicSchemaByIndex();
        } else {
            copyFieldList = getDynamicSchemaByName();
        }
        dynamicColumnsSchema = Schema.createRecord("dynamic", null, null, false);
        dynamicColumnsSchema.setFields(copyFieldList);
    }

    /**
     * Returns schema of this {@link IndexedRecord}
     * 
     * TODO Here should be returned actual schema (schema which corresponds to get() method output. However, seems it is not used)
     */
    @Override
    public Schema getSchema() {
        return designSchema;
    }

    public Schema getOutgoingDynamicRuntimeSchema() {
        return dynamicColumnsSchema;
    }

    /**
     * Throws {@link UnmodifiableAdapterException}. This operation is not supported
     */
    @Override
    public void put(int i, Object v) {
        throw new UnmodifiableAdapterException();
    }

    /**
     * {@inheritDoc}
     * 
     * Could be called only after first record was wrapped
     * 
     * @param i index of required value. Could be from 0 to designSchemaSize
     */
    @Override
    public Object get(int i) {
        if (hasDynamic) {
            // If we are asking for the dynamic column, then all of the fields that don't match the outgoing schema are
            // added to a map.
            if (i == dynamicColumnPosition) {
                if (byIndex) {
                    return getDynamicMapByIndex();
                } else {
                    return getDynamicMapByName();
                }
            }
        }

        // We should never ask for an index outside of the outgoing schema.
        if (i > designSchemaSize) {
            throw new ArrayIndexOutOfBoundsException(i);
        }

        Field outField = null;
        if (hasDynamic && i > dynamicColumnPosition) {
            outField = designFields.get(i-1);
        } else {
            outField = designFields.get(i);
        }

        Object value = wrappedRecord.get(indexMap[i]);
        return transformValue(value, outField);
    }

    /**
     * Transforms record column value from Avro type to Talend type
     * 
     * @param value record column value, which should be transformed into Talend compatible value. 
     * It can be null when null
     * corresponding wrapped field.
     * @param designField design field, it should contain information about value's Talend type. It mustn't be null
     */
    private Object transformValue(Object value, Field designField) {
        
        if (null == value) {
            return null;
        }
        
        String talendType = designField.getProp(TALEND6_COLUMN_TALEND_TYPE);
        String javaClass = AvroUtils.unwrapIfNullable(designField.schema()).getProp(SchemaConstants.JAVA_CLASS_FLAG);

        // TODO(rskraba): A full list of type conversion to coerce to Talend-compatible types.
        if ("id_Short".equals(talendType)) { //$NON-NLS-1$
            return value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(String.valueOf(value));
        } else if ("id_Date".equals(talendType) || "java.util.Date".equals(javaClass)) { //$NON-NLS-1$
            return value instanceof Date ? value : new Date((Long) value);
        } else if ("id_Byte".equals(talendType)) { //$NON-NLS-1$
            return value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(String.valueOf(value));
        } else if ("id_Character".equals(talendType) || "java.lang.Character".equals(javaClass)) {
            return value instanceof Character ? value : ((String) value).charAt(0);
        } else if ("id_BigDecimal".equals(talendType) || "java.math.BigDecimal".equals(javaClass)) {
            return value instanceof BigDecimal ? value : new BigDecimal(String.valueOf(value));
        }
        return value;
    }

    /**
     * Computes number of dynamic columns as: recordColumns - designColumns
     * TODO check if it is correct expression for it
     * 
     * It could be computed only after first record was wrapped. However, it should be same for all records
     */
    private void computeDynamicColumnsNumber() {
        dynamicColumns = runtimeSchemaSize - designSchemaSize;
        if (dynamicColumns < 0) {
            throw new UnsupportedOperationException(
                    "The incoming data does not have sufficient columns to create a dynamic column."); //$NON-NLS-1$
        }
    }

    /**
     * @return A map of all of the unresolved columns, when the unresolved columns are determined by the position of the
     * Dynamic column in enforced schema.
     */
    private Map<String, Object> getDynamicMapByIndex() {
        Map<String, Object> result = new HashMap<>();
        for (int j = 0; j < dynamicColumns; j++) {
            result.put(runtimeFields.get(dynamicColumnPosition + j).name(),
                    wrappedRecord.get(dynamicColumnPosition + j));
        }
        return result;
    }

    /**
     * @return A list of all of the unresolved columns's schema, when the unresolved columns are determined by the
     * position of the Dynamic column in enforced schema.
     */
    private List<Schema.Field> getDynamicSchemaByIndex() {
        List<Schema.Field> fields = new ArrayList<>();
        for (int j = 0; j < dynamicColumns; j++) {
            Schema.Field se = runtimeFields.get(dynamicColumnPosition + j);
            Schema.Field field = new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal());
            Map<String, Object> fieldProperties = se.getObjectProps();
            for (String propName : fieldProperties.keySet()) {
                Object propValue = fieldProperties.get(propName);
                if (propValue != null) {
                    field.addProp(propName, propValue);
                }
            }
            fields.add(field);
        }
        return fields;
    }

    /**
     * @return A map of all of the unresolved columns, when the unresolved columns are determined by the names of the
     * resolved column in enforced schema.
     */
    private Map<String, Object> getDynamicMapByName() {
        // Lazy initialization of source position by name.
        if (dynamicColumnSources == null) {
            dynamicColumnSources = new HashMap<>();
            for (Schema.Field wrappedField : runtimeFields) {
                Schema.Field outField = designSchema.getField(wrappedField.name());
                if (outField == null) {
                    dynamicColumnSources.put(wrappedField.name(), wrappedField.pos());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Integer> e : dynamicColumnSources.entrySet()) {
            result.put(e.getKey(), wrappedRecord.get(e.getValue()));
        }
        return result;
    }

    /**
     * @return A list of all of the unresolved columns's schema, when the unresolved columns are determined by the names
     * of the resolved column in enforced schema.
     */
    private List<Schema.Field> getDynamicSchemaByName() {
        List<Schema.Field> fields = new ArrayList<>();
        List<String> designColumnsName = new ArrayList<>();
        for (Schema.Field se : designFields) {
            designColumnsName.add(se.name());
        }
        for (Schema.Field se : runtimeFields) {
            if (designColumnsName.contains(se.name())) {
                continue;
            }
            Schema.Field field = new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal());
            Map<String, Object> fieldProperties = se.getObjectProps();
            for (String propName : fieldProperties.keySet()) {
                Object propValue = fieldProperties.get(propName);
                if (propValue != null) {
                    field.addProp(propName, propValue);
                }
            }
            fields.add(field);
        }
        return fields;
    }

}