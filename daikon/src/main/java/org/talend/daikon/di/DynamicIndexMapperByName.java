package org.talend.daikon.di;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;

/**
 * {@link DynamicIndexMapper} implementation, which match fields according their names
 */
class DynamicIndexMapperByName implements DynamicIndexMapper {

    /**
     * {@link Schema} which was specified by user during setting component properties (at design time)
     * This schema may contain di-specific properties
     */
    private final Schema designSchema;

    /**
     * A {@link List} of design schema {@link Field}s
     */
    private final List<Field> designFields;

    /**
     * Number of fields in design schema. It is less then number of fields in POJO by 1 in case of dynamic field
     */
    private final int designSchemaSize;

    /**
     * Number of dynamic fields. It equals runtime schema size minus design schema size
     * Note, design schema doesn't contain dynamic field as a field. It contains schema properties, which
     * describes dynamic field
     */
    private final int dynamicFields;

    /**
     * Dynamic column position in the design schema. Schema can contain 0 or 1 dynamic columns.
     */
    private final int dynamicFieldPosition;

    /**
     * Actual schema of {@link IndexedRecord}
     */
    private final Schema runtimeSchema;

    /**
     * Constructor sets design and runtime schemas, design schema fields and size, dynamic field position and number of dynamic
     * fields
     * 
     * @param designSchema design schema
     * @param runtimeSchema runtime schema
     */
    DynamicIndexMapperByName(Schema designSchema, Schema runtimeSchema) {
        this.designSchema = designSchema;
        this.designFields = designSchema.getFields();
        this.designSchemaSize = designFields.size();
        this.runtimeSchema = runtimeSchema;
        int runtimeSchemaSize = runtimeSchema.getFields().size();
        this.dynamicFields = runtimeSchemaSize - designSchemaSize;

        if (AvroUtils.isIncludeAllFields(designSchema)) {
            dynamicFieldPosition = Integer.valueOf(designSchema.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION));
        } else {
            throw new IllegalArgumentException("Runtime schema doesn't contain dynamic field");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * If there is dynamic field corresponding value has no sense, because there are several runtime fields which corresponds
     * dynamic field.
     * That's why -1 value is set for dynamic field index.
     */
    @Override
    public int[] computeIndexMap() {
        int[] indexMap = new int[designSchemaSize + 1];
        for (int i = 0; i < dynamicFieldPosition; i++) {
            Field designField = designFields.get(i);
            String fieldName = designField.name();
            Field runtimeField = runtimeSchema.getField(fieldName);
            indexMap[i] = runtimeField.pos();
        }
        indexMap[dynamicFieldPosition] = -1;
        for (int i = dynamicFieldPosition; i < designSchemaSize; i++) {
            Field designField = designFields.get(i);
            String fieldName = designField.name();
            Field runtimeField = runtimeSchema.getField(fieldName);
            indexMap[i + 1] = runtimeField.pos();
        }
        return indexMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> computeDynamicFieldsIndexes() {
        ArrayList<Integer> dynamicFieldsIndexes = new ArrayList<Integer>(dynamicFields);
        for (Field runtimeField : runtimeSchema.getFields()) {
            String fieldName = runtimeField.name();
            Field designField = designSchema.getField(fieldName);
            if (designField == null) {
                dynamicFieldsIndexes.add(runtimeField.pos());
            }
        }
        return dynamicFieldsIndexes;
    }

}
