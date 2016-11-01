package org.talend.daikon.di;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

/**
 * {@link DynamicIndexMapper} implementation, which match fields according their indexes
 */
class DynamicIndexMapperByIndex implements DynamicIndexMapper {

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
     * Constructor sets design schema size, number of dynamic fields and dynamic field position
     * 
     * @param designSchema design schema
     * @param runtimeSchema runtime schema
     */
    DynamicIndexMapperByIndex(Schema designSchema, Schema runtimeSchema) {
        this.designSchemaSize = designSchema.getFields().size();
        int runtimeSchemaSize = runtimeSchema.getFields().size();
        this.dynamicFields = runtimeSchemaSize - designSchemaSize;

        if (AvroUtils.isIncludeAllFields(designSchema)) {
            dynamicFieldPosition = Integer.valueOf(designSchema.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION));
        } else {
            throw new IllegalArgumentException("Design schema doesn't contain dynamic field");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * If there is dynamic field corresponding value has no sense, because there are several runtime fields which corresponds dynamic field.
     * That's why -1 value is set for dynamic field index. All other fields should be shifted on dynamicFields positions forward
     */
    @Override
    public int[] computeIndexMap() {
        int[] indexMap = new int[designSchemaSize + 1];
        for (int i = 0; i < dynamicFieldPosition; i++) {
            indexMap[i] = i;
        }
        indexMap[dynamicFieldPosition] = -1;
        for (int i = dynamicFieldPosition + 1; i < designSchemaSize + 1; i++) {
            indexMap[i] = dynamicFields + i - 1;
        }
        return indexMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> computeDynamicFieldsIndexes() {
        ArrayList<Integer> dynamicFieldsIndexes = new ArrayList<>(dynamicFields);
        for (int i = 0; i < dynamicFields; i++) {
            int dynamicFieldIndex = dynamicFieldPosition + i;
            dynamicFieldsIndexes.add(dynamicFieldIndex);
        }
        return dynamicFieldsIndexes;
    }

}
