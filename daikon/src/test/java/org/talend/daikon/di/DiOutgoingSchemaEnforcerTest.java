package org.talend.daikon.di;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Unit-tests for {@link DiOutgoingSchemaEnforcer} class
 */
@SuppressWarnings("nls")
public class DiOutgoingSchemaEnforcerTest {

    /**
     * Runtime {@link Schema} instance, which is used as argument in tests
     */
    private static Schema runtimeSchema;

    /**
     * Design {@link Schema} instance, which is used as argument in tests
     */
    private static Schema talend6Schema;

    /**
     * An actual record that a component would like to be emitted, which may or may not contain enriched schema
     * information.
     */
    private static IndexedRecord record;

    private static int NUM_DAYS = 1000;

    /**
     * 1000 days after 1970-01-01, equal to 1972-09-27.
     */
    private static Date DATE_COMPARE = new Date(NUM_DAYS * 60 * 60 * 24 * 1000L);

    /**
     * Creates runtime schema, design schema and record, which is used as test arguments
     */
    @BeforeClass
    public static void setup() throws Exception {
        runtimeSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .name("createdDate").prop(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'").type().nullable().longType() //
                .noDefault() //
                .name("logicalDate").type(AvroUtils._logicalDate()).noDefault() //
                .name("logicalTime").type(AvroUtils._logicalTime()).noDefault() //
                .name("logicalTimestamp").type(AvroUtils._logicalTimestamp()).noDefault() //
                .endRecord(); //

        talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .name("createdDate").prop(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'").type().nullable().longType()
                .noDefault() //
                .name("logicalDate").type(AvroUtils._logicalDate()).noDefault() //
                .name("logicalTime").type(AvroUtils._logicalTime()).noDefault() //
                .name("logicalTimestamp").type(AvroUtils._logicalTimestamp()).noDefault() //
                .endRecord(); //

        record = new GenericData.Record(runtimeSchema);
        record.put(0, 1);
        record.put(1, "User");
        record.put(2, 100);
        record.put(3, true);
        record.put(4, new Date(1467170137872L));
        record.put(5, NUM_DAYS);
        record.put(6, 1000);
        record.put(7, 1467170137872L);
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#get(int)} returns correct values retrieved from wrapped
     * {@link IndexedRecord} in case design and runtime schema have same order of the fields
     */
    @Test
    public void testGetByIndex() {
        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);
        enforcer.setWrapped(record);

        assertThat(enforcer.get(0), equalTo((Object) 1));
        assertThat(enforcer.get(1), equalTo((Object) "User"));
        assertThat(enforcer.get(2), equalTo((Object) 100));
        assertThat(enforcer.get(3), equalTo((Object) true));
        assertThat(enforcer.get(4), equalTo((Object) new Date(1467170137872L)));
        assertThat(enforcer.get(5), equalTo((Object) DATE_COMPARE));
        assertThat(enforcer.get(6), equalTo((Object) 1000));
        assertThat(enforcer.get(7), equalTo((Object) new Date(1467170137872L)));
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#get(int)} returns correct values retrieved from wrapped
     * {@link IndexedRecord} in case design and runtime schema have different order of the fields
     */
    @Test
    public void testGetByName() {
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("valid").type().booleanType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("id").type().intType().noDefault() //
                .name("createdDate").prop(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'").type().nullable().longType() //
                .noDefault() //
                .name("age").type().intType().noDefault() //
                .endRecord(); //

        IndexMapper indexMapper = new IndexMapperByName(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);
        enforcer.setWrapped(record);

        assertThat(enforcer.get(0), equalTo((Object) true));
        assertThat(enforcer.get(1), equalTo((Object) "User"));
        assertThat(enforcer.get(2), equalTo((Object) 1));
        assertThat(enforcer.get(3), equalTo((Object) new Date(1467170137872L)));
        assertThat(enforcer.get(4), equalTo((Object) 100));
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#get(int)} throws {@link IndexOutOfBoundsException} in case of incoming
     * index less than 0 or more than (designSchemaSize - 1)
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);
        enforcer.setWrapped(record);

        enforcer.get(10);
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#transformValue(Object, Field)} transforms {@link Date} value correctly
     * using Talend type property
     */
    @Test
    public void testTransformValueToDateByTalendType() {
        Date expectedDate = new Date(1L);

        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);

        Field dateField = new Field("createdDate", Schema.create(Schema.Type.LONG), null, null);
        dateField.addProp(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date");
        dateField.addProp(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'");

        Object transformedValue = enforcer.transformValue(1L, dateField);

        assertThat(transformedValue, equalTo((Object) expectedDate));
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#transformValue(Object, Field)} transforms {@link Date} value correctly
     * using Java class
     */
    @Test
    public void testTransformValueToDateByJavaClass() {
        Date expectedDate = new Date(1L);

        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);

        Field dateField = new Field("createdDate", Schema.create(Schema.Type.LONG), null, null);
        dateField.schema().addProp(SchemaConstants.JAVA_CLASS_FLAG, "java.util.Date");

        Object transformedValue = enforcer.transformValue(1L, dateField);

        assertThat(transformedValue, equalTo((Object) expectedDate));
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#transformValue(Object, Field)} transforms {@link BigDecimal} value
     * correctly using Java class
     */
    @Test
    public void testTransformValueToDecimal() {
        BigDecimal expectedDecimal = new BigDecimal("10.20");

        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);

        Field decimalField = new Field("decimal", AvroUtils._decimal(), null, null);

        Object transformedValue = enforcer.transformValue("10.20", decimalField);

        assertThat(transformedValue, equalTo((Object) expectedDecimal));
    }

    /**
     * Checks {@link DiOutgoingSchemaEnforcer#transformValue(Object, Field)} transforms {@link Character} value
     * correctly using Java class
     */
    @Test
    public void testTransformValueToCharacter() {
        char expectedChar = 'A';

        IndexMapper indexMapper = new IndexMapperByIndex(talend6Schema);
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(talend6Schema, indexMapper);

        Field characterField = new Field("character", AvroUtils._character(), null, null);

        Object transformedValue = enforcer.transformValue("A", characterField);

        assertThat(transformedValue, equalTo((Object) expectedChar));
    }

    /**
     * Checks case when designSchema and runtimeSchema are different
     */
    @Test
    public void testSetWrapped() throws Exception {
        int[] expectedIndexMap = { 0 };

        Schema designSchema = SchemaBuilder.builder().record("Record").fields().name("col0").type(AvroUtils._date()).noDefault()
                .endRecord();

        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields().name("field0").type().longType().noDefault()
                .endRecord();

        IndexedRecord indexedRecord = new GenericData.Record(runtimeSchema);
        indexedRecord.put(0, 1467170137872L);

        DiOutgoingSchemaEnforcer enforcer = EnforcerCreator.createOutgoingEnforcer(designSchema, false);
        enforcer.setWrapped(indexedRecord);
        int[] actualIndexMap = enforcer.indexMap;
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }
    
    /**
     * Checks {@link DiOutgoingSchemaEnforcer#get(int)} converts all types from Avro to DI correctly
     */
    @Test
    public void testGetAllTypes() {
        Schema designSchema = SchemaBuilder.builder().record("Record") //
                .fields() //
                .name("Boolean").type().booleanType().noDefault() //
                .name("Byte").type(AvroUtils._byte()).noDefault() //
                .name("Short").type(AvroUtils._short()).noDefault() //
                .name("Integer").type().intType().noDefault() //
                .name("LogicalDate").type(AvroUtils._logicalDate()).noDefault() //
                .name("LogicalTimeMillis").type(AvroUtils._logicalTime()).noDefault() //
                .name("Long").type().longType().noDefault() //
                .name("Date").type(AvroUtils._date()).noDefault() //
                .name("LogicalTimestampMillis").type(AvroUtils._logicalTimestamp()).noDefault() //
                .name("Float").type().floatType().noDefault() //
                .name("Double").type().doubleType().noDefault() //
                .name("Bytes").type().bytesType().noDefault() //
                .name("Decimal").type(AvroUtils._decimal()).noDefault() //
                .name("Character").type(AvroUtils._character()).noDefault() //
                .name("String").type().stringType().noDefault() //
                .name("Array").type().array().items().stringType().noDefault() //
                .endRecord(); //
        
        /*
         * Runtime schema may differ from design schema. It may not contain some DI schema properties.
         * But it doesn't matter for test, so we may take runtime schema equals to design schema
         */
        Schema runtimeSchema = designSchema;
        
        IndexedRecord record = new GenericData.Record(runtimeSchema);
        // boolean
        record.put(0, true);
        // byte
        record.put(1, 123);
        // short
        record.put(2, 12345);
        // integer
        record.put(3, 123456789);
        // logical date
        record.put(4, 1234);
        // logical time-millis
        record.put(5, 12345);
        // long
        record.put(6, 123456789012l);
        // deprecated DI date
        record.put(7, 123456789012l);
        // logical timestamp-millis
        record.put(8, 123456789012l);
        // float
        record.put(9, 12.34f);
        // double
        record.put(10, 1234.5678);
        // bytes
        byte[] bytes = new byte[] { 0, 1, 2, 3 };
        record.put(11, bytes);
        // DI Decimal
        record.put(12, "1234.5678");
        // DI Character
        record.put(13, "s");
        // String
        record.put(14, "str");
        // Array
        record.put(15, Arrays.asList("one", "two", "three"));
        
        DiOutgoingSchemaEnforcer enforcer = new DiOutgoingSchemaEnforcer(designSchema, new IndexMapperByIndex(designSchema));
        enforcer.setWrapped(record);
        
        assertEquals(true, enforcer.get(0));
        assertEquals((byte) 123, enforcer.get(1));
        assertEquals((short) 12345, enforcer.get(2));
        assertEquals(123456789, enforcer.get(3));
        // 106617600000 = 1234 days in milliseconds
        assertEquals(new Date(106617600000l), enforcer.get(4));
        assertEquals(12345, enforcer.get(5));
        assertEquals(123456789012l, enforcer.get(6));
        assertEquals(new Date(123456789012l), enforcer.get(7));
        assertEquals(new Date(123456789012l), enforcer.get(8));
        assertEquals(12.34f, enforcer.get(9));
        assertEquals(1234.5678, enforcer.get(10));
        assertEquals(bytes, enforcer.get(11));
        assertEquals(new BigDecimal("1234.5678"), enforcer.get(12));
        assertEquals('s', enforcer.get(13));
        assertEquals("str", enforcer.get(14));
        assertEquals(Arrays.asList("one", "two", "three"), enforcer.get(15));
    }
}
