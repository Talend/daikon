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
package org.talend.components.api.test;

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
 * {@link Schema}s are not immutable because, in practice, annotations on the schema can be changed. We use factory
 * methods to guarantee that a "clean" Schema is always returned.
 */
public class SampleSchemas {

    /** The expected classes for datum generated for the fields in recordPrimitives* schemas. */
    public static final Class[] recordPrimitivesClasses = { String.class, Integer.class, Long.class, Float.class, Double.class,
            Boolean.class, ByteBuffer.class, GenericFixed.class };

    /** The expected classes for datum generated for the fields in recordComposites* schemas. */
    public static final Class[] recordCompositesClasses = { IndexedRecord.class, Map.class, List.class, GenericEnumSymbol.class };

    private SampleSchemas() {
    }

    public static final Schema recordSimple() {
        return SchemaBuilder.record("recordSimple").fields() //
                .requiredInt("id") //
                .requiredString("name") //
                .endRecord();
    }

    public static final Schema mapSimple() {
        return SchemaBuilder.map().values(recordSimple());
    }

    public static final Schema arraySimple() {
        return SchemaBuilder.array().items(recordSimple());
    }

    public static final Schema enumSimple() {
        return SchemaBuilder.enumeration("enumSimple").symbols("one", "two", "three");
    }

    public static final Schema recordPrimitivesRequired() {
        return SchemaBuilder.record("recordPrimitivesRequired").fields() //
                .requiredString("col1") //
                .requiredInt("col2") //
                .requiredLong("col3") //
                .requiredFloat("col4") //
                .requiredDouble("col5") //
                .requiredBoolean("col6") //
                .requiredBytes("col7") //
                .name("col8").type().fixed("col8").size(1).noDefault() //
                .endRecord();
    }

    public static final Schema recordPrimitivesNullable() {
        return SchemaBuilder.record("recordPrimitivesNullable").fields() //
                .nullableString("col1", "default") //
                .nullableInt("col2", 1) //
                .nullableLong("col3", 2L) //
                .nullableFloat("col4", 3.0f) //
                .nullableDouble("col5", 4.0) //
                .nullableBoolean("col6", true) //
                .nullableBytes("col7", new byte[] { 0x05 }) //
                .name("col8").type().nullable().fixed("col8").size(1).fixedDefault(new byte[] { 0x06 }) //
                .endRecord();
    }

    public static final Schema recordPrimitivesOptional() {
        return SchemaBuilder.record("recordPrimitivesOptional").fields() //
                .optionalString("col1") //
                .optionalInt("col2") //
                .optionalLong("col3") //
                .optionalFloat("col4") //
                .optionalDouble("col5") //
                .optionalBoolean("col6") //
                .optionalBytes("col7") //
                .name("col8").type().nullable().fixed("col8").size(1).noDefault() //
                .endRecord();
    }

    public static final Schema recordCompositesRequired() {
        return SchemaBuilder.record("recordCompositesRequired").fields() //
                .name("col1").type(recordPrimitivesRequired()).noDefault() //
                .name("col2").type(mapSimple()).noDefault() //
                .name("col3").type(arraySimple()).noDefault() //
                .name("col4").type(enumSimple()).noDefault() //
                .endRecord();
    }
}
