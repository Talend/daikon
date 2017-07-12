package org.talend.daikon.avro.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test {@link ConvertJsonSchema}
 */
public class ConvertJsonSchemaTest {

    private final ConvertJson convertJson = new ConvertJson(new ObjectMapper());

    private final ConvertJsonSchema convertJsonSchema = new ConvertJsonSchema(new ObjectMapper());

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    /**
     * Test {@link ConvertJsonSchema#getOutputRecord(JsonNode, Schema)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link ConvertJsonSchemaTest#simpleJson}
     *
     * @throws Exception
     */
    @Test
    public void testGetOutputRecordSimpleJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleJson);
        Schema schema = convertJson.convertToAvro(simpleJson);

        // Get Avro Generic Record
        GenericRecord outputRecord = convertJsonSchema.getOutputRecord(jsonNode, schema);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertEquals("b1", recordA.get("b"));

        // Check `d` field value
        assertEquals("d1", outputRecord.get(1));
    }

    /**
     * Test {@link ConvertJsonSchema#getOutputRecord(JsonNode, Schema)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link ConvertJsonSchemaTest#arrayJson}
     *
     * @throws Exception
     */
    @Test
    public void testGetOutputRecordArrayJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(arrayJson);
        Schema schema = convertJson.convertToAvro(arrayJson);

        // Get Avro Generic Record
        GenericRecord outputRecord = convertJsonSchema.getOutputRecord(jsonNode, schema);

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
     * Test {@link ConvertJsonSchema#getOutputRecord(JsonNode, Schema)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link ConvertJsonSchemaTest#nullJson}
     *
     * @throws Exception
     */
    @Test
    public void testGetOutputRecordNullJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(nullJson);
        Schema schema = convertJson.convertToAvro(nullJson);

        // Get Avro Generic Record
        GenericRecord outputRecord = convertJsonSchema.getOutputRecord(jsonNode, schema);

        // Check that `a` field is null
        assertNull(outputRecord.get("a"));
    }
}
