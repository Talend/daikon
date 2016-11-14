package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.properties.ReferenceExampleProperties;
import org.talend.daikon.properties.ReferenceExampleProperties.TestAProperties;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UiSchemaGeneratorTest {

    @Test
    public void genWidget() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleUiSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        assertEquals(jsonStr, generator.genWidget(properties).toString());
    }

    @Test
    public void genWidgetWithRefPropertiesHidden() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("ReferenceExampleUiSchema.json");
        ReferenceExampleProperties refEProp = (ReferenceExampleProperties) new ReferenceExampleProperties(null).init();
        TestAProperties testAProp = (TestAProperties) new TestAProperties(null).init();
        refEProp.testAPropReference.setReference(testAProp);

        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(refEProp);
        assertEquals(jsonStr, uiSchemaJsonObj.toString());
    }

}
