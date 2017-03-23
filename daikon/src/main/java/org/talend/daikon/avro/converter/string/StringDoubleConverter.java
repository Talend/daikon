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
 * Converts String datum to avro double type and vice versa
 */
public class StringDoubleConverter extends StringConverter<Double> {

    private static final Schema DOUBLE_SCHEMA = AvroUtils._double();

    /**
     * Returns schema of double avro type
     * 
     * @return schema of double avro type
     */
    @Override
    public Schema getSchema() {
        return DOUBLE_SCHEMA;
    }

    @Override
    public String convertToDatum(Double value) {
        return value.toString();
    }

    @Override
    public Double convertToAvro(String value) {
        return Double.valueOf(value);
    }

}
