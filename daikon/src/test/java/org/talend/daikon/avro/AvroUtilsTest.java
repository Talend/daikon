package org.talend.daikon.avro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

/**
 * Unit tests for {@link AvroUtils}.
 */
public class AvroUtilsTest {

    @Test
    public void testWrapAsNullable() {
        // An already nullable schema is passed through.
        Schema alreadyNullable = SchemaBuilder.builder().nullable().booleanType();
        assertThat(AvroUtils.wrapAsNullable(alreadyNullable), sameInstance(alreadyNullable));

        // A non-nullable schema is converted to a union.
        Schema unnullable = SchemaBuilder.builder().booleanType();
        Schema expected = SchemaBuilder.builder().unionOf().type(unnullable).and().nullType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(unnullable), is(expected));

        // A union with three elements, with the middle one null is passed through.
        Schema mixWithNullable = SchemaBuilder.builder().unionOf().booleanType().and().nullType().and().intType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(mixWithNullable), sameInstance(mixWithNullable));

        // A union with three elements, none nullable, has a nullable added. to the end.
        Schema mixWithNoNullable = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().endUnion();
        expected = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().and().nullType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(mixWithNoNullable), is(expected));
    }

    @Test
    public void testUnwrapIfNullable() {
        // An already nullable schema is passed through.
        Schema alreadyNullable = SchemaBuilder.builder().nullable().booleanType();
        Schema expected = SchemaBuilder.builder().booleanType();
        assertThat(AvroUtils.unwrapIfNullable(alreadyNullable), is(expected));

        // A non-nullable schema is passed through.
        Schema unnullable = SchemaBuilder.builder().booleanType();
        assertThat(AvroUtils.unwrapIfNullable(unnullable), sameInstance(unnullable));

        // A union with three elements, with the middle one null is passed through.
        Schema mixWithNullable = SchemaBuilder.builder().unionOf().booleanType().and().nullType().and().intType().endUnion();
        expected = SchemaBuilder.builder().unionOf().booleanType().and().intType().endUnion();
        assertThat(AvroUtils.unwrapIfNullable(mixWithNullable), is(expected));

        // A union with three elements, none nullable, is passed through.
        Schema mixWithNoNullable = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().endUnion();
        assertThat(AvroUtils.unwrapIfNullable(mixWithNoNullable), sameInstance(mixWithNoNullable));
    }

    @Test
    public void testMakeFieldMap() {

        Schema s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        Map<String, Field> map = AvroUtils.makeFieldMap(s);
        assertEquals("field1", ((Schema.Field) map.get("field1")).name());
        assertEquals("field2", ((Schema.Field) map.get("field2")).name());
    }

    @Test
    public void testSetProperty() {
        Schema s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        s = AvroUtils.setProperty(s, "where", "here");
        assertThat(s.getProp("where"), is("here"));
        s = AvroUtils.setProperty(s, "where", "there");
        assertThat(s.getProp("where"), is("there"));
    }

    @Test
    public void testAppendFields() {
        Schema s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        s = AvroUtils.appendFields(s, new Field("a1", SchemaBuilder.builder().stringType(), null, (Object) null),
                new Field("a2", SchemaBuilder.builder().intType(), null, (Object) null));
        assertEquals(4, s.getFields().size());
        assertEquals("a1", s.getFields().get(2).name());
        assertEquals("a2", s.getFields().get(3).name());
    }

    @Test
    public void testRemoveFields() {
        Schema s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().name("a1").type().stringType().noDefault().name("a2").type().intType().noDefault()
                .endRecord();
        Set<String> columns = new HashSet<>();
        columns.add("field1");
        columns.add("a1");
        s = AvroUtils.removeFields(s, columns);
        assertEquals(2, s.getFields().size());
        assertEquals("field2", s.getFields().get(0).name());
        assertEquals("a2", s.getFields().get(1).name());
    }

    @Test
    public void testIsIncludeAllFields() {
        Schema s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        assertFalse(AvroUtils.isIncludeAllFields(s));
        s = AvroUtils.setIncludeAllFields(s, true);
        assertTrue(AvroUtils.isIncludeAllFields(s));
        s = AvroUtils.setIncludeAllFields(s, false);
        assertFalse(AvroUtils.isIncludeAllFields(s));
    }

    @Test
    public void testIsSchemaEmpty() {
        Schema s = null;
        assertTrue(AvroUtils.isSchemaEmpty(s));

        s = SchemaBuilder.record("test").fields().endRecord();
        assertTrue(AvroUtils.isSchemaEmpty(s));

        s = AvroUtils.setIncludeAllFields(s, true);
        assertFalse(AvroUtils.isSchemaEmpty(s));

        s = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        assertFalse(AvroUtils.isSchemaEmpty(s));
    }

    @Test
    public void _boolean() throws Exception {
        Schema type = AvroUtils._boolean();
        assertThat(type.getType(), is(Schema.Type.BOOLEAN));
    }

    @Test
    public void _byte() throws Exception {
        Schema type = AvroUtils._byte();
        assertThat(type.getType(), is(Schema.Type.INT));
        assertThat(type.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Byte.class.getCanonicalName()));
    }

    @Test
    public void _bytes() throws Exception {
        Schema type = AvroUtils._bytes();
        assertThat(type.getType(), is(Schema.Type.BYTES));
    }

    @Test
    public void _character() throws Exception {
        Schema type = AvroUtils._character();
        assertThat(type.getType(), is(Schema.Type.STRING));
        assertThat(type.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Character.class.getCanonicalName()));
    }

    @Test
    public void _date() throws Exception {
        Schema type = AvroUtils._date();
        assertThat(type.getType(), is(Schema.Type.LONG));
        assertThat(type.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Date.class.getCanonicalName()));
    }

    @Test
    public void _double() throws Exception {
        Schema type = AvroUtils._double();
        assertThat(type.getType(), is(Schema.Type.DOUBLE));
    }

    @Test
    public void _float() throws Exception {
        Schema type = AvroUtils._float();
        assertThat(type.getType(), is(Schema.Type.FLOAT));
    }

    @Test
    public void _int() throws Exception {
        Schema type = AvroUtils._int();
        assertThat(type.getType(), is(Schema.Type.INT));
    }

    @Test
    public void _long() throws Exception {
        Schema type = AvroUtils._long();
        assertThat(type.getType(), is(Schema.Type.LONG));
    }

    @Test
    public void _short() throws Exception {
        Schema type = AvroUtils._short();
        assertThat(type.getType(), is(Schema.Type.INT));
        assertThat(type.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Short.class.getCanonicalName()));
    }

    @Test
    public void _string() throws Exception {
        Schema type = AvroUtils._string();
        assertThat(type.getType(), is(Schema.Type.STRING));
    }

    @Test
    public void _decimal() throws Exception {
        Schema type = AvroUtils._decimal();
        assertThat(type.getType(), is(Schema.Type.STRING));
        assertThat(type.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(BigDecimal.class.getCanonicalName()));
    }

    @Test
    public void isSameType() throws Exception {
        assertTrue(AvroUtils.isSameType(AvroUtils._string(), AvroUtils._string()));
        assertTrue(!AvroUtils.isSameType(AvroUtils._string(), AvroUtils._character()));
    }

    /**
     * Checks {@link AvroUtils#createEmptySchema()} returns not null avro {@link Schema}, which type is
     * {@link Type#RECORD}, name is "EmptySchema" and it has no fields
     */
    @Test
    public void testCreateEmptySchema() {
        Type expectedType = Type.RECORD;
        String expectedName = "EmptySchema";

        Schema emptySchema = AvroUtils.createEmptySchema();

        assertNotNull(emptySchema);
        Type actualType = emptySchema.getType();
        assertEquals(expectedType, actualType);
        String actualName = emptySchema.getName();
        assertEquals(expectedName, actualName);
        List<Field> fields = emptySchema.getFields();
        assertThat(fields, empty());
    }

    @Test
    public void testgetFieldNames() throws Exception {
        Schema input = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        assertThat(AvroUtils.getFieldNames(input), containsInAnyOrder("field1", "field2"));

    }

    @Test
    public void testCreateRejectSchema() throws Exception {
        Schema input = SchemaBuilder.record("test").fields().name("field1").type().booleanType().noDefault().name("field2").type()
                .stringType().noDefault().endRecord();
        Schema reject = AvroUtils.createRejectSchema(input, "rejectName");

        assertNotNull(reject.getField(AvroUtils.REJECT_FIELD_INPUT));
        assertEquals(input, reject.getField(AvroUtils.REJECT_FIELD_INPUT).schema());
        assertNotNull(reject.getField(AvroUtils.REJECT_FIELD_ERROR_MESSAGE));
        assertEquals(Schema.create(Schema.Type.STRING), reject.getField(AvroUtils.REJECT_FIELD_ERROR_MESSAGE).schema());
    }

    @Test
    public void testIsNumerical() throws Exception {
        assertFalse(AvroUtils.isNumerical(Schema.Type.ARRAY));
        assertFalse(AvroUtils.isNumerical(Schema.Type.BOOLEAN));
        assertFalse(AvroUtils.isNumerical(Schema.Type.BYTES));
        assertTrue(AvroUtils.isNumerical(Schema.Type.DOUBLE));
        assertFalse(AvroUtils.isNumerical(Schema.Type.ENUM));
        assertFalse(AvroUtils.isNumerical(Schema.Type.FIXED));
        assertTrue(AvroUtils.isNumerical(Schema.Type.FLOAT));
        assertTrue(AvroUtils.isNumerical(Schema.Type.INT));
        assertTrue(AvroUtils.isNumerical(Schema.Type.LONG));
        assertFalse(AvroUtils.isNumerical(Schema.Type.MAP));
        assertFalse(AvroUtils.isNumerical(Schema.Type.NULL));
        assertFalse(AvroUtils.isNumerical(Schema.Type.RECORD));
        assertFalse(AvroUtils.isNumerical(Schema.Type.STRING));
        assertFalse(AvroUtils.isNumerical(Schema.Type.UNION));
    }

    @Test
    public void testIsString() throws Exception {
        assertFalse(AvroUtils.isString(Schema.Type.ARRAY));
        assertFalse(AvroUtils.isString(Schema.Type.BOOLEAN));
        assertFalse(AvroUtils.isString(Schema.Type.BYTES));
        assertFalse(AvroUtils.isString(Schema.Type.DOUBLE));
        assertFalse(AvroUtils.isString(Schema.Type.ENUM));
        assertFalse(AvroUtils.isString(Schema.Type.FIXED));
        assertFalse(AvroUtils.isString(Schema.Type.FLOAT));
        assertFalse(AvroUtils.isString(Schema.Type.INT));
        assertFalse(AvroUtils.isString(Schema.Type.LONG));
        assertFalse(AvroUtils.isString(Schema.Type.MAP));
        assertFalse(AvroUtils.isString(Schema.Type.NULL));
        assertFalse(AvroUtils.isString(Schema.Type.RECORD));
        assertTrue(AvroUtils.isString(Schema.Type.STRING));
        assertFalse(AvroUtils.isString(Schema.Type.UNION));
    }

    @Test
    public void testLogicalDate() {
        Schema expected = new Schema.Parser().parse("{\"type\":\"int\",\"logicalType\":\"date\"}");

        Schema actual = AvroUtils._logicalDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testLogicalTimeMillis() {
        Schema expected = new Schema.Parser().parse("{\"type\":\"int\",\"logicalType\":\"time-millis\"}");

        Schema actual = AvroUtils._logicalTime();
        assertEquals(expected, actual);
    }

    @Test
    public void testLogicalTimeMicros() {
        Schema expected = new Schema.Parser().parse("{\"type\":\"long\",\"logicalType\":\"time-micros\"}");

        Schema actual = AvroUtils._logicalTimeMicros();
        assertEquals(expected, actual);
    }

    @Test
    public void testLogicalTimestampMillis() {
        Schema expected = new Schema.Parser().parse("{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}");

        Schema actual = AvroUtils._logicalTimestamp();
        assertEquals(expected, actual);
    }

    @Test
    public void testLogicalTimestampMicros() {
        Schema expected = new Schema.Parser().parse("{\"type\":\"long\",\"logicalType\":\"timestamp-micros\"}");

        Schema actual = AvroUtils._logicalTimestampMicros();
        assertEquals(expected, actual);
    }
}
