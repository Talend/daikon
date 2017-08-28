// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.EnumSet;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.Property.Flags;
import org.talend.daikon.properties.property.PropertyValueEvaluator;
import org.talend.daikon.properties.property.StringProperty;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class PropertyTest {

    @Test
    public void testProperty() {
        Property<String> element = newProperty(null);
        assertNull(element.getName());
        assertEquals(element, element.setName("testName"));
        assertEquals("testName", element.getName());

        // displayName use the name
        assertEquals("property.testName.displayName", element.getDisplayName());
        assertEquals(element, element.setDisplayName("testDisplayName"));
        assertEquals("testDisplayName", element.getDisplayName());

        assertNull(element.getTitle());
        assertEquals(element, element.setTitle("testTitle"));
        assertEquals("testTitle", element.getTitle());

        assertEquals(TypeUtils.toString(String.class), element.getType());

        assertEquals(-1, element.getSize());
        assertTrue(element.isSizeUnbounded());
        assertEquals(element, element.setSize(28));
        assertEquals(28, element.getSize());
        assertFalse(element.isSizeUnbounded());
        assertEquals(element, element.setSize(-1));
        assertTrue(element.isSizeUnbounded());

        assertEquals(0, element.getOccurMinTimes());
        assertFalse(element.isRequired());
        assertEquals(element, element.setOccurMinTimes(33));
        assertEquals(33, element.getOccurMinTimes());
        assertTrue(element.isRequired());

        assertEquals(0, element.getOccurMaxTimes());
        assertEquals(element, element.setOccurMaxTimes(42));
        assertEquals(42, element.getOccurMaxTimes());

        assertEquals(element, element.setOccurMinTimes(0));
        element.setRequired();
        assertTrue(element.isRequired());
        assertEquals(1, element.getOccurMinTimes());
        assertEquals(1, element.getOccurMaxTimes());

        element.setRequired(false);
        assertEquals(0, element.getOccurMinTimes());
        assertEquals(1, element.getOccurMaxTimes());

        assertEquals(0, element.getPrecision());
        assertEquals(element, element.setPrecision(222));
        assertEquals(222, element.getPrecision());

        assertNull(element.getPattern());
        assertEquals(element, element.setPattern("mypattern"));
        assertEquals("mypattern", element.getPattern());

        assertNull(element.getValue());
        element.setValue("mypattern");
        assertEquals("mypattern", element.getValue());

        assertFalse(element.isNullable());
        assertEquals(element, element.setNullable(true));
        assertTrue(element.isNullable());
        assertEquals(element, element.setNullable(false));
        assertFalse(element.isNullable());

        assertEquals("testName", element.toStringIndent(0));
        assertEquals(" testName", element.toStringIndent(1));
        assertEquals("    testName", element.toStringIndent(4));
    }

    @Test
    public void testHiddenForProperties() {
        Property<String> element = newProperty("element");
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        Widget widget = new Widget(element);
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        widget.setHidden(true);
        assertTrue(element.isFlag(Property.Flags.HIDDEN));
        widget.setHidden(false);
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        widget.setHidden();
        assertTrue(element.isFlag(Property.Flags.HIDDEN));
    }

    @Test
    public void testVisibleForProperties() {
        Property<String> element = newProperty("element");
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        Widget widget = new Widget(element);
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        widget.setVisible(false);
        assertTrue(element.isFlag(Property.Flags.HIDDEN));
        widget.setVisible(true);
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        widget.setVisible(false);
        assertTrue(element.isFlag(Property.Flags.HIDDEN));
        widget.setVisible();
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
    }

    @Test
    public void testFlags() {
        Property<String> element = newProperty("element");
        assertFalse(element.isFlag(Property.Flags.ENCRYPT));
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        element.addFlag(Property.Flags.ENCRYPT);
        assertTrue(element.isFlag(Property.Flags.ENCRYPT));
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        element.addFlag(Property.Flags.HIDDEN);
        assertTrue(element.isFlag(Property.Flags.ENCRYPT));
        assertTrue(element.isFlag(Property.Flags.HIDDEN));
        element.removeFlag(Property.Flags.HIDDEN);
        assertTrue(element.isFlag(Property.Flags.ENCRYPT));
        assertFalse(element.isFlag(Property.Flags.HIDDEN));
        element.removeFlag(Property.Flags.ENCRYPT);
        assertFalse(element.isFlag(Property.Flags.ENCRYPT));
        assertFalse(element.isFlag(Property.Flags.HIDDEN));

        String elementStr = JsonWriter.objectToJson(element);
        element = (Property) JsonReader.jsonToJava(elementStr);
        element.addFlag(Property.Flags.HIDDEN);

        element.addFlag(Property.Flags.ENCRYPT);
        assertTrue(element.isFlag(Property.Flags.ENCRYPT));

    }

    @Test
    public void testCopyTaggedValues() {
        Property<String> element = newString("element");
        element.setTaggedValue("foo", "foo1");
        Property<String> element2 = newString("element2");
        element2.setTaggedValue("bar", "bar1");

        assertEquals("foo1", element.getTaggedValue("foo"));
        assertNotEquals("bar1", element.getTaggedValue("bar"));
        element.copyTaggedValues(element2);
        assertEquals("foo1", element.getTaggedValue("foo"));
        assertEquals("bar1", element.getTaggedValue("bar"));
    }

    @Test
    public void testSetPossibleValuesNotNamedNamedThing() {
        StringProperty stringProperty = new StringProperty("foo") {// in order to have i18n related to this class
        };
        stringProperty.setPossibleValues("possible.value");
        assertEquals("possible.value", stringProperty.getPossibleValuesDisplayName("possible.value"));
        stringProperty.setPossibleValues("possible.value.3");
        assertEquals("possible value 3 i18n", stringProperty.getPossibleValuesDisplayName("possible.value.3"));

    }

    @Test
    public void testType() {
        Property foo = newInteger("foo");
        foo.setValue("bar");
        assertEquals("bar", foo.getValue());
    }

    @Test
    public void testEncryptDoNothing() {
        class NotAnExistingType {// left empty on purpose
        }
        Property<NotAnExistingType> foo = newProperty(NotAnExistingType.class, "foo").setFlags(EnumSet.of(Flags.ENCRYPT));
        NotAnExistingType notAnExistingTypeInstance = new NotAnExistingType();
        foo.setValue(notAnExistingTypeInstance);
        assertEquals(notAnExistingTypeInstance, foo.getValue());
        foo.encryptStoredValue(true);
        assertEquals(notAnExistingTypeInstance, foo.getValue());
        foo.encryptStoredValue(false);
        assertEquals(notAnExistingTypeInstance, foo.getValue());
    }

    @Test
    public void testEquals() {
        Property<String> prop1 = newProperty("name");
        prop1.setValue("foo");
        Property<String> prop2 = newProperty("name");
        prop2.setValue("foo");
        Property<String> prop3 = newProperty("name");
        prop3.setValue("bar");
        Property<String> propNull = null;

        /* Reflexive */
        assertThat(prop1.equals(prop1), is(Boolean.TRUE));
        assertThat(prop2.equals(prop2), is(Boolean.TRUE));

        /* Symmetric */
        assertThat(prop1.equals(prop2), is(Boolean.TRUE));
        assertThat(prop2.equals(prop1), is(Boolean.TRUE));

        /* Transitive */
        assertThat(prop1.equals(propNull), is(Boolean.FALSE));
        assertThat(prop2.equals(propNull), is(Boolean.FALSE));

        assertThat(prop1.equals(prop3), is(Boolean.FALSE));

    }

    @Test
    public void testDisplayName() {
        Property<String> prop1 = newProperty("name");
        NamedThing possibleValue = new SimpleNamedThing("col1", "Name");
        prop1.setPossibleValues(possibleValue);

        String s = prop1.getPossibleValuesDisplayName(possibleValue);
        assertThat(s, is("Name"));
    }

    @Test
    public void testDefaultValue() {
        StringProperty prop1 = newString("prop1", "value1");
        assertNull(prop1.getDefaultValue());
        assertNull(prop1.getStringDefaultValue());

        StringProperty prop2 = newString("prop2", "value2", "defaultValue2");
        assertEquals("defaultValue2", prop2.getDefaultValue());
        assertEquals("defaultValue2", prop2.getStringDefaultValue());

        StringProperty prop3 = newString("prop3", "value3");
        prop3.setValueEvaluator(new PropertyValueEvaluator() {

            @Override
            public <T> T evaluate(Property<T> property, Object storedValue) {
                return storedValue == null ? null : (T) "prop3".concat((String) storedValue);
            }
        });
        assertNull(prop3.getDefaultValue());
        assertNull(prop3.getStringDefaultValue());

        StringProperty prop4 = newString("prop4", "value4", "defaultValue4");
        prop4.setValueEvaluator(new PropertyValueEvaluator() {

            @Override
            public <T> T evaluate(Property<T> property, Object storedValue) {
                return storedValue == null ? null : (T) "prop4".concat((String) storedValue);
            }
        });
        assertEquals("prop4defaultValue4", prop4.getDefaultValue());
        assertEquals("prop4defaultValue4", prop4.getStringDefaultValue());
    }
}
