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
 * Unit tests for {@link StringTimestampConverter}
 */
public class StringTimestampConverterTest extends StringConverterTest {

    @Override
    StringTimestampConverter createConverter() {
        return new StringTimestampConverter();
    }

    /**
     * Checks {@link StringTimestampConverter#getSchema()} returns logical
     * timestamp schema
     */
    @Test
    public void testGetSchema() {
        StringTimestampConverter converter = createConverter();
        Schema schema = converter.getSchema();
        assertEquals(AvroUtils._logicalTimestamp(), schema);
    }

    /**
     * Checks {@link StringTimestampConverter#convertToDatum(Long)} returns
     * "13-02-2009 11:31:30:123", when <code>1234567890123l<code> is passed
     */
    @Test
    public void testConvertToDatum() {
        StringTimestampConverter converter = createConverter();
        String value = converter.convertToDatum(1234567890123l);
        assertEquals("13-02-2009 11:31:30:123", value);
    }

    /**
     * Checks {@link StringTimestampConverter#convertToDatum(Long)} returns
     * "21-03-2017", when <code>1490054400000l<code> is passed and "dd-MM-yyyy"
     * pattern is used
     */
    @Test
    public void testConvertToDatumPattern() {
        StringTimestampConverter converter = new StringTimestampConverter("dd-MM-yyyy");
        String value = converter.convertToDatum(1490054400000l);
        assertEquals("21-03-2017", value);
    }

    /**
     * Checks {@link StringTimestampConverter#convertToAvro(String)} returns
     * <code>1490054400000l<code>, when "21-03-2017" is passed and "dd-MM-yyyy"
     * pattern is used
     */
    @Test
    public void testConvertToAvroPattern() {
        StringTimestampConverter converter = new StringTimestampConverter("dd-MM-yyyy");
        long value = converter.convertToAvro("21-03-2017");
        assertEquals(1490054400000l, value);
    }

    /**
     * Checks {@link StringTimestampConverter#convertToAvro(String)} throws
     * {@link IllegalArgumentException} when input argument doesn't match date
     * pattern, which was passed to constructor
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertToAvroPatternWrong() {
        StringTimestampConverter converter = new StringTimestampConverter("dd-MM-yyyy");
        converter.convertToAvro("21.03.2017");
    }

}
