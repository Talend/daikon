package org.talend.daikon.properties.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.testproperties.TestProperties;

public class WidgetTest {

    @Test
    public void testConfigurationValues() {
        Widget widget = widget(newString("w1"));
        assertNull(widget.getConfigurationValue("foo"));
        assertNull(widget.getConfigurationValue("bar"));
        widget.setConfigurationValue("foo", "fooValue");
        widget.setConfigurationValue("bar", "barValue");
        assertEquals("fooValue", widget.getConfigurationValue("foo"));
        assertEquals("barValue", widget.getConfigurationValue("bar"));
    }

    @Test
    public void testConfigurationValuesInPropertiesSerialization() {
        TestProperties props = (WidgetTestProperties) new WidgetTestProperties("props").init();
        assertEquals(true,
                props.getForm(Form.MAIN).getWidget("confProperty").getConfigurationValue(Widget.READ_ONLY_WIDGET_CONF));
        assertEquals(true,
                props.getForm(Form.MAIN).getWidget("confProperty").getConfigurationValue(Widget.AUTO_FOCUS_WIDGET_CONF));

        TestProperties desProps = Properties.Helper.fromSerializedPersistent(props.toSerialized(), TestProperties.class).object;
        assertEquals(true,
                desProps.getForm(Form.MAIN).getWidget("confProperty").getConfigurationValue(Widget.READ_ONLY_WIDGET_CONF));
        assertEquals(true,
                desProps.getForm(Form.MAIN).getWidget("confProperty").getConfigurationValue(Widget.AUTO_FOCUS_WIDGET_CONF));

    }

    @Test
    public void testReadonly() {
        Widget widget = widget(newString("w1"));
        assertFalse(widget.isReadonly());
        widget.setConfigurationValue(Widget.READ_ONLY_WIDGET_CONF, true);
        assertTrue(widget.isReadonly());
        widget.setConfigurationValue(Widget.READ_ONLY_WIDGET_CONF, false);
        assertFalse(widget.isReadonly());
    }

    @Test
    public void testAutoFocus() {
        Widget widget = widget(newString("w1"));
        assertFalse(widget.isAutoFocus());
        widget.setConfigurationValue(Widget.AUTO_FOCUS_WIDGET_CONF, true);
        assertTrue(widget.isAutoFocus());
        widget.setConfigurationValue(Widget.AUTO_FOCUS_WIDGET_CONF, false);
        assertFalse(widget.isAutoFocus());
    }

    class WidgetTestProperties extends TestProperties {

        public Property<String> confProperty = newProperty("confProperty");

        public WidgetTestProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            getForm(Form.MAIN).addRow(widget(confProperty).setConfigurationValue(Widget.READ_ONLY_WIDGET_CONF, true)
                    .setConfigurationValue(Widget.AUTO_FOCUS_WIDGET_CONF, true));
        }
    }

}
