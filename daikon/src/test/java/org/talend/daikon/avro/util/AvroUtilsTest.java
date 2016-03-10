package org.talend.daikon.avro.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.SchemaConstants;

import java.util.Map;

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
    public void testIsDynamic() {
        Schema s = SchemaBuilder.record("dynamic").fields().name("field1").type().bytesType().noDefault().endRecord();
        Schema fs = AvroUtils.setFieldDynamic(s.getField("field1")).schema();
        assertTrue(AvroUtils.isDynamic(fs));
    }

    @Test
    public void testMakeFieldMap() {

        Schema s = SchemaBuilder.record("test")
                .fields()
                .name("field1").type().booleanType().noDefault()
                .name("field2").type().stringType().noDefault()
                .endRecord();
        Map map = AvroUtils.makeFieldMap(s);
        assertEquals("field1", ((Schema.Field)map.get("field1")).name());
        assertEquals("field2", ((Schema.Field)map.get("field2")).name());
    }

}
