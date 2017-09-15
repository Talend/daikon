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
package org.talend.daikon.avro.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import org.talend.daikon.avro.AvroUtils;

/**
 * Test {@link JsonGenericRecordConverter}
 */
public class JsonGenericRecordConverterTest {

    private final Schema schemaStrB = SchemaBuilder.record("b").fields().name("b").type().optional().stringType().endRecord();

    private final Schema schemaIntB = SchemaBuilder.record("b").fields().name("b").type().optional().intType().endRecord();

    private final Schema schemaBoolB = SchemaBuilder.record("b").fields().name("b").type().optional().booleanType().endRecord();

    private final Schema schemaComplexRecordWithStrFields = SchemaBuilder.record("complexRecordWithStrFields").fields().name("a")
            .type(schemaStrB).noDefault().name("d").type().optional().stringType().endRecord();

    private final Schema schemaArrayOfComplexStringRecords = SchemaBuilder.record("arrayOfComplexStringRecords").fields()
            .name("a").type(SchemaBuilder.array().items(schemaStrB)).noDefault().endRecord();

    private final Schema schemaComplexRecordWithIntegerFields = SchemaBuilder.record("complexRecordWithIntegerFields").fields()
            .name("a").type(schemaIntB).noDefault().name("d").type().optional().intType().endRecord();

    private final Schema schemaComplexRecordWithBooleanFields = SchemaBuilder.record("complexRecordWithBooleanFields").fields()
            .name("a").type(schemaBoolB).noDefault().name("d").type().optional().booleanType().endRecord();

    private final Schema schemaArrayOfComplexBooleanRecords = SchemaBuilder.record("arrayOfComplexBooleanRecords").fields()
            .name("a").type(SchemaBuilder.array().items(schemaBoolB)).noDefault().endRecord();

    private final Schema schemaArrayOfInteger = SchemaBuilder.record("arrayOfInteger").fields().name("a")
            .type(SchemaBuilder.array().items(AvroUtils.wrapAsNullable(AvroUtils._int()))).noDefault().endRecord();

    private final Schema schemaArrayOfString = SchemaBuilder.record("arrayOfString").fields().name("a")
            .type(SchemaBuilder.array().items(AvroUtils.wrapAsNullable(AvroUtils._string()))).noDefault().endRecord();

    private final String jsonComplexRecordWithStrFields = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String jsonArrayOfComplexStringRecords = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String jsonComplexRecordWithIntegerFields = "{\"a\": {\"b\": 10}, \"d\": 11}";

    private final String jsonComplexRecordWithBooleanFields = "{\"a\": {\"b\": false}, \"d\": true}";

    private final String jsonArrayOfComplexBooleanRecords = "{\"a\": [{\"b\": true}, {\"b\": false}]}";

    private final String jsonArrayOfInteger = "{\"a\": [10, 11]}";

    private final String jsonArrayOfString = "{\"a\": [\"a1\", \"a2\"]}";

    private final String jsonArrayOfNull = "{\"a\": [null]}";

    private JsonGenericRecordConverter jsonGenericRecordConverter;

