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
package org.talend.daikon.di.converter;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.LogicalTypeUtils;

/**
 * Converts DI metadata to avro schema and vice versa
 */
public final class SchemaConverter {

    private SchemaConverter() {
        // Class provides static utility methods and shouldn't be instantiated
    }

    /**
     * Converts DI type to Avro field schema
     * 
     * @param diType data integration native type
     * @param logicalType avro logical type
     * @return field schema
     * @throws {@link UnsupportedOperationException} in case of unsupported di type or logical type
     */
    public static Schema diToAvro(String diType, String logicalType) {
        Schema fieldSchema = LogicalTypeUtils.getSchemaByLogicalType(logicalType);
        if (fieldSchema != null) {
            return fieldSchema;
        }

        switch (diType) {
        case "id_String":
            return Schema.create(Schema.Type.STRING);
        case "id_Boolean":
            return Schema.create(Schema.Type.BOOLEAN);
        case "id_Integer":
            return Schema.create(Schema.Type.INT);
        case "id_Long":
            return Schema.create(Schema.Type.LONG);
        case "id_Double":
            return Schema.create(Schema.Type.DOUBLE);
        case "id_Float":
            return Schema.create(Schema.Type.FLOAT);
        case "id_Byte":
            return AvroUtils._byte();
        case "id_Short":
            return AvroUtils._short();
        case "id_Character":
            return AvroUtils._character();
        case "id_BigDecimal":
            return AvroUtils._decimal();
        case "id_Date":
            return AvroUtils._date();
        default:
            throw new UnsupportedOperationException("Unrecognized type " + diType);
        }
    }

}
