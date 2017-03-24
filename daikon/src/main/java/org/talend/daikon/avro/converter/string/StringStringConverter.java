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
 * Converts String datum to avro string type and vice versa Actually returns
 * arguments unchanged. So it is used as API implementation
 */
public class StringStringConverter extends StringConverter<String> {

    private static final Schema STRING_SCHEMA = AvroUtils._string();

    /**
     * Returns schema of string avro type
     * 
     * @return schema of string avro type
     */
    @Override
    public Schema getSchema() {
        return STRING_SCHEMA;
    }

    @Override
    public String convertToDatum(String value) {
        return value;
    }

    @Override
    public String convertToAvro(String value) {
        return value;
    }

}
