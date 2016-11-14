package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.serialize.jsonschema.ReferenceExampleProperties.TestReferenceProperties;

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

    @Test
    public void testSerializeUnserializeWithReferenceProperties() throws ParseException, JsonProcessingException, IOException {
        // create a json string of a setup properties
        ReferenceExampleProperties referenceProperties = JsonDataGeneratorTest.createASetupReferenceExampleProperties();
        String parentJson = JsonSchemaUtil.toJson(referenceProperties);
        String childJson = JsonSchemaUtil.toJson(referenceProperties.reference);

        // re-create the Properties from the json data of the json string
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(parentJson);
        JsonNode jsonData = jsonNode.get(JsonSchemaUtil.TAG_JSON_DATA);
        ReferenceExampleProperties deserParentJson = JsonSchemaUtil.fromJson(jsonData.toString(),
                (ReferenceExampleProperties) new ReferenceExampleProperties("refexample").init());

        jsonNode = mapper.readTree(childJson);
        jsonData = jsonNode.get(JsonSchemaUtil.TAG_JSON_DATA);
        TestReferenceProperties deserChildJson = JsonSchemaUtil.fromJson(jsonData.toString(),
                (TestReferenceProperties) new ReferenceExampleProperties.TestReferenceProperties("reference").init());

        // merge everything to the parent
        Map<String, Properties> propertiesMap = new HashMap<>();
        propertiesMap.put("ReferenceExample", deserParentJson);
        propertiesMap.put("TestReference", deserChildJson);
        JsonSchemaUtil.resolveReferenceProperties(propertiesMap);

        // compare the parent with the initial object
        assertEquals(referenceProperties, deserParentJson);
    }

    public static String readJson(String path) throws URISyntaxException, IOException {
        java.net.URL url = JsonSchemaUtilTest.class.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8").trim();
    }

}
