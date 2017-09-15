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
 * Converts String datum to avro long type and vice versa
 */
public class StringLongConverter extends StringConverter<Long> {

    private static final Schema LONG_SCHEMA = AvroUtils._long();

    /**
     * Returns schema of long avro type
     * 
     * @return schema of long avro type
     */
    @Override
    public Schema getSchema() {
        return LONG_SCHEMA;
    }

    @Override
    public String convertToDatum(Long value) {
        return value.toString();
    }

    @Override
    public Long convertToAvro(String value) {
        return Long.parseLong(value);
    }

}
