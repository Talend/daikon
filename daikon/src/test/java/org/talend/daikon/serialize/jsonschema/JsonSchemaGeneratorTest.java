package org.talend.daikon.serialize.jsonschema;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonSchemaGeneratorTest {

    @Test
    public void genSchema() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonSchema.json");
        FullExampleProperties properties = new FullExampleProperties("properties");
        properties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        assertEquals(jsonStr, generator.genSchema(properties).toString());
    }

}