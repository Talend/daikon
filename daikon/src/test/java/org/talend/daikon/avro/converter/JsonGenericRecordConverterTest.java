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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import org.talend.daikon.avro.inferrer.JsonSchemaInferrer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test {@link JsonGenericRecordConverter}
 */
public class JsonGenericRecordConverterTest {

    private final JsonSchemaInferrer jsonSchemaInferrer = JsonSchemaInferrer.createJsonSchemaInferrer();

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    private JsonGenericRecordConverter jsonGenericRecordConverter;

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#simpleJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroSimpleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleJson);
        Schema schema = jsonSchemaInferrer.inferSchema(simpleJson);
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(simpleJson);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertEquals("b1", recordA.get("b"));

        // Check `d` field value
        assertEquals("d1", outputRecord.get(1));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#arrayJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroArrayJson() throws IOException {
        Schema schema = jsonSchemaInferrer.inferSchema(arrayJson);
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(arrayJson);

        // Get `a` array field
        ArrayList<GenericRecord> arrayRecordA = (ArrayList<GenericRecord>) outputRecord.get(0);

        // Check that `a` array field contains two records
        assertEquals(2, arrayRecordA.size());

        // Check `b` field values
        GenericRecord recordB1 = arrayRecordA.get(0);
        GenericRecord recordB2 = arrayRecordA.get(1);
        assertEquals("b1", recordB1.get("b"));
        assertEquals("b2", recordB2.get("b"));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#nullJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroNullJson() throws IOException {
        Schema schema = jsonSchemaInferrer.inferSchema(nullJson);
        jsonGenericRecordConverter = new JsonGenericRecordConverter(schema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(nullJson);

        // Check that `a` field is null
        assertNull(outputRecord.get("a"));
    }
}
