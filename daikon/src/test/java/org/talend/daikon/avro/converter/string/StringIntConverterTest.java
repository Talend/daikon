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
 * Unit tests for {@link StringIntConverter}
 */
public class StringIntConverterTest extends StringConverterTest {

    @Override
    StringIntConverter createConverter() {
        return new StringIntConverter();
    }

    /**
     * Checks {@link StringIntConverter#getSchema()} returns int schema
     */
    @Test
    public void testGetSchema() {
        StringIntConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._int(), schema);
    }

    /**
     * Checks {@link StringIntConverter#convertToDatum(Integer)} returns
     * "1234", when <code>1234<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringIntConverter converter = createConverter();
        String value = converter.convertToDatum(1234);
        assertEquals("1234", value);
    }

    /**
     * Checks {@link StringIntConverter#convertToAvro(String)} returns
     * <code>1234<code>, when "1234" is passed
     */
    @Test
    public void testConvertToAvro() {
        StringIntConverter converter = createConverter();
        int value = converter.convertToAvro("1234");
        assertEquals(1234, value);
    }

    /**
     * Checks {@link StringIntConverter#convertToAvro(String)} throws
     * {@link NumberFormatException} if not a number string is passed
     */
    @Test(expected = NumberFormatException.class)
    public void testConvertToAvroNotInt() {
        StringIntConverter converter = createConverter();
        converter.convertToAvro("not an int");
    }

}
