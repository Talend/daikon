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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * created by pbailly on 5 Nov 2015 Detailled comment
 *
 */
public class PropertyTest {

    /**
     * Simple test to be sure that no one will touch this list without thinking about consequences. If you remove/add a
     * type here, be sure that it does not break everything.
     */
    @Test
    public void testEnum() {
        List<String> ref = Arrays.asList("STRING", "BOOLEAN", "INT", "DATE", "DATETIME", "DECIMAL", "FLOAT", "DOUBLE",
                "BYTE_ARRAY", "ENUM", "DYNAMIC", "GROUP", "SCHEMA");
        List<Property.Type> types = Arrays.asList(Property.Type.values());
        assertEquals(ref.size(), types.size());
        assertEquals(ref.toString(), types.toString());
    }

    @Test
    public void testProperty() {
        Property element = new Property(null);
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

        assertEquals(Property.Type.STRING, element.getType());
        assertEquals(element, element.setType(Property.Type.BYTE_ARRAY));
        assertEquals(Property.Type.BYTE_ARRAY, element.getType());

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

        assertNull(element.getDefaultValue());
        assertEquals(element, element.setDefaultValue("mypattern"));
        assertEquals("mypattern", element.getDefaultValue());

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
        Property element = new Property("element");
        Property child = new Property("myElement");
        assertNotNull(element.addChild(child).getChild("myElement"));
        assertEquals("myElement", element.getChild("myElement").getName());

        List<Property> children = element.getChildren();
        assertEquals(1, children.size());
        assertEquals("myElement", children.get(0).getName());

        children.add(new Property("myElement2"));
        element.setChildren(children);
        assertEquals("myElement", element.getChild("myElement").getName());
        assertEquals("myElement2", element.getChild("myElement2").getName());

        Map<String, Property> childrenMap = element.getChildMap();
        assertEquals(2, childrenMap.size());
        assertEquals("myElement", childrenMap.get("myElement").getName());
        assertEquals("myElement2", childrenMap.get("myElement2").getName());
        childrenMap.put("myElement3", new Property("myElement3"));
    }

}
