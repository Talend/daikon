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
 * Unit tests for {@link StringToDoubleConverter}.
 */
public class StringToDoubleConverterTest extends StringConverterTest {

    @Override
    StringToDoubleConverter createConverter() {
        return new StringToDoubleConverter();
    }

    /**
     * Checks {@link StringToDoubleConverter#getSchema()} returns double schema
     */
    @Test
    public void testGetSchema() {
        StringToDoubleConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._double(), schema);
    }

    /**
     * Checks {@link StringToDoubleConverter#convertToDatum(Double)} returns
     * "12.34", when <code>12.34<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringToDoubleConverter converter = createConverter();
        String value = converter.convertToDatum(12.34);
        assertEquals("12.34", value);
    }

    /**
     * Checks {@link StringToDoubleConverter#convertToAvro(String)} returns
     * <code>12.34<code>, when "12.34" is passed
     */
    @Test
    public void testConvertToAvro() {
        StringToDoubleConverter converter = createConverter();
        double value = converter.convertToAvro("12.34");
        assertEquals(12.34, value, 0D);
    }

    /**
     * Checks {@link StringToDoubleConverter#convertToAvro(String)} throws
     * {@link NumberFormatException} if not a number string is passed
     */
    @Test(expected = NumberFormatException.class)
    public void testConvertToAvroNotDouble() {
        StringToDoubleConverter converter = createConverter();
        converter.convertToAvro("not a double");
    }

}
