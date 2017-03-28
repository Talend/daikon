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
package org.talend.daikon.avro;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;

/**
 * Utility class for avro {@link LogicalType}
 */
public final class LogicalTypeUtils {

    private LogicalTypeUtils() {
        // Class provides static utility methods and shouldn't be instantiated
    }

    /**
     * Checks whether specified schema has logical timestamp-millis type.
     * Its type should be LONG and
     * Its logical type should be "timestamp-millis"
     * 
     * @param schema avro schema
     * @return true, if schema has logical timestamp-millis type
     */
    public static boolean isLogicalTimestampMillis(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        return Type.LONG == schema.getType() && "timestamp-millis".equals(logicalType.getName());
    }

    /**
     * Checks whether specified schema has logical timestamp-micros type.
     * Its type should be LONG and
     * Its logical type should be "timestamp-micros"
     * 
     * @param schema avro schema
     * @return true, if schema has logical timestamp-micros type
     */
    public static boolean isLogicalTimestampMicros(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        return Type.LONG == schema.getType() && "timestamp-micros".equals(logicalType.getName());
    }

    /**
     * Checks whether specified schema has logical date type
     * Its type should be INT and
     * Its logical type should be "date"
     * 
     * @param schema avro schema
     * @return true, if schema has logical date type
     */
    public static boolean isLogicalDate(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        return Type.INT == schema.getType() && "date".equals(logicalType.getName());
    }

    /**
     * Checks whether specified schema has logical time-millis type
     * It should have type INT and logical type "time-millis"
     * 
     * @param schema avro schema
     * @return true, if schema has logical time-millis type
     */
    public static boolean isLogicalTimeMillis(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        return Type.INT == schema.getType() && "time-millis".equals(logicalType.getName());
    }

    /**
     * Checks whether specified schema has logical time-micros type
     * It should have type LONG and logical type "time-micros"
     * 
     * @param schema avro schema
     * @return true, if schema has logical time-micros type
     */
    public static boolean isLogicalTimeMicros(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        return Type.LONG == schema.getType() && "time-micros".equals(logicalType.getName());
    }
}
