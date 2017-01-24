package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.serialize.FullExampleProperties;

public class JsonWidgetTest {

    private FullExampleProperties properties = new FullExampleProperties("fullexample");

    private Form mainForm;

    private Form advancedForm;

    @Before
    public void init() {
        properties.init();
        mainForm = properties.getForm(Form.MAIN);
        advancedForm = properties.getForm(Form.ADVANCED);
    }

    @Test
    public void getOrder() throws Exception {
        Widget stringPropWidget = mainForm.getWidget(properties.stringProp.getName());
        Widget hiddenTextPropWidget = mainForm.getWidget(properties.hiddenTextProp.getName());
        Widget textareaPropWidget = advancedForm.getWidget(properties.textareaProp.getName());
        JsonWidget jsonWidget = new JsonWidget(stringPropWidget, mainForm);
        assertEquals(stringPropWidget.getRow() * 100 + stringPropWidget.getOrder(), jsonWidget.getOrder());
        jsonWidget = new JsonWidget(hiddenTextPropWidget, mainForm);
        assertEquals(hiddenTextPropWidget.getRow() * 100 + hiddenTextPropWidget.getOrder(), jsonWidget.getOrder());
        jsonWidget = new JsonWidget(textareaPropWidget, advancedForm);
        assertEquals(textareaPropWidget.getRow() * 10000 + textareaPropWidget.getOrder(), jsonWidget.getOrder());
    }

    @Test
    public void getContent() throws Exception {
        Widget stringPropWidget = mainForm.getWidget(properties.stringProp.getName());
        JsonWidget jsonWidget = new JsonWidget(stringPropWidget, mainForm);
        assertEquals(properties.stringProp, jsonWidget.getContent());
        Widget commonPropWidget = mainForm.getWidget(properties.commonProp.getName());
        jsonWidget = new JsonWidget(commonPropWidget, mainForm);
        assertEquals(properties.commonProp, jsonWidget.getContent());
    }

    @Test
    public void getName() throws Exception {
        Widget commonPropWidget = mainForm.getWidget(properties.commonProp.getName());
        JsonWidget jsonWidget = new JsonWidget(commonPropWidget, mainForm);
        assertEquals(properties.commonProp.getName(), jsonWidget.getName());
        commonPropWidget = Widget.widget(properties.commonProp.getForm(Form.MAIN));
        jsonWidget = new JsonWidget(commonPropWidget, mainForm);
        assertEquals(properties.commonProp.getName(), jsonWidget.getName());
    }

    @Test
    public void getWidget() throws Exception {
        Widget commonPropWidget = mainForm.getWidget(properties.commonProp.getName());
        JsonWidget jsonWidget = new JsonWidget(commonPropWidget, mainForm);
        assertEquals(commonPropWidget, jsonWidget.getWidget());
        commonPropWidget = Widget.widget(properties.commonProp.getForm(Form.MAIN));
        jsonWidget = new JsonWidget(commonPropWidget, mainForm);
        assertEquals(commonPropWidget, jsonWidget.getWidget());
    }

}
