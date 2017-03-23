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
package org.talend.daikon.avro.converter.string;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

/**
 * Converts String datum to avro float type and vice versa
 */
public class StringFloatConverter extends StringConverter<Float> {

    private static final Schema FLOAT_SCHEMA = AvroUtils._float();

    /**
     * Returns schema of float avro type
     * 
     * @return schema of float avro type
     */
    @Override
    public Schema getSchema() {
        return FLOAT_SCHEMA;
    }

    @Override
    public String convertToDatum(Float value) {
        return value.toString();
    }

    @Override
    public Float convertToAvro(String value) {
        return Float.valueOf(value);
    }

}
