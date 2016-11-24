package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.properties.test.PropertiesTestUtils;
import org.talend.daikon.serialize.FullExampleProperties;

public class JsonSchemaGeneratorTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void genSchema() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        assertEquals(jsonStr, generator.genSchema(properties).toString());
    }

    @Test
    public void testI18N() {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        PropertiesTestUtils.checkAllI18N(properties, errorCollector);
    }

}