    /**
     * Test {@link JsonGenericRecordConverter#getSchema()}
     */
    @Test
    public void testGetSchema() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaComplexRecordWithStrFields);
        assertThat(schemaComplexRecordWithStrFields, is(equalTo(jsonGenericRecordConverter.getSchema())));
    }

    /**
     * Test {@link JsonGenericRecordConverter#getDatumClass()}
     */
    @Test
    public void testGetDatumClass() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter();
        assertThat(jsonGenericRecordConverter.getDatumClass(), is(equalTo(String.class)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToDatum(GenericRecord)}
     */
    @Test
    public void testConvertToDatum() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaComplexRecordWithStrFields);
        GenericRecord record = jsonGenericRecordConverter.convertToAvro(jsonComplexRecordWithStrFields);
        String jsonConverted = jsonGenericRecordConverter.convertToDatum(record);
        assertThat(jsonComplexRecordWithStrFields, is(equalTo(jsonConverted)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#jsonComplexRecordWithStrFields}
     *
     * @throws Exception
     */
    @Test
    public void testConvertComplexRecordWithStrFieldsToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaComplexRecordWithStrFields);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonComplexRecordWithStrFields);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertThat((String) recordA.get("b"), is(equalTo("b1")));

        // Check `d` field value
        assertThat((String) outputRecord.get(1), is(equalTo("d1")));
    }

    @Test
    public void testConvertArrayOfIntegerToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaArrayOfInteger);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonArrayOfInteger);

        // Get `a` field
        ArrayList<Integer> arrayRecordA = (ArrayList<Integer>) outputRecord.get("a");

        assertThat(arrayRecordA.get(0), is(equalTo(10)));
        assertThat(arrayRecordA.get(1), is(equalTo(11)));
    }

    @Test
    public void testConvertArrayOfStringToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaArrayOfString);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonArrayOfString);

        // Get `a` field
        ArrayList<String> arrayRecordA = (ArrayList<String>) outputRecord.get("a");

        assertThat(arrayRecordA.get(0), is(equalTo("a1")));
        assertThat(arrayRecordA.get(1), is(equalTo("a2")));
    }

    @Test
    public void testConvertArrayOfNullToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaArrayOfString);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonArrayOfNull);

        // Get `a` field
        ArrayList<Object> arrayRecordA = (ArrayList<Object>) outputRecord.get("a");

        assertThat(arrayRecordA.get(0), is(equalTo(null)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#jsonArrayOfComplexStringRecords}
     *
     * @throws Exception
     */
    @Test
    public void testConvertArrayOfComplexStringRecordsToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaArrayOfComplexStringRecords);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonArrayOfComplexStringRecords);

        // Get `a` array field
        ArrayList<GenericRecord> arrayRecordA = (ArrayList<GenericRecord>) outputRecord.get(0);

        // Check that `a` array field contains two records
        assertThat(arrayRecordA, hasSize(2));

        // Check `b` field values
        GenericRecord recordB1 = arrayRecordA.get(0);
        GenericRecord recordB2 = arrayRecordA.get(1);
        assertThat((String) recordB1.get("b"), is(equalTo("b1")));
        assertThat((String) recordB2.get("b"), is(equalTo("b2")));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#jsonComplexRecordWithIntegerFields}
     *
     * @throws Exception
     */
    @Test
    public void testConvertComplexRecordWithIntegerFieldsToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaComplexRecordWithIntegerFields);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonComplexRecordWithIntegerFields);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertThat((int) recordA.get("b"), is(equalTo(10)));

        // Check `d` field value
        assertThat((int) outputRecord.get(1), is(equalTo(11)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#jsonComplexRecordWithBooleanFields}
     *
     * @throws Exception
     */
    @Test
    public void testConvertComplexRecordWithBooleanFieldsToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaComplexRecordWithBooleanFields);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonComplexRecordWithBooleanFields);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get("a");

        // Check `b` field value
        assertThat((boolean) recordA.get("b"), is(equalTo(false)));

        // Check `d` field value
        assertThat((boolean) outputRecord.get("d"), is(equalTo(true)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#jsonArrayOfComplexBooleanRecords}
     *
     * @throws Exception
     */
    @Test
    public void testConvertArrayOfComplexBooleanRecordsToAvro() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schemaArrayOfComplexBooleanRecords);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(jsonArrayOfComplexBooleanRecords);

        // Get `a` array field
        ArrayList<GenericRecord> arrayRecordA = (ArrayList<GenericRecord>) outputRecord.get("a");

        // Check that `a` array field contains two records
        assertThat(arrayRecordA, hasSize(2));

        // Check `b` field values
        GenericRecord recordB1 = arrayRecordA.get(0);
        GenericRecord recordB2 = arrayRecordA.get(1);
        assertThat((boolean) recordB1.get("b"), is(equalTo(true)));
        assertThat((boolean) recordB2.get("b"), is(equalTo(false)));
    }
}
