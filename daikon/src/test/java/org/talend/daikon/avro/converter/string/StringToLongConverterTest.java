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
 * Unit tests for {@link StringToLongConverter}
 */
public class StringToLongConverterTest extends StringConverterTest {

    @Override
    StringToLongConverter createConverter() {
        return new StringToLongConverter();
    }

    /**
     * Checks {@link StringToLongConverter#getSchema()} returns long schema
     */
    @Test
    public void testGetSchema() {
        StringToLongConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._long(), schema);
    }

    /**
     * Checks {@link StringToLongConverter#convertToDatum(Long)} returns
     * "1234567890", when <code>1234567890<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringToLongConverter converter = createConverter();
        String value = converter.convertToDatum(1234567890l);
        assertEquals("1234567890", value);
    }

    /**
     * Checks {@link StringToLongConverter#convertToAvro(String)} returns
     * <code>1234567890<code>, when "1234567890" is passed
     */
    @Test
    public void testConvertToAvro() {
        StringToLongConverter converter = createConverter();
        long value = converter.convertToAvro("1234567890");
        assertEquals(1234567890l, value);
    }

    /**
     * Checks {@link StringToLongConverter#convertToAvro(String)} throws
     * {@link NumberFormatException} if not a number string is passed
     */
    @Test(expected = NumberFormatException.class)
    public void testConvertToAvroNotLong() {
        StringToLongConverter converter = createConverter();
        converter.convertToAvro("not an long");
    }
}
