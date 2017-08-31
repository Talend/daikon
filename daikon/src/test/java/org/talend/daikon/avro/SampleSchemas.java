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

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.IndexedRecord;

/**
 * Contains several schemas that are useful for testing.
 *
 * In practice, these instances can't be reused since unit tests can modify Schema properties. A "clean" Schema is
 * always returned.
 */
public class SampleSchemas {

    /** The expected classes for datum generated for the fields in recordPrimitives* schemas. */
    public static final Class[] recordPrimitivesClasses = { String.class, Integer.class, Long.class, Float.class, Double.class,
            Boolean.class, ByteBuffer.class };

    /** The expected classes for datum generated for the fields in recordComposites* schemas. */
    public static final Class[] recordCompositesClasses = { IndexedRecord.class, Map.class, List.class, GenericFixed.class,
            GenericEnumSymbol.class, };

    private SampleSchemas() {
    }

    /**
     * A simple two-column {@link Schema.Type#RECORD} schema for very basic tests.
     */
    public static final Schema recordSimple() {
        return SchemaBuilder.record("recordSimple").fields() //
                .requiredInt("id") //
                .requiredString("name") //
                .endRecord();
    }

    /**
     * A simple {@link Schema.Type#MAP} schema with {@link #recordSimple()} values.
     */
    public static final Schema mapSimple() {
        return SchemaBuilder.map().values(recordSimple());
    }

    /**
     * A simple {@link Schema.Type#ARRAY} schema with {@link #recordSimple()} elements.
     */
    public static final Schema arraySimple() {
        return SchemaBuilder.array().items(recordSimple());
    }

    /**
     * A simple {@link Schema.Type#ARRAY} schema with {@link #arraySimple()} elements.
     */
    public static final Schema arrayOfArraySimple() {
        return SchemaBuilder.array().items(recordSimple());
    }

    /**
     * A simple {@link Schema.Type#ARRAY} schema with LONG elements (to demonstrate arrays of primitives).
     */
    public static final Schema arrayOfLong() {
        return SchemaBuilder.array().items(Schema.create(Schema.Type.LONG));
    }

    /**
     * A simple {@link Schema.Type#ARRAY} schema with nullable or LONG elements (to demonstrate arrays of primitives).
     */
    public static final Schema arrayOfNullableLong() {
        return SchemaBuilder.array().items(AvroUtils.wrapAsNullable(Schema.create(Schema.Type.LONG)));
    }

    /**
     * A simple {@link Schema.Type#ENUM} schema with the symbols: "one", "two", "three".
     */
    public static final Schema enumSimple() {
        return SchemaBuilder.enumeration("enumSimple").symbols("one", "two", "three");
    }

    /**
     * A {@link Schema.Type#RECORD} schema that has fields of all of the Avro primitive types.
     *
     * The primitive types (in field order) are {@link Schema.Type#STRING}, {@link Schema.Type#INT},
     * {@link Schema.Type#LONG}, {@link Schema.Type#FLOAT}, {@link Schema.Type#BOOLEAN}, and {@link Schema.Type#BYTES}.
     *
     * This does not include {@link Schema.Type#ENUM} or {@link Schema.Type#FIXED}, which are serialized as primitives
     * but require a schema to be interpreted.
     *
     * The classes that are expected as values in the fields are defined (in field order) in the constant
     * {@link #recordPrimitivesClasses}.
     *
     * Every column requires a value; nulls are not permitted.
     */
    public static final Schema recordPrimitivesRequired() {
        return SchemaBuilder.record("recordPrimitivesRequired").fields() //
                .requiredString("col1") //
                .requiredInt("col2") //
                .requiredLong("col3") //
                .requiredFloat("col4") //
                .requiredDouble("col5") //
                .requiredBoolean("col6") //
                .requiredBytes("col7") //
                .endRecord();
    }

    /**
     * A {@link Schema.Type#RECORD} schema that has fields of all of the Avro primitive types.
     *
     * The record structure is identical to {@link #recordPrimitivesRequired()}, but nulls are permitted as field
     * values. A non-null default is defined for each field for schema evolution.
     */
    public static final Schema recordPrimitivesNullable() {
        return SchemaBuilder.record("recordPrimitivesNullable").fields() //
                .nullableString("col1", "default") //
                .nullableInt("col2", 1) //
                .nullableLong("col3", 2L) //
                .nullableFloat("col4", 3.0f) //
                .nullableDouble("col5", 4.0) //
                .nullableBoolean("col6", true) //
                .nullableBytes("col7", new byte[] { 0x05 }) //
                .endRecord();
    }

    /**
     * A {@link Schema.Type#RECORD} schema that has fields of all of the Avro primitive types.
     *
     * The record structure is identical to {@link #recordPrimitivesRequired()}, but nulls are permitted as field
     * values. A non-null default is defined for each field for schema evolution.
     */
    public static final Schema recordPrimitivesOptional() {
        return SchemaBuilder.record("recordPrimitivesOptional").fields() //
                .optionalString("col1") //
                .optionalInt("col2") //
                .optionalLong("col3") //
                .optionalFloat("col4") //
                .optionalDouble("col5") //
                .optionalBoolean("col6") //
                .optionalBytes("col7") //
                .endRecord();
    }

    /**
     * A {@link Schema.Type#RECORD} schema that has fields of all of the Avro composite types.
     *
     * The composite types (in field order) are {@link Schema.Type#RECORD}, {@link Schema.Type#MAP},
     * {@link Schema.Type#ARRAY}, {@link Schema.Type#FIXED}, and {@link Schema.Type#ENUM}.
     *
     * Each of the fields require an Avro {@link Schema} to serialize and interpret.
     *
     * The classes that are expected as values in the fields are defined (in field order) in the constant
     * {@link #recordCompositesClasses}.
     */
    public static final Schema recordCompositesRequired() {
        return SchemaBuilder.record("recordCompositesRequired").fields() //
                .name("col1").type(recordPrimitivesRequired()).noDefault() //
                .name("col2").type(mapSimple()).noDefault() //
                .name("col3").type(arraySimple()).noDefault() //
                .name("col4").type().fixed("col4").size(1).noDefault() //
                .name("col5").type(enumSimple()).noDefault() //
                .endRecord();
    }

    public static final Schema recordArraysRequired() {
        return SchemaBuilder.record("recordArrays").fields() //
                .name("col1").type(arraySimple()).noDefault() //
                .name("col2").type(arrayOfArraySimple()).noDefault() //
                .name("col3").type(arrayOfLong()).noDefault() //
                .name("col4").type(arrayOfNullableLong()).noDefault() //
                .endRecord();
    }

}
