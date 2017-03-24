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

import static org.junit.Assert.*;

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
}
