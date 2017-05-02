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
package org.talend.daikon.avro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.junit.Test;

/**
 * Unit-tests for {@link LogicalTypeUtils}
 */
public class LogicalTypeUtilsTest {

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestampMillis(Schema)} returns <code>true</code> if timestamp-millis schema is
     * passed
     */
    @Test
    public void testIsLogicalTimestampMillis() {
        Schema timestampMillisSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTimestampMillis(timestampMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestampMillis(Schema)} returns <code>false</code> if not timestamp-millis schema
     * is passed
     */
    @Test
    public void testIsLogicalTimestampMillisFalse() {
        Schema notTimestampMillisSchema = LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertFalse(LogicalTypeUtils.isLogicalTimestampMillis(notTimestampMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestampMicros(Schema)} returns <code>true</code> if timestamp-micros schema is
     * passed
     */
    @Test
    public void testIsLogicalTimestampMicros() {
        Schema timestampMicrosSchema = LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTimestampMicros(timestampMicrosSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestampMicros(Schema)} returns <code>false</code> if not timestamp-micros schema
     * is passed
     */
    @Test
    public void testIsLogicalTimestampMicrosFalse() {
        Schema notTimestampMicrosSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        assertFalse(LogicalTypeUtils.isLogicalTimestampMicros(notTimestampMicrosSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalDate(Schema)} returns <code>true</code> if logical date schema is passed
     */
    @Test
    public void testIsLogicalDateTrue() {
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT));
        assertTrue(LogicalTypeUtils.isLogicalDate(dateSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalDate(Schema)} returns <code>false</code> if not logical date schema is passed
     */
    @Test
    public void testIsLogicalDateFalse() {
        Schema intSchema = AvroUtils._int();
        assertFalse(LogicalTypeUtils.isLogicalDate(intSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimeMillis(Schema)} returns <code>true</code> if time-millis schema is passed
     */
    @Test
    public void testIsLogicalTimeMillis() {
        Schema timeMillisSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
        assertTrue(LogicalTypeUtils.isLogicalTimeMillis(timeMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimeMillis(Schema)} returns <code>false</code> if not time-millis schema is passed
     */
    @Test
    public void testIsLogicalTimeMillisFalse() {
        Schema notTimeMillisSchema = LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertFalse(LogicalTypeUtils.isLogicalTimeMillis(notTimeMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimeMicros(Schema)} returns <code>true</code> if time-micros schema is passed
     */
    @Test
    public void testIsLogicalTimeMicros() {
        Schema timeMicrosSchema = LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTimeMicros(timeMicrosSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimeMicros(Schema)} returns <code>false</code> if not time-micros schema is passed
     */
    @Test
    public void testIsLogicalTimeMicrosFalse() {
        Schema notTimeMicrosSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
        assertFalse(LogicalTypeUtils.isLogicalTimeMicros(notTimeMicrosSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns <code>null</code> if incoming schema has no logical type
     */
    @Test
    public void testGetLogicalTypeNameNull() {
        Schema longSchema = AvroUtils._long();
        assertNull(LogicalTypeUtils.getLogicalTypeName(longSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns "date" if incoming schema has "date" logical type
     */
    @Test
    public void testGetLogicalTypeNameDate() {
        Schema logicalDateSchema = AvroUtils._logicalDate();
        assertEquals(LogicalTypeUtils.DATE, LogicalTypeUtils.getLogicalTypeName(logicalDateSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns "timestamp-millis" if incoming schema has
     * "timestamp-millis" logical type
     */
    @Test
    public void testGetLogicalTypeNameTimestampMillis() {
        Schema timestampMillisSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        assertEquals(LogicalTypeUtils.TIMESTAMP_MILLIS, LogicalTypeUtils.getLogicalTypeName(timestampMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns "timestamp-micros" if incoming schema has
     * "timestamp-micros" logical type
     */
    @Test
    public void testGetLogicalTypeNameTimestampMicros() {
        Schema timestampMicrosSchema = LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertEquals(LogicalTypeUtils.TIMESTAMP_MICROS, LogicalTypeUtils.getLogicalTypeName(timestampMicrosSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns "time-millis" if incoming schema has "time-millis"
     * logical type
     */
    @Test
    public void testGetLogicalTypeNameTimeMillis() {
        Schema timeMillisSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
        assertEquals(LogicalTypeUtils.TIME_MILLIS, LogicalTypeUtils.getLogicalTypeName(timeMillisSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#getLogicalTypeName(Schema)} returns "time-micros" if incoming schema has "time-micros"
     * logical type
     */
    @Test
    public void testGetLogicalTypeNameTimeMicros() {
        Schema timeMicrosSchema = LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertEquals(LogicalTypeUtils.TIME_MICROS, LogicalTypeUtils.getLogicalTypeName(timeMicrosSchema));
    }

    @Test
    public void testGetSchemaByLogicalTypeDate() {
        Schema expected = AvroUtils._logicalDate();

        Schema actual = LogicalTypeUtils.getSchemaByLogicalType("date");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSchemaByLogicalTypeTimeMillis() {
        Schema expected = AvroUtils._logicalTime();

        Schema actual = LogicalTypeUtils.getSchemaByLogicalType("time-millis");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSchemaByLogicalTypeTimeMicros() {
        Schema expected = AvroUtils._logicalTimeMicros();

        Schema actual = LogicalTypeUtils.getSchemaByLogicalType("time-micros");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSchemaByLogicalTypeTimestampMillis() {
        Schema expected = AvroUtils._logicalTimestamp();

        Schema actual = LogicalTypeUtils.getSchemaByLogicalType("timestamp-millis");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSchemaByLogicalTypeTimestampMicros() {
        Schema expected = AvroUtils._logicalTimestampMicros();

        Schema actual = LogicalTypeUtils.getSchemaByLogicalType("timestamp-micros");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSchemaByLogicalTypeNull() {
        assertNull(LogicalTypeUtils.getSchemaByLogicalType(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSchemaByLogicalTypeNotSupported() {
        LogicalTypeUtils.getSchemaByLogicalType("unsupported");
    }

}
