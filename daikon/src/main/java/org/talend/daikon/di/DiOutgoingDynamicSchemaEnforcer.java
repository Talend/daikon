// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import static org.talend.daikon.di.IndexMapper.DYNAMIC;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;

/**
 * This class acts as a wrapper around an arbitrary Avro {@link IndexedRecord} to transform output avro-styled values to the exact
 * Java objects expected by the Talend 6 Studio (which will copy the fields into a POJO in generated code).
 * <p>
 * A wrapper like this should be attached to an input component, for example, to ensure that its outgoing data meets the
 * Schema constraints imposed by the Studio, including:
 * <ul>
 * <li>Coercing the types of the returned objects to *exactly* the type required by the Talend POJO.</li>
 * <li>Placing all of the unresolved columns between the wrapped schema and the output schema in the Dynamic column.</li>
 * </ul>
 * <p>
 * It extends {@link DiOutgoingSchemaEnforcer} and provides handling for dynamic fields
 */
public class DiOutgoingDynamicSchemaEnforcer extends DiOutgoingSchemaEnforcer {
    
    /**
     * A {@link List} of design schema {@link Field}s
     * It is stored as separate field to accelerate access to them
     */
    private List<Field> designFields;

    /**
     * A {@link List} of runtime schema {@link Field}s
     */
    private List<Field> runtimeFields;

    /**
     * Dynamic field position in the design schema. Schema can contain 0 or 1 dynamic columns.
     */
    private final int dynamicFieldPosition;

    /**
     * Contains indexes of dynamic fields (i.e. fields which are present in runtime schema, but are not present in design schema)
     */
    private List<Integer> dynamicFieldsIndexes;

    /**
     * {@link Schema}, which describes dynamic fields (i.e. fields which are present in runtime schema, but are not present in
     * design schema)
     */
    private Schema dynamicFieldsSchema;

    /**
     * Constructor sets values related to dynamic fields handling
     * 
     * @param designSchema design schema (specified by user and provided by Di Studio)
     * @param indexMapper tool, which computes correspondence between design and runtime fields
     */
    public DiOutgoingDynamicSchemaEnforcer(Schema designSchema, DynamicIndexMapper indexMapper) {
        super(indexMapper);
        this.designFields = designSchema.getFields();
        this.dynamicFieldPosition = DynamicFieldUtils.getDynamicFieldPosition(designSchema);
        if (dynamicFieldPosition == DynamicFieldUtils.NO_DYNAMIC_FIELD) {
            throw new IllegalArgumentException("Design schema doesn't contain dynamic field");
        }
    }

    /**
     * Returns dynamic fields schema
     * This method could be called only after {@ling DiOutgoingDynamicSchemaEnforcer#setWrapped(IndexedRecord)} was called
     * 
     * @return dynamic fields schema
     */
    public Schema getDynamicFieldsSchema() {
        return dynamicFieldsSchema;
    }

    /**
     * {@inheritDoc}
     * 
     * Could be called only after first record was wrapped
     * 
     * @param pojoIndex index of required value. Could be from 0 to designSchemaSize
     */
    // @Override
    public Object get(int pojoIndex) {
        int runtimeIndex = indexMap[pojoIndex];
        if (runtimeIndex == DYNAMIC) {
            return getDynamicValues();
        }

        Field designField = pojoIndex > dynamicFieldPosition ? designFields.get(pojoIndex - 1) : designFields.get(pojoIndex);
        Object value = wrappedRecord.get(runtimeIndex);
        return transformValue(value, designField);
    }

    /**
     * Computes {@link this#indexMap}, initializes {@link this#converters}
     * Computes runtime fields and creates dynamic fields schema 
     * 
     * @param record first incoming {@link IndexedRecord}
     */
    @Override
    protected void processFirstRecord(IndexedRecord record) {
        super.processFirstRecord(record);
        Schema runtimeSchema = record.getSchema();
        this.runtimeFields = runtimeSchema.getFields();
        this.dynamicFieldsIndexes = ((DynamicIndexMapper) indexMapper).computeDynamicFieldsIndexes(runtimeSchema);
        createDynamicFieldsSchema();
    }

    /**
     * Retrieves dynamic fields values and returns them as map.
     * Map key is dynamic field name
     * Map value is dynamic field value, transformed to Talend type
     * 
     * @return map with dynamic values
     */
    private Map<String, Object> getDynamicValues() {
        Map<String, Object> dynamicValues = new LinkedHashMap<>();
        for (int dynamicIndex : dynamicFieldsIndexes) {
            Field dynamicField = runtimeFields.get(dynamicIndex);
            String dynamicFieldName = dynamicField.name();
            Object value = wrappedRecord.get(dynamicIndex);
            value = transformValue(value, runtimeFields.get(dynamicIndex));
            dynamicValues.put(dynamicFieldName, value);
        }
        return dynamicValues;
    }

    /**
     * Creates {@link Schema} of dynamic fields. It is used to create Dynamic Metadatas in DI
     */
    private void createDynamicFieldsSchema() {
        List<Field> dynamicFields = new ArrayList<>();
        // List<Integer> dynamicFieldsIndexes = indexMapper.computeDynamicFieldsIndexes();
        for (int index : dynamicFieldsIndexes) {
            Field dynamicField = runtimeFields.get(index);
            Field dynamicFieldCopy = new Schema.Field(dynamicField.name(), dynamicField.schema(), dynamicField.doc(),
                    dynamicField.defaultVal());
            Map<String, Object> fieldProperties = dynamicField.getObjectProps();
            for (Map.Entry<String, Object> entry : fieldProperties.entrySet()) {
                Object propValue = entry.getValue();
                if (propValue != null) {
                    dynamicFieldCopy.addProp(entry.getKey(), propValue);
                }
            }
            dynamicFields.add(dynamicFieldCopy);
        }

        dynamicFieldsSchema = Schema.createRecord("dynamic", null, null, false);
        dynamicFieldsSchema.setFields(dynamicFields);
    }
}
