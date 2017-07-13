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
package org.talend.daikon.avro.inferrer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test {@link JsonSchemaInferrer}
 */
public class JsonSchemaInferrerTest {

    private final JsonSchemaInferrer jsonSchemaInferrer = JsonSchemaInferrer.createJsonSchemaInferrer();

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    private final String intJson = "{\"a\": 100}";

    private final String doubleJson = "{\"a\": 100.1}";

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#simpleJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},{"name":"d","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsSimpleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleJson);

        List<Schema.Field> fields = jsonSchemaInferrer.getFields(jsonNode);
        assertEquals(2, fields.size());

        // Check `a` field
        Schema.Field fieldA = fields.get(0);

        // "name":"a"
        assertEquals("a", fieldA.name());

        // Check `a` field type content
        // "type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}

        // "type":"record"
        assertEquals("record", fieldA.schema().getType().getName());

        // "fields":[{"name":"b","type":["null","string"]}]
        List<Schema.Field> fieldATypeFields = fieldA.schema().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldATypeFields.get(0);

        // "name":"b"
        assertEquals("b", fieldB.name());

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertEquals("union", fieldBType.getName());
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertEquals("string", fieldSchemaBTypeItems.get(0).getName());
        assertEquals("null", fieldSchemaBTypeItems.get(1).getName());

        // Check `d` field
        Schema.Field fieldD = fields.get(1);

        // "name":"d"
        assertEquals("d", fieldD.name());

        // Check `d` field type content
        // "type":["string","null"]
        Schema.Type fieldDType = fieldD.schema().getType();
        assertEquals("union", fieldDType.getName());
        List<Schema> fieldSchemaDTypeItems = fieldD.schema().getTypes();
        assertEquals("string", fieldSchemaDTypeItems.get(0).getName());
        assertEquals("null", fieldSchemaDTypeItems.get(1).getName());
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#arrayJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsArrayJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(arrayJson);

        List<Schema.Field> fields = jsonSchemaInferrer.getFields(jsonNode);
        assertEquals(1, fields.size());

        // Check `a` field
        Schema.Field fieldA = fields.get(0);

        // "name":"a"
        assertEquals("a", fieldA.name());

        // Check `a` field type content
        // "type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}

        // "type":"array"
        assertEquals("array", fieldA.schema().getType().getName());

        List<Schema.Field> fieldAArray = fieldA.schema().getElementType().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldAArray.get(0);

        // "name":"b"
        assertEquals("b", fieldB.name());

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertEquals("union", fieldBType.getName());
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertEquals("string", fieldSchemaBTypeItems.get(0).getName());
        assertEquals("null", fieldSchemaBTypeItems.get(1).getName());
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#nullJson}
     *
     * Expected fields:
     * [{"name":"a","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsNullJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(nullJson);

        List<Schema.Field> fields = jsonSchemaInferrer.getFields(jsonNode);
        assertEquals(1, fields.size());

        // Check `a` field
        Schema.Field fieldA = fields.get(0);

        // "name":"a"
        assertEquals("a", fieldA.name());

        // Check `a` field type content
        // "type":["string","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertEquals("union", fieldAType.getName());
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertEquals("string", fieldSchemaATypeItems.get(0).getName());
        assertEquals("null", fieldSchemaATypeItems.get(1).getName());
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#intJson}
     *
     * Expected fields:
     * [{"name":"a","type":["int","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsIntJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(intJson);

        List<Schema.Field> fields = jsonSchemaInferrer.getFields(jsonNode);
        assertEquals(1, fields.size());

        // Check `a` field
        Schema.Field fieldA = fields.get(0);

        // "name":"a"
        assertEquals("a", fieldA.name());

        // Check `a` field type content
        // "type":["int","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertEquals("union", fieldAType.getName());
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertEquals("int", fieldSchemaATypeItems.get(0).getName());
        assertEquals("null", fieldSchemaATypeItems.get(1).getName());
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#doubleJson}
     *
     * Expected fields:
     * [{"name":"a","type":["double","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsDoubleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(doubleJson);

        List<Schema.Field> fields = jsonSchemaInferrer.getFields(jsonNode);
        assertEquals(1, fields.size());

        // Check `a` field
        Schema.Field fieldA = fields.get(0);

        // "name":"a"
        assertEquals("a", fieldA.name());

        // Check `a` field type content
        // "type":["double","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertEquals("union", fieldAType.getName());
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertEquals("double", fieldSchemaATypeItems.get(0).getName());
        assertEquals("null", fieldSchemaATypeItems.get(1).getName());
    }

    /**
     * Test {@link JsonSchemaInferrer#inferSchema(String)}
     *
     * Convert the input record: {@link JsonSchemaInferrerTest#simpleJson} to avro schema
     *
     * Expected avro schema:
     * {"type":"record","name":"outer_record","namespace":"org.talend",
     * "fields":[{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},
     * {"name":"d","type":["string","null"]}]}
     *
     * @throws IOException
     */
    @Test
    public void testInferSchema() throws IOException {

        Schema schema = jsonSchemaInferrer.inferSchema(simpleJson);
        List<Schema.Field> fields = schema.getFields();
        assertEquals(2, fields.size());

        // Get `a` field schema and check its type
        Schema.Field fieldA = fields.get(0);
        Schema schemaA = fieldA.schema();
        assertEquals("record", schemaA.getType().getName());

        List<Schema.Field> fieldsA = schemaA.getFields();

        // Check `b` field schema
        Schema.Field fieldB = fieldsA.get(0);
        Schema schemaB = fieldB.schema();
        assertEquals("union", schemaB.getType().getName());
        List<Schema> typesB = schemaB.getTypes();
        assertEquals("string", typesB.get(0).getName());
        assertEquals("null", typesB.get(1).getName());

        // Check `d` field schema
        Schema.Field fieldD = fields.get(1);
        Schema schemaD = fieldD.schema();
        assertEquals("union", schemaD.getType().getName());
        List<Schema> typesD = schemaD.getTypes();
        assertEquals("string", typesD.get(0).getName());
        assertEquals("null", typesD.get(1).getName());
    }
}
