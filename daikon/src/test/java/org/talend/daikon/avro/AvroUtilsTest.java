package org.talend.daikon.avro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Unit tests for {AvroUtils}.
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
        Map map = AvroUtils.makeFieldMap(s);
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
}
