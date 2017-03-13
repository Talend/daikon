package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.StringProperty;
import org.talend.daikon.properties.test.PropertiesTestUtils;
import org.talend.daikon.serialize.FullExampleProperties;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JsonSchemaGeneratorTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    class NestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myNestedStr = PropertyFactory.newString("myNestedStr");

        public final Property<String> myNestedMultiValueStr = new StringProperty("myNestedMultiValueStr") {

            public String getPossibleValuesDisplayName(Object possibleValue) {
                switch ((String) possibleValue) {
                case "a":
                    return "Ai18n";
                case "b":
                    return "Bi18n";
                default:
                    return null;
                }
            };
        };

        public NestedProperties(String name) {
            super(name);
        }

        @Override
        public void setupProperties() {
            super.setupProperties();
            myNestedMultiValueStr.setPossibleValues("a", "b");
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

        public final NestedProperties np2 = new NestedProperties("np2");

        public final NestedProperties np3 = new NestedProperties("np3");

        public final NestedProperties np4 = new NestedProperties("np4");

        public final NestedProperties np5 = new NestedProperties("np5");

        public AProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyForm");
            form.addRow(myStr);
            form.addRow(np.getForm("MyNestedForm"));
            form.addRow(np2);
            form.addRow(Widget.widget(np4.getForm("MyNestedForm")).setVisible(false));
            form.addRow(Widget.widget(np5).setVisible(false));
            Form anotherForm = new Form(this, "anotherForm");
            anotherForm.addRow(np3);
        }
    }

    @Test
    public void genSchema() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        assertEquals(jsonStr, generator.genSchema(properties, Form.MAIN).toString());
    }

    @Test
    public void testI18N() {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        PropertiesTestUtils.checkAllI18N(properties, errorCollector);
    }

    @Test
    public void testTitleForm() throws JSONException {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.genSchema(aProperties, "MyForm");
        String expectedPartial = "{\"properties\": {\"np\": {\"title\": \"form.MyNestedForm.displayName\"},\"np2\": {\"title\": \"properties.np2.displayName\"},\"np3\": {\"title\": \"\"},\"np4\": {\"title\": \"\"},\"np5\": {\"title\": \"\"}},\"title\": \"form.MyForm.displayName\"}";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test
    public void testI18nForMultiValues() throws JSONException {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.genSchema(aProperties, "MyForm");
        String expectedPartial = "{\"properties\":{\"np\":{\"properties\":{\"myNestedMultiValueStr\":{\"type\":\"string\",\"enum\":[\"a\",\"b\"],\"enumNames\":[\"Ai18n\",\"Bi18n\"]}}}}}\n";
        System.out.println(genSchema.toString());
        assertEquals(expectedPartial, genSchema.toString(), false);
    }
}
