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
     * Checks {@link LogicalTypeUtils#isLogicalTimestamp(Schema)} returns <code>true</code> if timestamp-millis schema is passed
     */
    @Test
    public void testIsLogicalTimestampMillis() {
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTimestamp(timestampSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestamp(Schema)} returns <code>true</code> if timestamp-micros schema is passed
     */
    @Test
    public void testIsLogicalTimestampMicros() {
        Schema timestampSchema = LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTimestamp(timestampSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTimestamp(Schema)} returns <code>false</code> if not timestamp schema is passed
     */
    @Test
    public void testIsLogicalTimestampFalse() {
        Schema longSchema = AvroUtils._long();
        assertFalse(LogicalTypeUtils.isLogicalTimestamp(longSchema));
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
     * Checks {@link LogicalTypeUtils#isLogicalTime(Schema))} returns <code>true</code> if time-millis schema is passed
     */
    @Test
    public void testIsLogicalTimeMillis() {
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
        assertTrue(LogicalTypeUtils.isLogicalTime(timeSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTime(Schema))} returns <code>true</code> if time-micros schema is passed
     */
    @Test
    public void testIsLogicalTimeMicros() {
        Schema timeSchema = LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
        assertTrue(LogicalTypeUtils.isLogicalTime(timeSchema));
    }

    /**
     * Checks {@link LogicalTypeUtils#isLogicalTime(Schema))} returns <code>false</code> if not logical time schema is passed
     */
    @Test
    public void testIsLogicalTimeFalse() {
        Schema intSchema = AvroUtils._int();
        assertFalse(LogicalTypeUtils.isLogicalTime(intSchema));
    }

}
