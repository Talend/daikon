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
package org.talend.daikon.di.converter;

import static org.junit.Assert.assertEquals;

import org.apache.avro.Schema;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.avro.AvroUtils;

/**
 * Unit-tests for {@link SchemaConverter}
 */
public class SchemaConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns String avro schema in case "id_String" di type is
     * passed
     */
    @Test
    public void testDiToAvroString() {
        Schema expectedSchema = AvroUtils._string();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_String", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Boolean avro schema in case "id_Boolean" di type
     * is passed
     */
    @Test
    public void testDiToAvroBoolean() {
        Schema expectedSchema = AvroUtils._boolean();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Boolean", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Integer avro schema in case "id_Integer" di type
     * is passed
     */
    @Test
    public void testDiToAvroInteger() {
        Schema expectedSchema = AvroUtils._int();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Integer", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Long avro schema in case "id_Long" di type is
     * passed
     */
    @Test
    public void testDiToAvroLong() {
        Schema expectedSchema = AvroUtils._long();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Long", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Double avro schema in case "id_Double" di type is
     * passed
     */
    @Test
    public void testDiToAvroDouble() {
        Schema expectedSchema = AvroUtils._double();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Double", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Float avro schema in case "id_Float" di type is
     * passed
     */
    @Test
    public void testDiToAvroFloat() {
        Schema expectedSchema = AvroUtils._float();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Float", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Integer avro schema with
     * "java-class"=java.lang.Byte
     * in case "id_Byte" di type is passed
     */
    @Test
    public void testDiToAvroByte() {
        Schema expectedSchema = AvroUtils._byte();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Byte", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Integer avro schema with
     * "java-class"=java.lang.Short
     * in case "id_Short" di type is passed
     */
    @Test
    public void testDiToAvroShort() {
        Schema expectedSchema = AvroUtils._short();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Short", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns String avro schema with
     * "java-class"=java.lang.Character
     * in case "id_Character" di type is passed
     */
    @Test
    public void testDiToAvroCharacter() {
        Schema expectedSchema = AvroUtils._character();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Character", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns String avro schema with
     * "java-class"=java.math.BigDecimal
     * in case "id_BigDecimal" di type is passed
     */
    @Test
    public void testDiToAvroBigDecimal() {
        Schema expectedSchema = AvroUtils._decimal();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_BigDecimal", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns Long avro schema with "java-class"=java.util.Date
     * in case "id_Date" di type is passed
     */
    @Test
    public void testDiToAvroDate() {
        Schema expectedSchema = AvroUtils._date();
        assertEquals(expectedSchema, SchemaConverter.diToAvro("id_Date", null));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} throws {@link UnsupportedOperationException}
     * in case unsupported type is passed
     */
    @Test
    public void testDiToAvroNotSupporter() {
        thrown.expect(UnsupportedOperationException.class);
        SchemaConverter.diToAvro("Unsupported", null);
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns logical date avro schema with in case "date"
     * logical type is passed
     */
    @Test
    public void testDiToAvroLogicalDate() {
        Schema expectedSchema = AvroUtils._logicalDate();
        assertEquals(expectedSchema, SchemaConverter.diToAvro(null, "date"));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns logical time-millis avro schema with in case
     * "time-millis" logical type is passed
     */
    @Test
    public void testDiToAvroLogicalTime() {
        Schema expectedSchema = AvroUtils._logicalTime();
        assertEquals(expectedSchema, SchemaConverter.diToAvro(null, "time-millis"));
    }

    /**
     * Checks {@link SchemaConverter#diToAvro(String, String)} returns logical timestamp-millis avro schema with in case
     * "timestamp-millis" logical type is passed
     */
    @Test
    public void testDiToAvroLogicalTimestamp() {
        Schema expectedSchema = AvroUtils._logicalTimestamp();
        assertEquals(expectedSchema, SchemaConverter.diToAvro(null, "timestamp-millis"));
    }

}
