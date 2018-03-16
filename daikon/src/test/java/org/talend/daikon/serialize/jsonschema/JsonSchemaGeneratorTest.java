package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

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

public class JsonSchemaGeneratorTest extends AbstractSchemaGenerator {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Override
    public PropertiesImpl getNestedProperties(String name) {
        return new NestedProperties(name);
    }

    @Test
    public void genSchema() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        assertEquals(jsonStr, generator.generateJsonSchema(properties, Form.MAIN).toString());
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
        ObjectNode genSchema = generator.generateJsonSchema(aProperties, "MyForm");
        String expectedPartial = "{\"properties\": {\"np\": {\"title\": \"form.MyNestedForm.displayName\"},"
                + "\"np2\": {\"title\": \"properties.np2.displayName\"},\"np3\": {\"title\": \"\"},"
                + "\"np4\": {\"title\": \"\"},\"np5\": {\"title\": \"\"}},\"title\": \"form.MyForm.displayName\"}";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test
    public void testI18nForMultiValues() throws JSONException {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.generateJsonSchema(aProperties, "MyForm");
        String expectedPartial = "{\"properties\":{\"np\":{\"properties\":{\"myNestedMultiValueStr\":"
                + "{\"type\":\"string\",\"enum\":[\"a\",\"b\"],\"enumNames\":[\"Ai18n\",\"Bi18n\"]}}}}}\n";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test
    public void testStringListProperty() throws JSONException {
        StringListProperty stringListProperty = new StringListProperty("foot");
        stringListProperty.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.generateJsonSchema(stringListProperty, Form.MAIN);
        String expectedPartial = "{\"title\":\"form.Main.displayName\",\"type\":\"object\",\"properties\""
                + ":{\"selectColumnIds\":{\"title\":\"property.selectColumnIds.displayName\","
                + "\"type\":\"array\",\"items\":{\"type\":\"string\",\"enum\":[\"col1\",\"col2\",\"col3\"],"
                + "\"enumNames\":[\"Surname\",\"Name\",\"Phone\"]},\"uniqueItems\":true,\"minItems\":1}}}";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test
    public void testStringListProperty_whenHidden() throws JSONException {
        StringListProperty stringListProperty = new StringListProperty("selectColumnIds") {

            @Override
            public Form getPreferredForm(String formName) {
                final Form form = new Form(this, formName);

                final Widget widget = new Widget(this);
                widget.setHidden();

                form.addRow(widget);
                return form;
            }
        };
        stringListProperty.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.generateJsonSchema(stringListProperty, Form.MAIN);
        // Only difference is the "minItems=0" since widget is hidden
        String expectedPartial = "{\"title\":\"form.Main.displayName\",\"type\":\"object\",\"properties\""
                + ":{\"selectColumnIds\":{\"title\":\"property.selectColumnIds.displayName\","
                + "\"type\":\"array\",\"items\":{\"type\":\"string\",\"enum\":[\"col1\",\"col2\",\"col3\"],"
                + "\"enumNames\":[\"Surname\",\"Name\",\"Phone\"]},\"minItems\":0}}}";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test()
    public void testTDKN445_NPE_unusedDoubleNestedProps() throws JSONException {
        UnusedNestedProperties props = new UnusedNestedProperties("foo");
        props.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.generateJsonSchema(props, Form.MAIN);
        String expectedPartial = "{\"title\":\"form.Main.displayName\",\"type\":\"object\",\"properties\":{\"nested2\":{\"title\":\"\",\"type\":\"object\",\"properties\":{\"nested\":{\"title\":\"\",\"type\":\"object\",\"properties\":{\"myNestedStr\":{\"title\":\"property.myNestedStr.displayName\",\"type\":\"string\"},\"myNestedMultiValueStr\":{\"title\":\"property.myNestedMultiValueStr.displayName\",\"type\":\"string\",\"enum\":[\"a\",\"b\"],\"enumNames\":[\"Ai18n\",\"Bi18n\"]}}}}}}}";
        assertEquals(expectedPartial, genSchema.toString(), false);
    }

    @Test()
    public void testRequiredOnlySetWhenVisible() throws JSONException {
        NestedProperties props = new NestedProperties("foo");
        props.init();
        JsonSchemaGenerator generator = new JsonSchemaGenerator();
        ObjectNode genSchema = generator.generateJsonSchema(props, Form.MAIN);
        String expectedComplete = "{\"title\":\"\",\"type\":\"object\",\"properties\":{\"myNestedStr\":{\"title\":\"property.myNestedStr.displayName\",\"type\":\"string\"},\"myNestedMultiValueStr\":{\"title\":\"property.myNestedMultiValueStr.displayName\",\"type\":\"string\",\"enum\":[\"a\",\"b\"],\"enumNames\":[\"Ai18n\",\"Bi18n\"]}}}";
        assertEquals(expectedComplete, genSchema.toString(), true);
    }

    public class NestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myNestedStr = PropertyFactory.newString("myNestedStr").setRequired();

        public final Property<String> myNestedMultiValueStr = new StringProperty("myNestedMultiValueStr") {

            @Override
            public String getPossibleValuesDisplayName(Object possibleValue) {
                switch ((String) possibleValue) {
                case "a":
                    return "Ai18n";
                case "b":
                    return "Bi18n";
                default:
                    return null;
                }
            }
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

    public class NestedNestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final NestedProperties nested = new NestedProperties("nested");

        public NestedNestedProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyNestedForm2");
            form.addRow(nested.getForm("MyNestedForm"));
        }
    }

    public class UnusedNestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final NestedNestedProperties nested2 = new NestedNestedProperties("nested2");

        public UnusedNestedProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, Form.MAIN);
            // the nested2 prop is not added intentionally
        }
    }

}
