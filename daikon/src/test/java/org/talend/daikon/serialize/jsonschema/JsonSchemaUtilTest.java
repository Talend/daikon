package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.junit.Test;
import org.talend.daikon.properties.TestEmptyProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSchemaUtilTest {

    @Test
    public void test() throws URISyntaxException, IOException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String jsonDataStr = readJson("FullExampleJsonData.json");

        FullExampleProperties properties = JsonSchemaUtil.fromJson(jsonDataStr,
                (FullExampleProperties) new FullExampleProperties("fullexample").init());

        String jsonStr = readJson("FullExampleProperties.json");
        String jsonResult = JsonSchemaUtil.toJson(properties);
        assertEquals(jsonStr, jsonResult);
    }

    @Test
    public void testSerializeUnserialize() throws ParseException, JsonProcessingException, IOException {
        // create a json string of a setup properties
        FullExampleProperties fep = JsonDataGeneratorTest.createASetupFullExampleProperties();
        String json = JsonSchemaUtil.toJson(fep);
        // re-create the Properties from the json data of the json string
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode jsonData = jsonNode.get(JsonSchemaUtil.TAG_JSON_DATA);
        FullExampleProperties deserFep = JsonSchemaUtil.fromJson(jsonData.toString(),
                (FullExampleProperties) new FullExampleProperties("fullexample").init());
        // compare them
        assertEquals(fep, deserFep);
    }

    public static String readJson(String path) throws URISyntaxException, IOException {
        java.net.URL url = JsonSchemaUtilTest.class.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8").trim();
    }

    @Test
    public void testDeserializeEmptyProperties() throws ParseException, JsonProcessingException, IOException {
        TestEmptyProperties properties = TestEmptyProperties.createASetupOptionalProperties();

        // Test instanciate the Properties with its default value
        String full = "{\"aProperty\":\"initalValue\",\"innerProperties\":{\"innerProperty\":\"initialInnerValue\"},"
                + "\"@definitionName\":\"testName\"}";
        TestEmptyProperties propertiesFull = JsonSchemaUtil.fromJson(full, TestEmptyProperties.createASetupOptionalProperties());
        assertEquals(properties, propertiesFull);

        // Test instanciate the Properties an empty JSON.
        String emptyJSON = "{}";
        TestEmptyProperties propertiesEmptyJSON = JsonSchemaUtil.fromJson(emptyJSON,
                TestEmptyProperties.createASetupOptionalProperties());
        assertEquals(properties, propertiesEmptyJSON);

        // Test instanciate the Properties with a JSON containing no Property but all the properties.
        String noData = "{\"innerProperties\":{}}";
        TestEmptyProperties propertiesNoData = JsonSchemaUtil.fromJson(noData,
                TestEmptyProperties.createASetupOptionalProperties());
        assertEquals(properties, propertiesNoData);
    }
}
