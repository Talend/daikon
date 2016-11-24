package org.talend.daikon.serialize.jsonschema;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.talend.daikon.serialize.FullExampleProperties;
import org.talend.daikon.serialize.FullExampleTestUtil;

public class JsonPropertiesResolverTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void resolveJson() throws Exception {
        String jsonDataStr = JsonSchemaUtilTest.readJson("FullExampleJsonData.json");
        JsonPropertiesResolver resolver = new JsonPropertiesResolver();
        FullExampleProperties properties = (FullExampleProperties) resolver.resolveJson((ObjectNode) mapper.readTree(jsonDataStr),
                new FullExampleProperties("fullexample").init());

        FullExampleTestUtil.comparePropertiesValue(FullExampleTestUtil.createASetupFullExampleProperties(), properties);
    }

}
