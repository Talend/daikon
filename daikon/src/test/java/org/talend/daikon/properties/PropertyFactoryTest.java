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
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;
import org.talend.daikon.properties.Property.Type;

public class PropertyFactoryTest {

    @Test
    public void testNewProperty() {
        Property element = PropertyFactory.newProperty("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Property.Type.STRING, element.getType());
    }

    @Test
    public void testNewProperty_WithTtitle() {
        Property element = PropertyFactory.newProperty("testProperty", "title");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertEquals("title", element.getTitle());
        assertEquals(Property.Type.STRING, element.getType());

    }

    @Test
    public void testNewProperty_WithTypeAndTitle() {
        Property element = PropertyFactory.newProperty(Property.Type.BOOLEAN, "testProperty", "title");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertEquals("title", element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
    }

    @Test
    public void testNewProperty_WithType() {
        Property element = PropertyFactory.newProperty(Type.BOOLEAN, "testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
    }

    @Test
    public void testNewString() {
        Property element = PropertyFactory.newString("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.STRING, element.getType());
    }

    @Test
    public void testNewInteger() {
        Property element = PropertyFactory.newInteger("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.INT, element.getType());
    }

    @Test
    public void testNewInteger_defaultvalueString() {
        Property element = PropertyFactory.newInteger("testProperty", "10");
        assertEquals("testProperty", element.getName());
        assertEquals("10", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.INT, element.getType());
    }

    @Test
    public void testNewInteger_defaultvalueInteger() {
        Property element = PropertyFactory.newInteger("testProperty", 10);
        assertEquals("testProperty", element.getName());
        assertEquals("10", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.INT, element.getType());
    }

    @Test
    public void testNewFloat() {
        Property element = PropertyFactory.newFloat("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.FLOAT, element.getType());
    }

    @Test
    public void testNewFloat_defaultvalue() {
        Property element = PropertyFactory.newFloat("testProperty", 5f);
        assertEquals("testProperty", element.getName());
        assertEquals("5.0", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.FLOAT, element.getType());
    }

    @Test
    public void testNewFloat_StringDefaultvalue() {
        Property element = PropertyFactory.newFloat("testProperty", "5f");
        assertEquals("testProperty", element.getName());
        assertEquals("5f", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.FLOAT, element.getType());
    }

    @Test
    public void testNewDouble() {
        Property element = PropertyFactory.newDouble("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.DOUBLE, element.getType());
    }

    @Test
    public void testNewDouble_defaultvalue() {
        Property element = PropertyFactory.newDouble("testProperty", 5d);
        assertEquals("testProperty", element.getName());
        assertEquals("5.0", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.DOUBLE, element.getType());
    }

    @Test
    public void testNewDouble_StringDefaultvalue() {
        Property element = PropertyFactory.newDouble("testProperty", "5f");
        assertEquals("testProperty", element.getName());
        assertEquals("5f", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.DOUBLE, element.getType());
    }

    @Test
    public void testNewBoolean() {
        Property element = PropertyFactory.newBoolean("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
    }

    @Test
    public void testNewBoolean_withDefault() {
        Property element = PropertyFactory.newBoolean("testProperty", true);
        assertEquals("testProperty", element.getName());
        assertEquals("true", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
        element = PropertyFactory.newBoolean("testProperty", false);
        assertEquals("testProperty", element.getName());
        assertEquals("false", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());

    }

    @Test
    public void testNewBoolean_withStringDefault() {
        Property element = PropertyFactory.newBoolean("testProperty", "true");
        assertEquals("testProperty", element.getName());
        assertEquals("true", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
        element = PropertyFactory.newBoolean("testProperty", "false");
        assertEquals("testProperty", element.getName());
        assertEquals("false", element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.BOOLEAN, element.getType());
    }

    @Test
    public void testNewDate() {
        Property element = PropertyFactory.newDate("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.DATE, element.getType());
    }

    @Test
    public void testNewEnumString() {
        Property element = PropertyFactory.newEnum("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getPossibleValues());
        assertNull(element.getTitle());
        assertEquals(Type.ENUM, element.getType());
    }

    @Test
    public void testNewEnum_withvalue() {
        Property element = PropertyFactory.newEnum("testProperty", "value1", "value2", "value3");
        assertEquals("testProperty", element.getName());
        assertNull(element.getDefaultValue());
        assertEquals(Arrays.asList("value1", "value2", "value3"), element.getPossibleValues());
        assertNull(element.getTitle());
        assertEquals(Type.ENUM, element.getType());
    }

}
