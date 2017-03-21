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
 * Unit tests for {@link StringToFloatConverter}.
 */
public class StringToFloatConverterTest extends StringConverterTest {

    @Override
    StringToFloatConverter createConverter() {
        return new StringToFloatConverter();
    }

    /**
     * Checks {@link StringToFloatConverter#getSchema()} returns float schema
     */
    @Test
    public void testGetSchema() {
        StringToFloatConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._float(), schema);
    }

    /**
     * Checks {@link StringToFloatConverter#convertToDatum(Float)} returns
     * "12.34", when <code>12.34f<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringToFloatConverter converter = createConverter();
        String value = converter.convertToDatum(12.34f);
        assertEquals("12.34", value);
    }

    /**
     * Checks {@link StringToFloatConverter#convertToAvro(String)} returns
     * <code>12.34<code>, when "12.34" is passed
     */
    @Test
    public void testConvertToAvro() {
        StringToFloatConverter converter = createConverter();
        float value = converter.convertToAvro("12.34");
        assertEquals(12.34f, value, 0f);
    }

    /**
     * Checks {@link StringToFloatConverter#convertToAvro(String)} throws
     * {@link NumberFormatException} if not a number string is passed
     */
    @Test(expected = NumberFormatException.class)
    public void testConvertToAvroNotFloat() {
        StringToFloatConverter converter = createConverter();
        converter.convertToAvro("not a float");
    }
}
