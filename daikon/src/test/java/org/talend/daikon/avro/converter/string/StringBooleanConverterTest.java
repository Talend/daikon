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
 * Unit tests for {@link StringBooleanConverter}.
 */
public class StringBooleanConverterTest extends StringConverterTest {

    @Override
    StringBooleanConverter createConverter() {
        return new StringBooleanConverter();
    }

    /**
     * Checks {@link StringBooleanConverter#getSchema()} returns boolean
     * schema
     */
    @Test
    public void testGetSchema() {
        StringBooleanConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._boolean(), schema);
    }

    /**
     * Checks {@link StringBooleanConverter#convertToDatum(Boolean)} returns
     * "true", when <code>true<code> is passed
     */
    @Test
    public void testConvertToDatumTrue() {
        StringBooleanConverter converter = createConverter();
        String value = converter.convertToDatum(true);
        assertEquals("true", value);
    }

    /**
     * Checks {@link StringBooleanConverter#convertToDatum(Boolean)} returns
     * "false", when <code>false<code> is passed
     */
    @Test
    public void testConvertToDatumFalse() {
        StringBooleanConverter converter = createConverter();
        String value = converter.convertToDatum(false);
        assertEquals("false", value);
    }

    /**
     * Checks {@link StringBooleanConverter#convertToAvro(String)} returns
     * <code>true<code>, when "true" is passed
     */
    @Test
    public void testConvertToAvroTrue() {
        StringBooleanConverter converter = createConverter();
        boolean value = converter.convertToAvro("true");
        assertEquals(true, value);
    }

    /**
     * Checks {@link StringBooleanConverter#convertToAvro(String)} returns
     * <code>false<code>, when "false" is passed
     */
    @Test
    public void testConvertToAvroFalse() {
        StringBooleanConverter converter = createConverter();
        boolean value = converter.convertToAvro("false");
        assertEquals(false, value);
    }
}
