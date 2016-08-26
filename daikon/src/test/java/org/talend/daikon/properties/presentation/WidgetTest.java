package org.talend.daikon.properties.presentation;

import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.testproperties.TestProperties;

import static org.junit.Assert.*;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

/**
 * Created by ycbai on 2016/8/26.
 */
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
        assertEquals(true, props.getForm(Form.MAIN).getWidget("readonlyProperty").getConfigurationValue(Widget.READ_ONLY));
        TestProperties desProps = Properties.Helper.fromSerializedPersistent(props.toSerialized(), TestProperties.class).object;
        assertEquals(true, desProps.getForm(Form.MAIN).getWidget("readonlyProperty").getConfigurationValue(Widget.READ_ONLY));
    }

    @Test
    public void testReadonly() {
        Widget widget = widget(newString("w1"));
        assertFalse(widget.isReadonly());
        widget.setConfigurationValue(Widget.READ_ONLY, true);
        assertTrue(widget.isReadonly());
        widget.setConfigurationValue(Widget.READ_ONLY, false);
        assertFalse(widget.isReadonly());
    }

    class WidgetTestProperties extends TestProperties {

        public Property<String> readonlyProperty = newProperty("readonlyProperty");

        public WidgetTestProperties(String name) {
            super(name);
        }
        @Override
        public void setupLayout() {
            super.setupLayout();
            getForm(Form.MAIN).addRow(widget(readonlyProperty).setConfigurationValue(Widget.READ_ONLY, true));
        }
    }

}
