package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ReferenceExampleProperties;
import org.talend.daikon.properties.ReferenceExampleProperties.TestAProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.serialize.FullExampleProperties;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UiSchemaGeneratorTest {

    class NestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myNestedStr = PropertyFactory.newString("myNestedStr");

        public NestedProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyNestedForm");
            form.addRow(myNestedStr);
        }
    }

    class AProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myStr = PropertyFactory.newString("myStr");

        public final NestedProperties np = new NestedProperties("np");

        public AProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyForm");
            form.addRow(myStr);
            form.addRow(np.getForm("MyNestedForm"));
        }
    }

    @Test
    public void genWidget() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleUiSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        System.out.println(generator.genWidget(properties, Form.MAIN).toString());
        assertEquals(jsonStr, generator.genWidget(properties, Form.MAIN).toString());
    }

    @Test
    public void genWidgetWithRefPropertiesHidden() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("ReferenceExampleUiSchema.json");
        ReferenceExampleProperties refEProp = (ReferenceExampleProperties) new ReferenceExampleProperties(null).init();
        TestAProperties testAProp = (TestAProperties) new TestAProperties(null).init();
        refEProp.testAPropReference.setReference(testAProp);

        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(refEProp, Form.MAIN);
        assertEquals(jsonStr, uiSchemaJsonObj.toString());
    }

    @Test
    public void testDoubleUiOrderElementIssue() throws Exception {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(aProperties, "MyForm");
        String expectedPartial = "{\"ui:order\":[\"myStr\",\"np\"]}";
        JSONAssert.assertEquals(expectedPartial, uiSchemaJsonObj.toString(), false);
    }

}
