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
 * Unit tests for {@link StringFloatConverter}.
 */
public class StringFloatConverterTest extends StringConverterTest {

    @Override
    StringFloatConverter createConverter() {
        return new StringFloatConverter();
    }

    /**
     * Checks {@link StringFloatConverter#getSchema()} returns float schema
     */
    @Test
    public void testGetSchema() {
        StringFloatConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._float(), schema);
    }

    /**
     * Checks {@link StringFloatConverter#convertToDatum(Float)} returns
     * "12.34", when <code>12.34f<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringFloatConverter converter = createConverter();
        String value = converter.convertToDatum(12.34f);
        assertEquals("12.34", value);
    }

    /**
     * Checks {@link StringFloatConverter#convertToAvro(String)} returns
     * <code>12.34<code>, when "12.34" is passed
     */
    @Test
    public void testConvertToAvro() {
        StringFloatConverter converter = createConverter();
        float value = converter.convertToAvro("12.34");
        assertEquals(12.34f, value, 0f);
    }

    /**
     * Checks {@link StringFloatConverter#convertToAvro(String)} throws
     * {@link NumberFormatException} if not a number string is passed
     */
    @Test(expected = NumberFormatException.class)
    public void testConvertToAvroNotFloat() {
        StringFloatConverter converter = createConverter();
        converter.convertToAvro("not a float");
    }
}
