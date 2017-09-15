package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collections;

import org.junit.Test;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.TestEmptyProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.serialize.FullExampleProperties;
import org.talend.daikon.serialize.FullExampleTestUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import shaded.org.apache.commons.io.IOUtils;

public class JsonSchemaUtilTest {

    @Test
    public void testUnserializeWithInstance() throws ParseException, JsonProcessingException, IOException {
        // create a json string of a setup properties
        FullExampleProperties fep = FullExampleTestUtil.createASetupFullExampleProperties();
        ObjectNode propertiesData = new JsonDataGenerator().processTPropertiesData(fep);
        FullExampleProperties deserFep = JsonSchemaUtil.fromJson(propertiesData.toString(),
                (FullExampleProperties) new FullExampleProperties("fullexample").init());
        // compare them
        assertEquals(fep, deserFep);
    }

    @Test
    public void testfromJsonNode() throws ParseException, JsonProcessingException, IOException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        DefinitionRegistryService defRegServ = getRegistryWithDef1();

        // create a json string of a setup properties
        FullExampleProperties fep = FullExampleTestUtil.createASetupFullExampleProperties();
        String json = new JsonDataGenerator().processTPropertiesData(fep).toString();

        // check instance is deserialized properly
        // re-create the Properties from the json data of the json string
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonData = (ObjectNode) mapper.readTree(json);
        jsonData.put(JsonSchemaConstants.DEFINITION_NAME_JSON_METADATA, "def1");
        FullExampleProperties deserFep = (FullExampleProperties) JsonSchemaUtil.fromJsonNode(defRegServ, jsonData);
        // compare them
        assertEquals(fep, deserFep);

        // check that if no definition node found we have the right exception
        mapper = new ObjectMapper();
        jsonData = (ObjectNode) mapper.readTree(json);
        try {
            deserFep = (FullExampleProperties) JsonSchemaUtil.fromJsonNode(defRegServ, jsonData);
            fail("should have thrown an exception");
        } catch (TalendRuntimeException e) {
            assertEquals(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e.getCode());
        }

        // check that if wrong definition found we have the right exception
        mapper = new ObjectMapper();
        jsonData = (ObjectNode) mapper.readTree(json);
        jsonData.put(JsonSchemaConstants.DEFINITION_NAME_JSON_METADATA, "unregistered_def");
        try {
            deserFep = (FullExampleProperties) JsonSchemaUtil.fromJsonNode(defRegServ, jsonData);
            fail("should have thrown an exception");
        } catch (TalendRuntimeException e) {
            assertTrue(e.getCode() == CommonErrorCodes.UNREGISTERED_DEFINITION);
            assertTrue(e.getContext().get("definitionName") == "unregistered_def");
        }

    }

    @Test
    public void testUnserializeWithDefinitionName()
            throws ParseException, JsonProcessingException, IOException, URISyntaxException {
        FullExampleProperties fep = FullExampleTestUtil.createASetupFullExampleProperties();
        String json = new JsonDataGenerator().genData(fep, "def1").toString();
        FullExampleProperties deserFep = (FullExampleProperties) JsonSchemaUtil.fromJson(json, getRegistryWithDef1());
        // compare them
        assertEquals(fep, deserFep);
    }

    @Test
    public void testUnserializeWithDefinitionNameAndStream() throws ParseException, JsonProcessingException, IOException {
        FullExampleProperties fep = FullExampleTestUtil.createASetupFullExampleProperties();
        ObjectNode jsonNode = new JsonDataGenerator().genData(fep, "def1");
        try (InputStream is = IOUtils.toInputStream(jsonNode.toString())) {
            FullExampleProperties deserFep = (FullExampleProperties) JsonSchemaUtil.fromJson(is, getRegistryWithDef1());
            // compare them
            assertEquals(fep, deserFep);
        }
    }

    public DefinitionRegistryService getRegistryWithDef1() {
        DefinitionRegistryService defRegServ = mock(DefinitionRegistryService.class);
        Definition exampleDef = mock(Definition.class);
        when(exampleDef.getPropertiesClass()).thenReturn(FullExampleProperties.class);
        when(defRegServ.getDefinitionsMapByType(Definition.class)).thenReturn(Collections.singletonMap("def1", exampleDef));
        when(defRegServ.createProperties(exampleDef, "")).thenReturn(new FullExampleProperties(""));
        return defRegServ;
    }

    @Test
    public void testSerializeWithDefinitionName() throws URISyntaxException, IOException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String propertiesJsonStr = JsonSchemaUtil.toJson(new FullExampleProperties("fullexample").init(), Form.MAIN,
                "ZeDefinitionName");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(propertiesJsonStr);
        ObjectNode jsonData = (ObjectNode) jsonNode.get(JsonSchemaUtil.TAG_JSON_DATA);
        JsonNode defNameNode = jsonData.get(JsonSchemaConstants.DEFINITION_NAME_JSON_METADATA);
        assertNotNull(defNameNode);
        assertEquals(defNameNode.asText(), "ZeDefinitionName");
    }

    public static String readJson(String path) throws URISyntaxException, IOException {
        java.net.URL url = JsonSchemaUtilTest.class.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8").trim();
    }

    public static void writeJson(String content, String path) throws URISyntaxException, IOException {
        FileWriter fileWriter = null;
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
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
