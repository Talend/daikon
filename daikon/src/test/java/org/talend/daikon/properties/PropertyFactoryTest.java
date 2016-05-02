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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

public class PropertyFactoryTest {

    @Test
    public void testNewProperty() {
        Property<String> element = PropertyFactory.newProperty("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(String.class, element.getType());
    }

    @Test
    public void testNewProperty_WithTtitle() {
        Property<String> element = PropertyFactory.newProperty("testProperty", "title");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertEquals("title", element.getTitle());
        assertEquals(String.class, element.getType());

    }

    @Test
    public void testNewProperty_WithTypeAndTitle() {
        Property<Boolean> element = PropertyFactory.newProperty(Boolean.class, "testProperty", "title");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertEquals("title", element.getTitle());
        assertEquals(Boolean.class, element.getType());
    }

    @Test
    public void testNewProperty_WithType() {
        Property<Boolean> element = PropertyFactory.newProperty(Boolean.class, "testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());
    }

    @Test
    public void testNewString() {
        Property<String> element = PropertyFactory.newString("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(String.class, element.getType());
    }

    @Test
    public void testNewInteger() {
        Property<Integer> element = PropertyFactory.newInteger("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Integer.class, element.getType());
    }

    @Test
    public void testNewInteger_defaultvalueString() {
        Property<Integer> element = PropertyFactory.newInteger("testProperty", "10");
        assertEquals("testProperty", element.getName());
        assertEquals((Integer) 10, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Integer.class, element.getType());
    }

    @Test
    public void testNewInteger_defaultvalueInteger() {
        Property<Integer> element = PropertyFactory.newInteger("testProperty", 10);
        assertEquals("testProperty", element.getName());
        assertEquals((Integer) 10, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Integer.class, element.getType());
    }

    @Test
    public void testNewFloat() {
        Property<Float> element = PropertyFactory.newFloat("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Float.class, element.getType());
    }

    @Test
    public void testNewFloat_defaultvalue() {
        Property<Float> element = PropertyFactory.newFloat("testProperty", 5f);
        assertEquals("testProperty", element.getName());
        assertEquals((Float) 5f, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Float.class, element.getType());
    }

    @Test
    public void testNewFloat_StringDefaultvalue() {
        Property<Float> element = PropertyFactory.newFloat("testProperty", "5f");
        assertEquals("testProperty", element.getName());
        assertEquals((Float) 5f, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Float.class, element.getType());
    }

    @Test
    public void testNewDouble() {
        Property<Double> element = PropertyFactory.newDouble("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Double.class, element.getType());
    }

    @Test
    public void testNewDouble_defaultvalue() {
        Property<Double> element = PropertyFactory.newDouble("testProperty", 5d);
        assertEquals("testProperty", element.getName());
        assertEquals((Double) 5.0, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Double.class, element.getType());
    }

    @Test
    public void testNewDouble_StringDefaultvalue() {
        Property<Double> element = PropertyFactory.newDouble("testProperty", "5f");
        assertEquals("testProperty", element.getName());
        assertEquals((Double) 5.0, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Double.class, element.getType());
    }

    @Test
    public void testNewBoolean() {
        Property<Boolean> element = PropertyFactory.newBoolean("testProperty");
        assertEquals("testProperty", element.getName());
        assertFalse(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());
    }

    @Test
    public void testNewBoolean_withDefault() {
        Property<Boolean> element = PropertyFactory.newBoolean("testProperty", true);
        assertEquals("testProperty", element.getName());
        assertEquals(true, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());
        element = PropertyFactory.newBoolean("testProperty", false);
        assertEquals("testProperty", element.getName());
        assertEquals(false, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());

    }

    @Test
    public void testNewBoolean_withStringDefault() {
        Property<Boolean> element = PropertyFactory.newBoolean("testProperty", "true");
        assertEquals("testProperty", element.getName());
        assertEquals(true, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());
        element = PropertyFactory.newBoolean("testProperty", "false");
        assertEquals("testProperty", element.getName());
        assertEquals(false, element.getValue());
        assertNull(element.getTitle());
        assertEquals(Boolean.class, element.getType());
    }

    @Test
    public void testNewDate() {
        Property<Date> element = PropertyFactory.newDate("testProperty");
        assertEquals("testProperty", element.getName());
        assertNull(element.getValue());
        assertNull(element.getTitle());
        assertEquals(Date.class, element.getType());
    }

    enum Foo {
        foo,
        bar,
        foobar;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNewEnum() {
        Property<Foo> element = PropertyFactory.newEnum("testProperty", Foo.class);
        assertEquals("testProperty", element.getName());
        assertThat((List<Foo>) element.getPossibleValues(), contains(Foo.foo, Foo.bar, Foo.foobar));
        assertNull(element.getTitle());
        assertEquals(Foo.class, element.getType());
    }

}
