package org.talend.daikon.serialize.jsonschema;

import org.junit.Test;

import static org.junit.Assert.*;

public class UiSchemaGeneratorTest {

    @Test
    public void genWidget() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleUiSchema.json");
        FullExampleProperties properties = new FullExampleProperties("properties");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        assertEquals(jsonStr, generator.genWidget(properties).toString());
    }
}