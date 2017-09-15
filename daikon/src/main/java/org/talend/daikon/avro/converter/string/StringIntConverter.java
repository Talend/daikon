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
 * Converts String datum to avro int type and vice versa
 */
public class StringIntConverter extends StringConverter<Integer> {

    private static final Schema INT_SCHEMA = AvroUtils._int();

    /**
     * Returns schema of int avro type
     * 
     * @return schema of int avro type
     */
    @Override
    public Schema getSchema() {
        return INT_SCHEMA;
    }

    @Override
    public String convertToDatum(Integer value) {
        return value.toString();
    }

    @Override
    public Integer convertToAvro(String value) {
        return Integer.parseInt(value);
    }

}
