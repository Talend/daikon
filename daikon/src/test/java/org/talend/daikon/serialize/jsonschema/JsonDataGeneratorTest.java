package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.serialize.FullExampleProperties;
import org.talend.daikon.serialize.FullExampleTestUtil;

public class JsonDataGeneratorTest {

    @Test
    public void genData() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonData.json");
        FullExampleProperties properties = FullExampleTestUtil.createASetupFullExampleProperties();

        JsonDataGenerator generator = new JsonDataGenerator();
        assertEquals(jsonStr, generator.genData(properties, "def1").toString());
    }

}
