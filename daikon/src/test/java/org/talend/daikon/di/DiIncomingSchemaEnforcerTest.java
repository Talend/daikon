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
package org.talend.daikon.di;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Unit tests for {@link DiIncomingSchemaEnforcer}.
 */
@SuppressWarnings("nls")
public class DiIncomingSchemaEnforcerTest {

    /**
     * An actual record that a component would like to be emitted, which may or may not contain enriched schema
     * information.
     */
    private IndexedRecord componentRecord;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Schema componentSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .name("address").type().stringType().noDefault() //
                .name("comment").type().stringType().noDefault() //
                .endRecord();
        componentRecord = new GenericData.Record(componentSchema);
        componentRecord.put(0, 1);
        componentRecord.put(1, "User");
        componentRecord.put(2, 100);
        componentRecord.put(3, true);
        componentRecord.put(4, "Main Street");
        componentRecord.put(5, "This is a record with six columns.");
    }

    private void checkEnforcerWithComponentRecordData(DiIncomingSchemaEnforcer enforcer) {
        // The enforcer must be ready to receive values.
        assertTrue(enforcer.areDynamicFieldsInitialized());
        
        // Create new record before passing values to enforcer
        enforcer.createNewRecord();

        // Put values into the enforcer and get them as an IndexedRecord.
        enforcer.put(0, 1);
        enforcer.put(1, "User");
        enforcer.put(2, 100);
        enforcer.put(3, true);
        enforcer.put(4, "Main Street");
        enforcer.put(5, "This is a record with six columns.");
        IndexedRecord adapted = enforcer.getCurrentRecord();

        // Ensure that the result is the same as the expected component record.
        assertThat(adapted, is(componentRecord));
        
        // Create new record before passing values to enforcer
        enforcer.createNewRecord();

        // Ensure that we create a new instance when we give it another value.
        enforcer.put("id", 2);
        enforcer.put("name", "User2");
        enforcer.put("age", 200);
        enforcer.put("valid", false);
        enforcer.put("address", "2 Main Street");
        enforcer.put("comment", "2 This is a record with six columns.");
        IndexedRecord adapted2 = enforcer.getCurrentRecord();

        // It should have the same schema, but not be the same instance.
        assertThat(adapted2.getSchema(), sameInstance(adapted.getSchema()));
        assertThat(adapted2, not(sameInstance(adapted)));
        assertThat(adapted2.get(0), is((Object) 2));
        assertThat(adapted2.get(1), is((Object) "User2"));
        assertThat(adapted2.get(2), is((Object) 200));
        assertThat(adapted2.get(3), is((Object) false));
        assertThat(adapted2.get(4), is((Object) "2 Main Street"));
        assertThat(adapted2.get(5), is((Object) "2 This is a record with six columns."));
    }

    /**
     * Checks following {@link DiIncomingSchemaEnforcer} workflow:
     * 1. Create instance of {@link DiIncomingSchemaEnforcer}
     * 2. Checks that dynamic fields don't require initialization (as design schema doesn't contain dynamic field)
     * 3. Checks that design schema equals to runtime schema
     * 4. Check DI data to IndexedRecord conversion for several data objects
     */
    @Test
    public void testNonDynamic() {
        // The design time schema should be the same as the runtime schema.
        Schema talend6Schema = componentRecord.getSchema();
        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(talend6Schema);
        
        // design schema has no dynamic fields, thus dynamic fields are considered as already initialized
        assertTrue(enforcer.areDynamicFieldsInitialized());

        // The enforcer is immediately usable
        assertThat(enforcer.getDesignSchema(), is(talend6Schema));
        assertThat(enforcer.getRuntimeSchema(), is(talend6Schema));
        checkEnforcerWithComponentRecordData(enforcer);
    }

    /**
     * Checks following {@link DiIncomingSchemaEnforcer} workflow:
     * 1. Create instance of {@link DiIncomingSchemaEnforcer}
     * 2. Check whether dynamic fields are initialized - should be false
     * 3. Initialize/add several dynamic fields
     * 4. Create runtime schema from dynamic fields and design schema
     * 5. Check whether dynamic fields are initialized - should be true
     * 6. Get runtime schema
     * 7. Check DI data to IndexedRecord conversion for several data objects
     * 
     * in case dynamic column is on the 0 position
     */
    @Test
    public void testDynamicColumnDynamicColumnAtStart() {
        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .fields() //
                .name("valid").type().booleanType().noDefault() //
                .name("address").type().stringType().noDefault() //
                .name("comment").type().stringType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        // The enforcer isn't usable yet.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertFalse(enforcer.areDynamicFieldsInitialized());
        assertThat(enforcer.getRuntimeSchema(), nullValue());

        enforcer.addDynamicField("id", "id_Integer", null, null, null, false);
        enforcer.addDynamicField("name", "id_String", null, null, null, false);
        enforcer.addDynamicField("age", "id_Integer", null, null, null, false);
        assertFalse(enforcer.areDynamicFieldsInitialized());
        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        // Check the run-time schema was created.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertThat(enforcer.getRuntimeSchema(), not(nullValue()));

        // Put values into the enforcer and get them as an IndexedRecord.
        checkEnforcerWithComponentRecordData(enforcer);
    }

    /**
     * Checks following {@link DiIncomingSchemaEnforcer} workflow:
     * 1. Create instance of {@link DiIncomingSchemaEnforcer}
     * 2. Check whether dynamic fields are initialized - should be false
     * 3. Initialize/add several dynamic fields
     * 4. Create runtime schema from dynamic fields and design schema
     * 5. Check whether dynamic fields are initialized - should be true
     * 6. Get runtime schema
     * 7. Check DI data to IndexedRecord conversion for several data objects
     * 
     * in case dynamic column is in the middle position
     */
    @Test
    public void testDynamicColumnDynamicColumnAtMiddle() {
        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "1") //
                .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .fields() //
                .name("id").type().intType().noDefault() //
                .name("address").type().stringType().noDefault() //
                .name("comment").type().stringType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        // The enforcer isn't usable yet.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertFalse(enforcer.areDynamicFieldsInitialized());
        assertThat(enforcer.getRuntimeSchema(), nullValue());

        enforcer.addDynamicField("name", "id_String", null, null, null, false);
        enforcer.addDynamicField("age", "id_Integer", null, null, null, false);
        enforcer.addDynamicField("valid", "id_Boolean", null, null, null, false);
        assertFalse(enforcer.areDynamicFieldsInitialized());
        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        // Check the run-time schema was created.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertThat(enforcer.getRuntimeSchema(), not(nullValue()));

        // Put values into the enforcer and get them as an IndexedRecord.
        checkEnforcerWithComponentRecordData(enforcer);
    }

    /**
     * Checks following {@link DiIncomingSchemaEnforcer} workflow:
     * 1. Create instance of {@link DiIncomingSchemaEnforcer}
     * 2. Check whether dynamic fields are initialized - should be false
     * 3. Initialize/add several dynamic fields
     * 4. Create runtime schema from dynamic fields and design schema
     * 5. Check whether dynamic fields are initialized - should be true
     * 6. Get runtime schema
     * 7. Check DI data to IndexedRecord conversion for several data objects
     * 
     * in case dynamic column is in the last position
     */
    @Test
    public void testDynamicColumnDynamicColumnAtEnd() {
        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "3") //
                .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        // The enforcer isn't usable yet.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertFalse(enforcer.areDynamicFieldsInitialized());
        assertThat(enforcer.getRuntimeSchema(), nullValue());

        enforcer.addDynamicField("valid", "id_Boolean", null, null, null, false);
        enforcer.addDynamicField("address", "id_String", null, null, null, false);
        enforcer.addDynamicField("comment", "id_String", null, null, null, false);
        assertFalse(enforcer.areDynamicFieldsInitialized());
        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        // Check the run-time schema was created.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertThat(enforcer.getRuntimeSchema(), not(nullValue()));

        // Put values into the enforcer and get them as an IndexedRecord.
        checkEnforcerWithComponentRecordData(enforcer);
    }

    /**
     * TODO to be removed
     * This is wrong test-case indexed record should not store values of type {@link Date}
     */
    @Ignore("Incorrect behavior. Keep this test here until behavior won't be fixed in components")
    @Test
    public void testTypeConversion_toDate() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields()
                //
                .name("field")
                //
                // properties
                .prop(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date")
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'")
                // type
                .type().longType().noDefault() //
                // Add java-class to longType? Add union to output?
                .endRecord();

        talend6Schema = AvroUtils.setProperty(talend6Schema, DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "3");

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(talend6Schema);

        // No dynamic columns, the schema is available.
        assertThat(enforcer.getDesignSchema(), is(talend6Schema));
        assertTrue(enforcer.areDynamicFieldsInitialized());
        assertThat(enforcer.getRuntimeSchema(), is(talend6Schema));

        // Put values into the enforcer and get them as an IndexedRecord.
        enforcer.createNewRecord();
        enforcer.put(0, new Date(1234567891011L));
        assertThat(enforcer.getCurrentRecord().get(0), is((Object) new Date(1234567891011L)));

        // 2016-05-02T17:30:38.000Z
        enforcer.createNewRecord();
        enforcer.put(0, "2009-02-13T23:31:31.000Z");
        // "yyyy-MM-dd'T'HH:mm:ss'000Z'"
        IndexedRecord adapted = enforcer.getCurrentRecord();
        assertThat(adapted.getSchema(), sameInstance(enforcer.getRuntimeSchema()));
        assertThat(adapted.get(0), is((Object) new Date(1234567891000L)));
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#put()} converts DI Date type to Avro logical data correctly
     */
    @Test
    public void testTypeConversion_toLogicalDate() {
        // The expected schema after enforcement.
        Schema designSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("field").type(AvroUtils._logicalDate()).noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        // No dynamic columns, the schema is available.
        assertThat(enforcer.getDesignSchema(), is(designSchema));
        assertTrue(enforcer.areDynamicFieldsInitialized());
        assertThat(enforcer.getRuntimeSchema(), is(designSchema));

        // Put values into the enforcer and get them as an IndexedRecord.
        enforcer.createNewRecord();
        enforcer.put(0, new Date(1234567891011L));
        IndexedRecord adapted = enforcer.getCurrentRecord();
        assertThat(adapted.get(0), is((Object) 14288));
        assertThat(adapted.getSchema(), sameInstance(enforcer.getRuntimeSchema()));
    }

    /**
     * Checks whether enforcer can recreate dynamic fields of all AVRO types including logical types
     * Note, this test duplicates {@link this#testDynamicColumnALLSupportedType()} but uses new API
     * Old test will be removed after old API removal
     */
    @Test
    public void testAddDynamicFieldAllTypes() {
        Schema expectedRuntimeSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("Test_String").type().stringType().noDefault() //
                .name("Test_Boolean").type().booleanType().noDefault() //
                .name("Test_Integer").type().intType().noDefault() //
                .name("Test_Long").type().longType().noDefault() //
                .name("Test_Double").type().doubleType().noDefault() //
                .name("Test_Float").type().floatType().noDefault() //
                .name("Test_BigDecimal").type(AvroUtils._decimal()).noDefault() //
                .name("Test_Date").prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'")
                .type(AvroUtils._date()).noDefault() //
                .name("Test_Byte").type(AvroUtils._byte()).noDefault() //
                .name("Test_Short").type(AvroUtils._short()).noDefault() //
                .name("Test_Character").type(AvroUtils._character()).noDefault() //
                .name("TestLogicalDate").type(AvroUtils._logicalDate()).noDefault() //
                .name("TestLogicalTimeMillis").type(AvroUtils._logicalTime()).noDefault() //
                .name("TestLogicalTimestampMillis").type(AvroUtils._logicalTimestamp()).noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord(); //

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'") //
                .fields() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        enforcer.addDynamicField("Test_String", "id_String", null, null, null, false);
        enforcer.addDynamicField("Test_Boolean", "id_Boolean", null, null, null, false);
        enforcer.addDynamicField("Test_Integer", "id_Integer", null, null, null, false);
        enforcer.addDynamicField("Test_Long", "id_Long", null, null, null, false);
        enforcer.addDynamicField("Test_Double", "id_Double", null, null, null, false);
        enforcer.addDynamicField("Test_Float", "id_Float", null, null, null, false);
        enforcer.addDynamicField("Test_BigDecimal", "id_BigDecimal", null, null, null, false);
        enforcer.addDynamicField("Test_Date", "id_Date", null, "yyyy-MM-dd'T'HH:mm:ss'000Z'", null, false);
        enforcer.addDynamicField("Test_Byte", "id_Byte", null, null, null, false);
        enforcer.addDynamicField("Test_Short", "id_Short", null, null, null, false);
        enforcer.addDynamicField("Test_Character", "id_Character", null, null, null, false);
        enforcer.addDynamicField("TestLogicalDate", "id_Date", "date", null, null, false);
        enforcer.addDynamicField("TestLogicalTimeMillis", "id_Integer", "time-millis", null, null, false);
        enforcer.addDynamicField("TestLogicalTimestampMillis", "id_Date", "timestamp-millis", null, null, false);

        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        Schema actualRuntimeSchema = enforcer.getRuntimeSchema();
        assertEquals(expectedRuntimeSchema, actualRuntimeSchema);

        enforcer.createNewRecord();
        enforcer.put(0, "string value");
        enforcer.put(1, true);
        enforcer.put(2, 100);
        enforcer.put(3, 1234567891011L);
        enforcer.put(4, 2.15);
        enforcer.put(5, 3.6f);
        enforcer.put(6, new BigDecimal("630.1020"));
        enforcer.put(7, new Date(1234567891011L));
        enforcer.put(8, (byte) 20);
        enforcer.put(9, (short) 2016);
        enforcer.put(10, 'A');
        // 46 * 365 days in milliseconds
        enforcer.put(11, new Date(1450656000000l));
        // 14 hours in milliseconds
        enforcer.put(12, 50400000);
        // 46 * 365 days + 14 hours
        enforcer.put(13, new Date(1450706400000l));
        enforcer.put(14, false);

        IndexedRecord record = enforcer.getCurrentRecord();

        assertThat(record.get(0), is((Object) "string value"));
        assertThat(record.get(1), is((Object) true));
        assertThat(record.get(2), is((Object) 100));
        assertThat(record.get(3), is((Object) 1234567891011L));
        assertThat(record.get(4), is((Object) 2.15));
        assertThat(record.get(5), is((Object) 3.6f));
        assertThat(record.get(6), is((Object) "630.1020"));
        assertThat(record.get(7), is((Object) 1234567891011L));
        assertThat(record.get(8), is((Object) 20));
        assertThat(record.get(9), is((Object) 2016));
        assertThat(record.get(10), is((Object) "A"));
        // should be integer value equals to 46 * 365 days in days
        assertThat(record.get(11), is((Object) 16790));
        // should be integer value equals to 14 hours in milliseconds
        assertThat(record.get(12), is((Object) 50400000));
        // should be long value equals to 1450706400000
        assertThat(record.get(13), is((Object) 1450706400000l));
        assertThat(record.get(14), is((Object) false));
    }

    /**
     * Checks whether enforcer can recreate dynamic fields when nullable is true
     */
    @Test
    public void testAddDynamicFieldNullable() {
        Schema expectedRuntimeSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("Test_BigDecimal").type(AvroUtils.wrapAsNullable(AvroUtils._decimal())).noDefault() //
                .name("Test_Date").prop(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'")
                .type(AvroUtils.wrapAsNullable(AvroUtils._date())).noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord(); //

        Schema designSchema = SchemaBuilder.builder().record("Record").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'") //
                .fields() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);

        enforcer.addDynamicField("Test_BigDecimal", "id_BigDecimal", null, null, null, true);
        enforcer.addDynamicField("Test_Date", "id_Date", null, "yyyy-MM-dd'T'HH:mm:ss'000Z'", null, true);

        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        Schema actualRuntimeSchema = enforcer.getRuntimeSchema();
        assertEquals(expectedRuntimeSchema, actualRuntimeSchema);

        enforcer.createNewRecord();
        enforcer.put(0, new BigDecimal("630.1020"));
        enforcer.put(1, new Date(1234567891011L));

        IndexedRecord record = enforcer.getCurrentRecord();
        assertThat(record.get(0), is((Object) "630.1020"));
        assertThat(record.get(1), is((Object) 1234567891011L));
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#put()} converts string value to date according pattern specified in dynamic field
     * TODO (iv.gonchar): this is incorrect behavior, because avro record should not contain java.util.Date value. It should store
     * such value as Long
     */
    @Ignore("Incorrect behavior. Keep this test here until behavior won't be fixed in components")
    @Test
    public void testPutDatePattern() {
        Schema designSchema = SchemaBuilder.builder().record("Record").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'") //
                .fields() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        enforcer.addDynamicField("Test_Date", "id_Date", null, "yyyy-MM-dd'T'HH:mm:ss'000Z'", null, true);
        enforcer.createRuntimeSchema();
        assertTrue(enforcer.areDynamicFieldsInitialized());

        enforcer.createNewRecord();
        enforcer.put(0, "2009-02-13T23:31:31.000Z");
        IndexedRecord record = enforcer.getCurrentRecord();
        assertThat(record.get(0), is((Object) new Date(1234567891000L)));
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#addDynamicField()} throws {@link UnsupportedOperationException} in case of
     * unsupported di type is passed
     */
    @Test
    public void testAddDynamicFieldUnsupportedType() {
        thrown.expect(UnsupportedOperationException.class);

        Schema designSchema = SchemaBuilder.builder().record("Record").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'") //
                .fields() //
                .name("valid").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        enforcer.addDynamicField("Test_Unsupported", "id_Unsupported", null, null, null, false);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#getDesignSchema()} returns design schema without any changes.
     * DiIncomingSchemaEnforcer should not change design schema at all
     */
    @Test
    public void testGetDesignSchema() {
        Schema designSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("intField").type().intType().noDefault() //
                .name("stringField").type().stringType().noDefault() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualDesignSchema = enforcer.getDesignSchema();
        assertEquals(designSchema, actualDesignSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#getRuntimeSchema()} returns schema which equals to original design schema in case
     * design schema doesn't
     * contain dynamic field
     */
    @Test
    public void testGetRuntimeSchemaNotDynamic() {
        Schema designSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("intField").type().intType().noDefault() //
                .name("stringField").type().stringType().noDefault() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema runtimeSchema = enforcer.getRuntimeSchema();
        assertEquals(designSchema, runtimeSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#getRuntimeSchema()} returns null in case design schema doesn't contain dynamic field
     * and dynamic fields
     * were not initialized yet
     */
    @Test
    public void testGetRuntimeSchemaDynamic() {
        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0") //
                .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true") //
                .fields() //
                .name("intField").type().intType().noDefault() //
                .name("stringField").type().stringType().noDefault() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema runtimeSchema = enforcer.getRuntimeSchema();
        assertNull(runtimeSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns String avro schema in case "id_String" di type is
     * passed
     */
    @Test
    public void testDiToAvroString() {
        Schema expectedSchema = AvroUtils._string();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_String", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Boolean avro schema in case "id_Boolean" di type
     * is passed
     */
    @Test
    public void testDiToAvroBoolean() {
        Schema expectedSchema = AvroUtils._boolean();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Boolean", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Integer avro schema in case "id_Integer" di type
     * is passed
     */
    @Test
    public void testDiToAvroInteger() {
        Schema expectedSchema = AvroUtils._int();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Integer", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Long avro schema in case "id_Long" di type is
     * passed
     */
    @Test
    public void testDiToAvroLong() {
        Schema expectedSchema = AvroUtils._long();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Long", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Double avro schema in case "id_Double" di type is
     * passed
     */
    @Test
    public void testDiToAvroDouble() {
        Schema expectedSchema = AvroUtils._double();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Double", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Float avro schema in case "id_Float" di type is
     * passed
     */
    @Test
    public void testDiToAvroFloat() {
        Schema expectedSchema = AvroUtils._float();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Float", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Integer avro schema with
     * "java-class"=java.lang.Byte
     * in case "id_Byte" di type is passed
     */
    @Test
    public void testDiToAvroByte() {
        Schema expectedSchema = AvroUtils._byte();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Byte", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Integer avro schema with
     * "java-class"=java.lang.Short
     * in case "id_Short" di type is passed
     */
    @Test
    public void testDiToAvroShort() {
        Schema expectedSchema = AvroUtils._short();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Short", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns String avro schema with
     * "java-class"=java.lang.Character
     * in case "id_Character" di type is passed
     */
    @Test
    public void testDiToAvroCharacter() {
        Schema expectedSchema = AvroUtils._character();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Character", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns String avro schema with
     * "java-class"=java.math.BigDecimal
     * in case "id_BigDecimal" di type is passed
     */
    @Test
    public void testDiToAvroBigDecimal() {
        Schema expectedSchema = AvroUtils._decimal();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_BigDecimal", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns Long avro schema with "java-class"=java.util.Date
     * in case "id_Date" di type is passed
     */
    @Test
    public void testDiToAvroDate() {
        Schema expectedSchema = AvroUtils._date();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro("id_Date", null);

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} throws {@link UnsupportedOperationException}
     * in case unsupported type is passed
     */
    @Test
    public void testDiToAvroNotSupporter() {
        thrown.expect(UnsupportedOperationException.class);

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        enforcer.diToAvro("Unsupported", null);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns logical date avro schema with in case "date"
     * logical type is passed
     */
    @Test
    public void testDiToAvroLogicalDate() {
        Schema expectedSchema = AvroUtils._logicalDate();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro(null, "date");

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns logical time-millis avro schema with in case
     * "time-millis" logical type is passed
     */
    @Test
    public void testDiToAvroLogicalTime() {
        Schema expectedSchema = AvroUtils._logicalTime();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro(null, "time-millis");

        assertEquals(expectedSchema, actualSchema);
    }

    /**
     * Checks {@link DiIncomingSchemaEnforcer#diToAvro(String, String)} returns logical timestamp-millis avro schema with in case
     * "timestamp-millis" logical type is passed
     */
    @Test
    public void testDiToAvroLogicalTimestamp() {
        Schema expectedSchema = AvroUtils._logicalTimestamp();

        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("booleanField").type().booleanType().noDefault() //
                .endRecord();

        DiIncomingSchemaEnforcer enforcer = new DiIncomingSchemaEnforcer(designSchema);
        Schema actualSchema = enforcer.diToAvro(null, "timestamp-millis");

        assertEquals(expectedSchema, actualSchema);
    }
}
