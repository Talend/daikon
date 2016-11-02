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

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;

/**
 * {@link IndexMapper} implementation, which match fields according their names
 */
class IndexMapperByName implements IndexMapper {

    /**
     * {@link Schema} which was specified by user during setting component properties (at design time)
     * This schema may contain di-specific properties
     */
    private final Schema designSchema;

    /**
     * Actual schema of {@link IndexedRecord}
     */
    private final Schema runtimeSchema;

    /**
     * Constructor sets design and runtime schemas
     * 
     * @param designSchema design schema
     * @param runtimeSchema runtime schema
     */
    IndexMapperByName(Schema designSchema, Schema runtimeSchema) {
        this.designSchema = designSchema;
        this.runtimeSchema = runtimeSchema;
    }

    /**
     * {@inheritDoc}
     * 
     * For each design field it finds corresponding runtime field by name and then uses runtime field's position
     */
    @Override
    public int[] computeIndexMap() {
        int designSchemaSize = designSchema.getFields().size();
        int[] indexMap = new int[designSchemaSize];
        List<Field> designFields = designSchema.getFields();
        for (int i = 0; i < designSchemaSize; i++) {
            Field designField = designFields.get(i);
            String fieldName = designField.name();
            Field runtimeField = runtimeSchema.getField(fieldName);
            indexMap[i] = runtimeField.pos();
        }
        return indexMap;
    }

}
