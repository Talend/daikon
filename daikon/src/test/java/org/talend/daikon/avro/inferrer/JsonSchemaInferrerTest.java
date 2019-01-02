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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

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

    private final String jsonComplexRecordWithStrFields = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String jsonArrayOfComplexStrRecords = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String jsonArrayOfString = "{\"a\": [\"b1\", \"b2\"]}";

    private final String jsonArrayOfInteger = "{\"a\": [10, 11]}";

    private final String jsonArrayOfNull = "{\"a\": [null]}";

    private final String jsonSimpleRecordNull = "{\"a\": null}";

    private final String jsonSimpleRecordInt = "{\"a\": 100}";

    private final String jsonSimpleRecordDouble = "{\"a\": 100.1}";

    private final String jsonComplexRecordWithBooleanFields = "{\"a\": {\"b\": false}, \"d\": true}";

    private final String jsonArrayOfComplexBooleanRecords = "{\"a\": [{\"b\": true}, {\"b\": false}]}";

    private final String realComplexRecord = "{\"id\": 1, \"features\": [{"
            + "\"geometry\": {\"coordinates\": [ 2.3903853, 48.8732135]}, "
            + "\"properties\": {\"status_ok\": true, \"public_name\": \"Paris\", "
            + "\"geo_point\": [], \"kind\": {}, \"postal_code\": null}}]}";

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonComplexRecordWithStrFields}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},{"name":"d","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfComplexRecordWithStrFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonComplexRecordWithStrFields);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(2));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}

        // "type":"record"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("record")));

        // "fields":[{"name":"b","type":["null","string"]}]
        List<Schema.Field> fieldATypeFields = fieldA.schema().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldATypeFields.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypes = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaBTypes.get(1).getName(), is(equalTo("null")));

        // Check `d` field
        Schema.Field fieldD = fieldList.get(1);

        // "name":"d"
        assertThat(fieldD.name(), is(equalTo("d")));

        // Check `d` field type content
        // "type":["string","null"]
        Schema.Type fieldDType = fieldD.schema().getType();
        assertThat(fieldDType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaDTypes = fieldD.schema().getTypes();
        assertThat(fieldSchemaDTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaDTypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonArrayOfComplexStrRecords}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfArrayOfComplexRecords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonArrayOfComplexStrRecords);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}

        // "type":"array"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("array")));

        List<Schema.Field> fieldAArray = fieldA.schema().getElementType().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldAArray.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypes = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaBTypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonArrayOfString}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":["string","null"]}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfArrayOfString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonArrayOfString);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":["string","null"]}
        Schema.Type fieldAType = fieldA.schema().getType();

        // "type":"array"
        assertThat(fieldAType.getName(), is(equalTo("array")));

        // "items":["string","null"]
        Schema fieldSchemaATypeItems = fieldA.schema().getElementType();
        assertThat(fieldSchemaATypeItems.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldSchemaATypeItems.getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonArrayOfInteger}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":["int","null"]}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfArrayOfInteger() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonArrayOfInteger);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":["int","null"]}
        Schema.Type fieldAType = fieldA.schema().getType();

        // "type":"array"
        assertThat(fieldAType.getName(), is(equalTo("array")));

        // "items":["int","null"]
        Schema fieldSchemaATypeItems = fieldA.schema().getElementType();
        assertThat(fieldSchemaATypeItems.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldSchemaATypeItems.getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("int")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonArrayOfNull}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":["string","null"]}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfArrayOfNull() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonArrayOfNull);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":["string","null"]}
        Schema.Type fieldAType = fieldA.schema().getType();

        // "type":"array"
        assertThat(fieldAType.getName(), is(equalTo("array")));

        // "items":["string","null"]
        Schema fieldSchemaATypeItems = fieldA.schema().getElementType();
        assertThat(fieldSchemaATypeItems.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldSchemaATypeItems.getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonSimpleRecordNull}
     *
     * Expected fields:
     * [{"name":"a","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfSimpleRecordNull() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonSimpleRecordNull);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["string","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonSimpleRecordInt}
     *
     * Expected fields:
     * [{"name":"a","type":["int","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfSimpleRecordInt() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonSimpleRecordInt);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["int","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("int")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonSimpleRecordDouble}
     *
     * Expected fields:
     * [{"name":"a","type":["double","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfSimpleRecordDouble() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonSimpleRecordDouble);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["double","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("double")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonComplexRecordWithBooleanFields}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}},{"name":"d","type":["boolean","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfComplexRecordWithBooleanFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonComplexRecordWithBooleanFields);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(2));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}

        // "type":"record"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("record")));

        // "fields":[{"name":"b","type":["boolean","null"]}]
        List<Schema.Field> fieldATypeFields = fieldA.schema().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldATypeFields.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypes = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypes.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaBTypes.get(1).getName(), is(equalTo("null")));

        // Check `d` field
        Schema.Field fieldD = fieldList.get(1);

        // "name":"d"
        assertThat(fieldD.name(), is(equalTo("d")));

        // Check `d` field type content
        // "type":["boolean","null"]
        Schema.Type fieldDType = fieldD.schema().getType();
        assertThat(fieldDType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaDTypes = fieldD.schema().getTypes();
        assertThat(fieldSchemaDTypes.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaDTypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#jsonArrayOfComplexBooleanRecords}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsOfArrayOfComplexBooleanRecords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonArrayOfComplexBooleanRecords);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}}

        // "type":"array"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("array")));

        List<Schema.Field> fieldAArray = fieldA.schema().getElementType().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldAArray.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["boolean","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypes = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypes.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaBTypes.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#inferSchema(String)}
     *
     * Convert the input record: {@link JsonSchemaInferrerTest#realComplexRecord} to avro schema
     *
     * Expected avro schema:
     * {"type":"record","name":"outer_record","namespace":"org.talend","fields":[
     * {"name":"id","type":["int","null"]},
     * {"name":"features","type":{"type":"array","items":{"type":"record","namespace":"","fields":[
     * {"name":"geometry","type":{"type":"record","fields":[
     * {"name":"coordinates","type":{"type":"array","items":["double","null"]}}]}},
     * {"name":"properties","type":{"type":"record","fields":[
     * {"name":"status_ok","type":["boolean","null"]},{"name":"public_name","type":["string","null"]},
     * {"name":"geo_point","type":{"type":"array","items":["string","null"]}},
     * {"name":"kind","type":{"type":"record","fields":[]}},{"name":"postal_code","type":["string","null"]}]}}]}}}]}
     *
     * @throws IOException
     */
    @Test
    public void testInferSchemaOfRealComplexRecord() throws IOException {

        Schema schema = jsonSchemaInferrer.inferSchema(realComplexRecord);
        assertThat(schema.getName(), is(startsWith("outer_record")));
        List<Schema.Field> fieldList = schema.getFields();
        assertThat(fieldList, hasSize(2));

        // Get `id` field schema and check its type
        // Check `id` field
        Schema.Field fieldId = fieldList.get(0);

        // "name":"id"
        assertThat(fieldId.name(), is(equalTo("id")));

        // Check `id` field type content
        // "type":["int","null"]
        Schema.Type fieldIdType = fieldId.schema().getType();
        assertThat(fieldIdType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaIdTypes = fieldId.schema().getTypes();
        assertThat(fieldSchemaIdTypes.get(0).getName(), is(equalTo("int")));
        assertThat(fieldSchemaIdTypes.get(1).getName(), is(equalTo("null")));

        // Get `features` field schema and check its type
        // Check `features` field
        Schema.Field fieldFeatures = fieldList.get(1);

        // "name":"features"
        assertThat(fieldFeatures.name(), is(equalTo("features")));

        // "type":"array"
        assertThat(fieldFeatures.schema().getType().getName(), is(equalTo("array")));

        List<Schema.Field> featuresFields = fieldFeatures.schema().getElementType().getFields();

        // Get `geometry` field schema and check its type
        // {"name":"geometry","type":{"type":"record","fields":[{"name":"coordinates","type":{"type":"array","items":["double","null"]}}]}}
        Schema.Field fieldGeometry = featuresFields.get(0);

        // "name":"geometry"
        assertThat(fieldGeometry.name(), is(equalTo("geometry")));

        // Check `geometry` field type content
        // "type":"record"
        assertThat(fieldGeometry.schema().getType().getName(), is(equalTo("record")));

        // "fields":[{"name":"coordinates","type":{"type":"array","items":["double","null"]}}]
        List<Schema.Field> fieldGeometryFields = fieldGeometry.schema().getFields();

        // Get `coordinates` field schema and check its type
        Schema.Field fieldCoordinates = fieldGeometryFields.get(0);

        Schema.Type fieldCoordinatesType = fieldCoordinates.schema().getType();

        // "type":"array"
        assertThat(fieldCoordinatesType.getName(), is(equalTo("array")));

        // "items":["double","null"]
        Schema fieldCoordinatesItems = fieldCoordinates.schema().getElementType();
        assertThat(fieldCoordinatesItems.getName(), is(equalTo("union")));
        List<Schema> fieldCoordinatesItemsList = fieldCoordinatesItems.getTypes();
        assertThat(fieldCoordinatesItemsList.get(0).getName(), is(equalTo("double")));
        assertThat(fieldCoordinatesItemsList.get(1).getName(), is(equalTo("null")));

        // Get `properties` field schema and check its type
        // Check `properties` field
        Schema.Field fieldProperties = featuresFields.get(1);

        // "name":"geometry"
        assertThat(fieldProperties.name(), is(equalTo("properties")));

        List<Schema.Field> fieldPropertiesFields = fieldProperties.schema().getFields();

        // Get `status_ok` field schema and check its type
        Schema.Field fieldStatus = fieldPropertiesFields.get(0);

        // "name":"status_ok"
        assertThat(fieldStatus.name(), is(equalTo("status_ok")));

        // "type":["boolean","null"]
        Schema.Type fieldStatusType = fieldStatus.schema().getType();
        assertThat(fieldStatusType.getName(), is(equalTo("union")));
        List<Schema> fieldStatusTypes = fieldStatus.schema().getTypes();
        assertThat(fieldStatusTypes.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldStatusTypes.get(1).getName(), is(equalTo("null")));

        // Get `public_name` field schema and check its type
        Schema.Field fieldPublicName = fieldPropertiesFields.get(1);

        // "name":"public_name"
        assertThat(fieldPublicName.name(), is(equalTo("public_name")));

        // "type":["string","null"]
        Schema.Type fieldPublicNameType = fieldPublicName.schema().getType();
        assertThat(fieldPublicNameType.getName(), is(equalTo("union")));
        List<Schema> fieldPublicNameTypes = fieldPublicName.schema().getTypes();
        assertThat(fieldPublicNameTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldPublicNameTypes.get(1).getName(), is(equalTo("null")));

        // Get `geo_point` field schema and check its type
        Schema.Field fieldGeoPoint = fieldPropertiesFields.get(2);

        // "name":"geo_point"
        assertThat(fieldGeoPoint.name(), is(equalTo("geo_point")));

        // "type":{"type":"array","items":["string","null"]}
        Schema.Type fieldGeoPointType = fieldGeoPoint.schema().getType();

        // "type":"array"
        assertThat(fieldGeoPointType.getName(), is(equalTo("array")));

        // "items":["string","null"]
        Schema fieldGeoPointTypeItems = fieldGeoPoint.schema().getElementType();
        assertThat(fieldGeoPointTypeItems.getName(), is(equalTo("union")));
        List<Schema> fieldGeoPointTypes = fieldGeoPointTypeItems.getTypes();
        assertThat(fieldGeoPointTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldGeoPointTypes.get(1).getName(), is(equalTo("null")));

        // Get `kind` field schema and check its type
        // {"name":"kind","type":{"type":"record","fields":[]}}
        Schema.Field fieldKind = fieldPropertiesFields.get(3);

        // "name":"kind"
        assertThat(fieldKind.name(), is(equalTo("kind")));

        Schema.Type fieldKindType = fieldKind.schema().getType();

        // "type":"record"
        assertThat(fieldKindType.getName(), is(equalTo("record")));
        // "fields":[]
        assertThat(fieldKind.schema().getFields(), hasSize(0));

        // Get `postal_code` field schema and check its type
        // {"name":"postal_code","type":["string","null"]}]}
        Schema.Field fieldPostalCode = fieldPropertiesFields.get(4);

        // "name":"postal_code"
        assertThat(fieldPostalCode.name(), is(equalTo("postal_code")));

        // "type":["string","null"]
        Schema.Type fieldPostalCodeType = fieldPostalCode.schema().getType();
        assertThat(fieldPostalCodeType.getName(), is(equalTo("union")));
        List<Schema> fieldPostalCodeTypes = fieldPostalCode.schema().getTypes();
        assertThat(fieldPostalCodeTypes.get(0).getName(), is(equalTo("string")));
        assertThat(fieldPostalCodeTypes.get(1).getName(), is(equalTo("null")));

        // Ensure that the inference is deterministic (generates the same schema twice).
        // This also ensure that for a given schema, the name is deterministic.
        Schema schema2 = jsonSchemaInferrer.inferSchema(realComplexRecord);
        assertThat(schema, equalTo(schema2));
    }

    /**
     * Test {@link JsonSchemaInferrer#inferSchema(String)}
     *
     * Check if two schemas are named differently
     *
     * @throws IOException
     */
    @Test
    public void testInferSchemaUnicity() throws IOException {
        Schema schema1 = jsonSchemaInferrer.inferSchema(realComplexRecord);
        Schema schema2 = jsonSchemaInferrer.inferSchema(jsonArrayOfNull);
        assertThat(schema1.getName(), is(not(equalTo(schema2.getName()))));
    }

    /**
     * Test {@link JsonSchemaInferrer#getAvroSchema(JsonNode)}
     *
     * Get avro schema from json node of input record: {@link JsonSchemaInferrerTest#jsonSimpleRecordInt}
     *
     * Expected avro schema:
     * {"type":"record","fields":[{"name":"a","type":["int","null"]}]}
     *
     * @throws IOException
     */
    @Test
    public void testGetAvroSchemaOfSimpleRecordInt() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonSimpleRecordInt);
        Schema schemaOfSimpleRecordInt = jsonSchemaInferrer.getAvroSchema(jsonNode);

        // "type":"record"
        assertThat(schemaOfSimpleRecordInt.getType().getName(), is(equalTo("record")));

        // Check `a` field
        Schema.Field fieldA = schemaOfSimpleRecordInt.getFields().get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["int","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypes = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypes.get(0).getName(), is(equalTo("int")));
        assertThat(fieldSchemaATypes.get(1).getName(), is(equalTo("null")));
    }
}
