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

import static org.junit.Assert.assertEquals;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.daikon.avro.AvroUtils;

/**
 * Unit tests for {@link StringStringConverter}
 */
public class StringStringConverterTest extends StringConverterTest {

    @Override
    StringStringConverter createConverter() {
        return new StringStringConverter();
    }

    /**
     * Checks {@link StringStringConverter#getSchema()} returns string schema
     */
    @Test
    public void testGetSchema() {
        StringStringConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._string(), schema);
    }

    /**
     * Checks {@link StringStringConverter#convertToDatum(String)} returns
     * "abcd", when "abcd" is passed It should return input argument without any
     * modification
     */
    @Test
    public void testConvertToDatum() {
        StringStringConverter converter = createConverter();
        String value = converter.convertToDatum("abcd");
        assertEquals("abcd", value);
    }

    /**
     * Checks {@link StringStringConverter#convertToAvro(String)} returns
     * "abcd", when "abcd" is passed It should return input argument without any
     * modification
     */
    @Test
    public void testConvertToAvro() {
        StringStringConverter converter = createConverter();
        String value = converter.convertToAvro("abcd");
        assertEquals("abcd", value);
    }

}
