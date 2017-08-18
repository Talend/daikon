package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONAssert.assertNotEquals;
import static org.talend.daikon.properties.presentation.Widget.widget;

import org.junit.Test;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.PropertiesList;
import org.talend.daikon.properties.ReferenceExampleProperties;
import org.talend.daikon.properties.ReferenceExampleProperties.TestAProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.serialize.FullExampleProperties;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UiSchemaGeneratorTest extends AbstractSchemaGenerator {

    @Override
    public PropertiesImpl getNestedProperties(String name) {
        return new NestedProperties(name);
    }

    @Test
    public void genWidget() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleUiSchema.json");
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
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
    public void checkUiOptions() throws Exception {
        ScalaRowProperties properties = new ScalaRowProperties("scalaCodeProperties");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(properties, "scalaCodeForm");

        // "scalaCode": { "ui:widget": "code", "ui:options": { "language": "scala" } }
        ObjectNode scalaCodeUiSchemaJsonObj = (ObjectNode) uiSchemaJsonObj.get("scalaCode");
        assertEquals("\"code\"", scalaCodeUiSchemaJsonObj.get("ui:widget").toString());
        assertEquals("{\"" + Widget.CODE_SYNTAX_WIDGET_CONF + "\":\"scala\"}",
                scalaCodeUiSchemaJsonObj.get("ui:options").toString());
    }

    @Test
    public void checkFilterRowProperties() throws Exception {
        FilterRowProperties properties = new FilterRowProperties("filterRowProperties");
        properties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(properties, "filterRowForm");

        ObjectNode filterRowSchemaJsonObj = (ObjectNode) uiSchemaJsonObj.get("nested");
        assertEquals("{\"type\":\"filter\"}", filterRowSchemaJsonObj.get("ui:options").toString());
        filterRowSchemaJsonObj = (ObjectNode) uiSchemaJsonObj.get("datalistProperty");
        assertEquals("\"datalist\"", filterRowSchemaJsonObj.get("ui:widget").toString());
    }

    @Test
    public void testDoubleUiOrderElementIssue() throws Exception {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(aProperties, "MyForm");
        String expectedPartial = "{\"ui:order\":[\"myStr\",\"np\",\"np2\",\"np4\",\"np5\",\"np3\"]}";
        assertEquals(expectedPartial, uiSchemaJsonObj.toString(), false);
    }

    @Test
    public void testHidden() throws Exception {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        UiSchemaGenerator generator = new UiSchemaGenerator();
        ObjectNode uiSchemaJsonObj = generator.genWidget(aProperties, "MyForm");
        System.out.println(uiSchemaJsonObj.toString());
        String expectedPartial = "{\"np\":{\"myNestedStr\":{\"ui:widget\":\"textarea\"}},\"np4\":{\"ui:widget\":\"hidden\"},"
                + "\"np5\":{\"ui:widget\":\"hidden\"},\"np2\":{\"ui:widget\":\"hidden\"},\"np3\":{\"ui:widget\":\"hidden\"}}";
        assertEquals(expectedPartial, uiSchemaJsonObj.toString(), false);
    }

    /**
     * Verify that when ALL of the top-level Property and Properties widgets are hidden on an AProperties form, the form
     * itself will also be hidden.
     */
    @Test
    public void testAllHidden() throws Exception {
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        Form f = aProperties.getForm("MyForm");

        // When all of the properties are hidden, the form will be hidden too.
        for (Widget w : f.getWidgets()) {
            w.setHidden();
        }

        // When all of the widgets are hidden, there should be a ui:widget = hidden on the root uiSchema.
        ObjectNode uiSchema = new UiSchemaGenerator().genWidget(aProperties, f.getName());
        String expectedPartial = "{\"ui:widget\":\"hidden\"}";
        assertEquals(expectedPartial, uiSchema.toString(), false);
    }

    /**
     * For one of the sub-properties of AProperties, turn all of its widgets into hidden. Verify that this
     * sub-properties is now hidden, but AProperties is still visible (since not ALL of the top-level properties are
     * hidden).
     */
    @Test
    public void testSomeHidden() throws Exception {
        // When all of the properties are hidden, the form will be hidden too.
        AProperties aProperties = new AProperties("foo");
        aProperties.init();
        Form f = aProperties.getForm("MyForm");

        // Hide a sub-sub-property (two levels deep).
        ((Form) f.getWidget("np").getContent()).getWidget("myNestedStr").setHidden();

        ObjectNode uiSchema = new UiSchemaGenerator().genWidget(aProperties, f.getName());
        // Since this is the only property of np, it will cause np to be hidden.
        {
            String expectedPartial = "{\"np\": {\"ui:widget\":\"hidden\"}}";
            assertEquals(expectedPartial, uiSchema.toString(), false);
        }

        // However, there are still visible properties on aProperties, the root will NOT be hidden.
        {
            String expectedPartial = "{\"ui:widget\":\"hidden\"}";
            assertNotEquals(expectedPartial, uiSchema.toString(), false);
        }
    }

    @Test
    public void testWidgetListProperty() throws Exception {
        StringListProperty stringListProperty = new StringListProperty("MyForm");
        stringListProperty.init();
        Form f = stringListProperty.getForm(Form.MAIN);
        ObjectNode uiSchema = new UiSchemaGenerator().genWidget(stringListProperty, f.getName());
        String expectedPartial = "{\"selectColumnIds\":{\"ui:widget\":\"listview\"}}";
        assertEquals(expectedPartial, uiSchema.toString(), false);
    }

    private class NestedProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myNestedStr = PropertyFactory.newString("myNestedStr");

        public NestedProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyNestedForm");
            form.addRow(Widget.widget(myNestedStr).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        }
    }

    private class ScalaRowProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> scalaCode = PropertyFactory.newString("scalaCode");

        public ScalaRowProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "scalaCodeForm");
            form.addRow(widget(scalaCode).setWidgetType(Widget.CODE_WIDGET_TYPE).setConfigurationValue("language", "scala"));
        }
    }

    private class FilterRowProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> datalistProperty = PropertyFactory.newString("datalistProperty");

        // nested properties
        public PropertiesList<FilterRowCriteriaProperties> nested = new PropertiesList<>("nested",
                new PropertiesList.NestedPropertiesFactory<FilterRowCriteriaProperties>() {

                    @Override
                    public FilterRowCriteriaProperties createAndInit(String name) {
                        return (FilterRowCriteriaProperties) new FilterRowCriteriaProperties(name).init();
                    }

                });

        public FilterRowProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "filterRowForm");
            form.addRow(widget(datalistProperty).setWidgetType(Widget.DATALIST_WIDGET_TYPE));
            form.addRow(widget(nested).setWidgetType(Widget.NESTED_PROPERTIES).setConfigurationValue("type", "filter"));
        }
    }

    private class FilterRowCriteriaProperties extends PropertiesImpl {

        public final Property<String> criteriaProperty = PropertyFactory.newString("criteriaProperty");

        public FilterRowCriteriaProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, Form.MAIN);
            form.addRow(widget(criteriaProperty).setWidgetType(Widget.DATALIST_WIDGET_TYPE));
        }

    }

}
