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

import static org.junit.Assert.*;
import static org.talend.daikon.properties.PropertyFactory.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Widget;

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
    public void testChildren() {
        Property<String> element = newProperty("element");
        Property<String> child = newProperty("myElement");
        assertNotNull(element.addChild(child).getChild("myElement"));
        assertEquals("myElement", element.getChild("myElement").getName());

        List<Property<?>> children = element.getChildren();
        assertEquals(1, children.size());
        assertEquals("myElement", children.get(0).getName());

        children.add(newProperty("myElement2"));
        element.setChildren(children);
        assertEquals("myElement", element.getChild("myElement").getName());
        assertEquals("myElement2", element.getChild("myElement2").getName());

        Map<String, Property<?>> childrenMap = element.getChildMap();
        assertEquals(2, childrenMap.size());
        assertEquals("myElement", childrenMap.get("myElement").getName());
        assertEquals("myElement2", childrenMap.get("myElement2").getName());
        childrenMap.put("myElement3", newProperty("myElement3"));
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
        Property<String> element = new Property<>(String.class, "element");
        element.setTaggedValue("foo", "foo1");
        Property<String> element2 = new Property<>(String.class, "element2");
        element2.setTaggedValue("bar", "bar1");

        assertEquals("foo1", element.getTaggedValue("foo"));
        assertNotEquals("bar1", element.getTaggedValue("bar"));
        element.copyTaggedValues(element2);
        assertEquals("foo1", element.getTaggedValue("foo"));
        assertEquals("bar1", element.getTaggedValue("bar"));
    }

    @Test
    public void testType() {
        Property foo = new Property<Integer>(Integer.class, "foo");
        foo.setValue("bar");
        assertEquals("bar", foo.getValue());
    }

}
