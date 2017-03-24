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
 * Converts String datum to avro boolean type and vice versa
 */
public class StringBooleanConverter extends StringConverter<Boolean> {

    private static final Schema BOOLEAN_SCHEMA = AvroUtils._boolean();

    /**
     * Returns schema of boolean avro type
     * 
     * @return schema of boolean avro type
     */
    @Override
    public Schema getSchema() {
        return BOOLEAN_SCHEMA;
    }

    @Override
    public String convertToDatum(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean convertToAvro(String value) {
        return Boolean.parseBoolean(value);
    }

}
