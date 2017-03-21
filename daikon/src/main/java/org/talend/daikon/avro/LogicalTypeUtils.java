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
     * Checks whether specified schema has logical timestamp type.
     * Its type should be LONG and
     * Its logical type should be either "timestamp-millis" or "timestamp-micros"
     * 
     * @param schema avro schema
     * @return true, if schema has logical timestamp type
     */
    public static boolean isLogicalTimestamp(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        Type type = schema.getType();
        String logicalTypeName = logicalType.getName();
        return type == Type.LONG
                && ("timestamp-millis".equals(logicalTypeName) || "timestamp-micros".equals(logicalType.getName()));
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
        Type type = schema.getType();
        String logicalTypeName = logicalType.getName();
        return type == Type.INT && "date".equals(logicalTypeName);
    }

    /**
     * Checks whether specified schema has logical time type
     * It should have type LONG and logical type "time-micros" or
     * type INT and logical type "time-millis"
     * 
     * @param schema avro schema
     * @return true, if schema has logical time type
     */
    public static boolean isLogicalTime(Schema schema) {
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return false;
        }
        Type type = schema.getType();
        String logicalTypeName = logicalType.getName();
        if (type == Type.INT && "time-millis".equals(logicalTypeName)) {
            return true;
        } else if (type == Type.LONG && "time-micros".equals(logicalTypeName)) {
            return true;
        } else {
            return false;
        }
    }
}
