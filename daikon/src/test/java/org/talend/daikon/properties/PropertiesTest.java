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

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.test.PropertiesTestUtils;
import org.talend.daikon.properties.testproperties.PropertiesWithDefinedI18N;
import org.talend.daikon.properties.testproperties.TestProperties;
import org.talend.daikon.properties.testproperties.nestedprop.NestedNestedProperties;
import org.talend.daikon.properties.testproperties.nestedprop.NestedProperties;
import org.talend.daikon.properties.testproperties.nestedprop.inherited.InheritedProperties;
import org.talend.daikon.properties.testproperties.references.MultipleRefProperties;

public class PropertiesTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void testSerializeProp() {
        Properties props = new TestProperties("test").init();
        PropertiesTestUtils.checkSerialize(props, errorCollector);
    }

    @Test
    public void testSerializeValues() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        props.userId.setValue("testUser");
        props.password.setValue("testPassword");
        assertTrue(props.password.getFlags().contains(Property.Flags.ENCRYPT));
        assertTrue(props.password.getFlags().contains(Property.Flags.SUPPRESS_LOGGING));
        NestedProperties nestedProp = (NestedProperties) props.getProperty("nestedProps");
        nestedProp.aGreatProperty.setValue("greatness");
        assertNotNull(nestedProp);
        props = (TestProperties) PropertiesTestUtils.checkSerialize(props, errorCollector);

        // Should be encrypted
        assertFalse(props.toSerialized().contains("testPassword"));

        assertEquals("testUser", props.userId.getStringValue());
        assertEquals("testPassword", props.password.getValue());
        assertEquals("greatness", props.nestedProps.aGreatProperty.getValue());

    }

    @Test
    public void testGetProperty() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        assertEquals("userId", props.getProperty("userId").getName());
        assertEquals("integer", props.getProperty("integer").getName());
        assertEquals("aGreatProperty", props.getProperty("nestedProps.aGreatProperty").getName());
    }

    @Test
    public void testGetValues() {
        Property prop = new Property("");
        // integer
        prop.setValue(1000);
        assertEquals(1000, prop.getIntValue());
        prop.setValue("1000");
        assertEquals(1000, prop.getIntValue());
        prop.setValue(null);
        assertEquals(0, prop.getIntValue());
        // String
        prop.setValue("a String");
        assertEquals("a String", prop.getStringValue());
        prop.setValue(null);
        assertEquals(null, prop.getStringValue());
        // Boolean
        prop.setValue(true);
        assertEquals(true, prop.getBooleanValue());
        prop.setValue(false);
        assertEquals(false, prop.getBooleanValue());
        prop.setValue(null);
        assertEquals(false, prop.getBooleanValue());
        prop.setValue("Any Obj");
        assertEquals(false, prop.getBooleanValue());
    }

    @Test
    public void testFindForm() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        Form main = props.getForm(Form.MAIN);
        assertTrue(main == props.mainForm);
        assertEquals(Form.MAIN, main.getName());
        Form restoreTest = props.getForm("restoreTest");
        assertTrue(restoreTest == props.restoreForm);
        assertEquals("restoreTest", restoreTest.getName());
    }

    @Test
    public void testCopyValues() {
        TestProperties props = (TestProperties) new TestProperties("test1").init();
        props.integer.setValue(1);
        props.userId.setValue("User1");
        ((Property) props.getProperty("nestedProps.aGreatProperty")).setValue("great1");

        TestProperties props2 = (TestProperties) new TestProperties("test2").init();
        props2.copyValuesFrom(props);
        assertEquals(1, ((Property) props2.getProperty("integer")).getIntValue());
        assertEquals("User1", ((Property) props2.getProperty("userId")).getStringValue());
        assertEquals("great1", ((Property) props2.getProperty("nestedProps.aGreatProperty")).getStringValue());
    }

    @Test
    // TDKN-12 copyValues does not work if target has null property
    public void testCopyValues2() {
        TestProperties props = (TestProperties) new TestProperties("test1").init();
        props.integer.setValue(1);
        props.userId.setValue("User1");
        ((Property) props.getProperty("nestedProps.aGreatProperty")).setValue("great1");

        TestProperties props2 = (TestProperties) new TestProperties("test2").init();
        props2.integer = null;
        props2.userId = null;
        props2.copyValuesFrom(props);
        assertEquals(1, ((Property) props2.getProperty("integer")).getIntValue());
        assertEquals("User1", ((Property) props2.getProperty("userId")).getStringValue());
        assertEquals("great1", ((Property) props2.getProperty("nestedProps.aGreatProperty")).getStringValue());
    }

    @Test
    public void testWrongFieldAndPropertyName() {
        TestProperties props = (TestProperties) new TestProperties("test1").init();
        props.setValue("nestedProps.aGreatProperty", "great1");
        assertEquals("great1", ((Property) props.getProperty("nestedProps.aGreatProperty")).getStringValue());
        try {
            props.setValue("nestedProps", "bad");
            fail("did not get expected exception");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testSetValueQualified() {
        TestProperties props = (TestProperties) new TestProperties("test1").init();
        props.setValue("nestedProps.aGreatProperty", "great1");
        assertEquals("great1", ((Property) props.getProperty("nestedProps.aGreatProperty")).getStringValue());
        try {
            props.setValue("nestedProps", "bad");
            fail("did not get expected exception");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testi18NForDirectProperty() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        NamedThing userIdProp = componentProperties.getProperty("userId");
        assertNotNull(userIdProp);
        assertEquals("User Identifier", userIdProp.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testi18NForNestedProperty() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        Properties nestedProp = (Properties) componentProperties.getProperty("nestedProps");
        assertNotNull(nestedProp);
        NamedThing greatProperty = nestedProp.getProperty(NestedProperties.A_GREAT_PROP_NAME);
        assertNotNull(greatProperty);
        assertEquals("A Fanstastic Property", greatProperty.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testi18NForNestedPropertyWithDefinedI18N() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        Properties nestedProp = (Properties) componentProperties.getProperty("nestedProp2");
        assertNotNull(nestedProp);
        NamedThing greatProperty = nestedProp.getProperty(PropertiesWithDefinedI18N.A_GREAT_PROP_NAME2);
        assertNotNull(greatProperty);
        assertEquals("A second Fanstastic Property", greatProperty.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testi18NForInheritedProperty() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        Properties nestedProp = (Properties) componentProperties.getProperty("nestedProp3");
        assertNotNull(nestedProp);
        NamedThing greatProperty = nestedProp.getProperty(NestedProperties.A_GREAT_PROP_NAME);
        assertNotNull(greatProperty);
        assertEquals("A Fanstastic Property", greatProperty.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testGetPropsList() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        List<NamedThing> pList = componentProperties.getProperties();
        assertTrue(pList.get(0) != null);
        assertEquals(14, pList.size());
    }

    @Test
    public void testGetPropsListInherited() {
        Properties componentProperties = new InheritedProperties("test");
        List<NamedThing> pList = componentProperties.getProperties();
        System.out.println(pList);
        assertTrue(pList.get(0) != null);
        assertEquals(4, pList.size());
    }

    @Test
    public void testGetProps() {
        TestProperties componentProperties = (TestProperties) new TestProperties("test").init();
        Form f = componentProperties.getForm(Form.MAIN);
        assertTrue(f.getWidget("userId").isVisible());
    }

    @Test
    public void testGetValuedProperties() {
        TestProperties tProps = (TestProperties) new TestProperties("test").init();
        assertNotNull(tProps.getValuedProperty("date"));
        assertNull(tProps.getValuedProperty("nestedProps"));
        assertNull(tProps.getValuedProperty("foo.nestedProps"));
    }

    @Test
    public void testGetProperties() {
        TestProperties tProps = (TestProperties) new TestProperties("test").init();
        assertNotNull(tProps.getProperties("nestedProps"));
        assertNull(tProps.getProperties("date"));
        assertNull(tProps.getProperties("foo.nestedProps"));
    }

    @Test
    public void testSerialize() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        PropertiesTestUtils.checkSerialize(props, errorCollector);
    }

    @Test
    // TCOMP-73 Form layout not right after properties deserialized
    public void testSerializeRefresh() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        props.suppressDate.setValue(Boolean.TRUE);
        props = (TestProperties) PropertiesTestUtils.checkSerialize(props, errorCollector);
        assertFalse(props.getForm("restoreTest").getWidget("date").isVisible());
    }

    @Test
    public void testPropertyInitializedDuringSetup() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        // check that getValue returns null, cause is not initialized it will throw an NPE.
        assertNull(props.initLater.getValue());
        assertNull(props.nestedInitLater.anotherProp.getValue());
    }

    @Test
    public void testCreatePropertiesForRuntime() {
        TestProperties props = (TestProperties) new TestProperties("test").initForRuntime();
        assertNull(props.initLater.getValue());
        assertNull(props.mainForm);
    }

    @Test
    public void testTaggedValue() {
        Property property = new Property("haha"); //$NON-NLS-1$
        assertNull(property.getTaggedValue("foo"));
        assertNull(property.getTaggedValue("bar"));
        property.setTaggedValue("foo", "fooValue");
        property.setTaggedValue("bar", "barValue");
        assertEquals("fooValue", property.getTaggedValue("foo"));
        assertEquals("barValue", property.getTaggedValue("bar"));
    }

    @Test
    public void testTaggedValuesSerialization() {
        TestProperties props = (TestProperties) new TestProperties("test").initForRuntime();
        assertNull(props.initLater.getTaggedValue("foo"));
        assertNull(props.initLater.getTaggedValue("bar"));
        props.initLater.setTaggedValue("foo", "fooValue");
        props.initLater.setTaggedValue("bar", "barValue");
        String s = props.toSerialized();
        Properties desProp = Properties.fromSerialized(s, Properties.class).properties;
        assertEquals("fooValue", ((Property) desProp.getProperty("initLater")).getTaggedValue("foo"));
        assertEquals("barValue", ((Property) desProp.getProperty("initLater")).getTaggedValue("bar"));
    }

    @Test
    public void testPropertyValueEvaluation() {
        TestProperties props = (TestProperties) new TestProperties("test").initForRuntime();
        props.userId.setValue("java.io.tmpdir");
        assertEquals("java.io.tmpdir", props.userId.getValue());
        props.setValueEvaluator(new PropertyValueEvaluator() {

            @Override
            public Object evaluate(Property property, Object storedValue) {
                return storedValue != null ? System.getProperty((String) storedValue) : null;
            }
        });
        assertEquals(System.getProperty("java.io.tmpdir"), props.userId.getValue());
        String s = props.toSerialized();
        TestProperties desProp = Properties.fromSerialized(s, TestProperties.class).properties;
        assertEquals("java.io.tmpdir", desProp.userId.getValue());
        // check that nested properties has also the evaluator set
        props.nestedInitLater.aGreatProperty.setValue("java.home");
        assertEquals(System.getProperty("java.home"), props.nestedInitLater.aGreatProperty.getValue());
    }

    @Test
    public void testPropertyValueEvaluationWithTaggedValueExample() {
        TestProperties props = (TestProperties) new TestProperties("test").initForRuntime();
        props.userId.setValue("java.io.tmpdir");
        // use tagged value to tell the proprty is a system property.
        props.userId.setTaggedValue("value.language", "sys.prop");
        assertEquals("java.io.tmpdir", props.userId.getValue());
        props.setValueEvaluator(new PropertyValueEvaluator() {

            @Override
            public Object evaluate(Property property, Object storedValue) {
                // if the prop is a system property then evaluate it.
                Object taggedValue = property.getTaggedValue("value.language");
                if (taggedValue != null && ((String) taggedValue).equals("sys.prop")) {
                    return System.getProperty((String) storedValue);
                } else {// otherwise just return the value.
                    return storedValue;
                }
            }
        });
        assertEquals(System.getProperty("java.io.tmpdir"), props.userId.getValue());
        String s = props.toSerialized();
        TestProperties desProp = Properties.fromSerialized(s, TestProperties.class).properties;
        assertEquals("java.io.tmpdir", desProp.userId.getValue());

    }

    @Test
    public void testAfterFormFinish() throws Throwable {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        assertNull(props.getValidationResult());
        PropertiesDynamicMethodHelper.afterFormFinish(props, Form.MAIN, null);
        assertEquals(ValidationResult.Result.ERROR, props.getValidationResult().getStatus());

    }

    @Test
    public void testNoDoubleMainForm() {
        MultipleRefProperties props = (MultipleRefProperties) new MultipleRefProperties("test").init();
        List<Form> forms = props.connection.getForms();
        assertEquals(1, forms.size());
    }

    @Test
    public void testAssignedNewProperties() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        // use a sub class to check is assignement also works with sub classes
        NestedNestedProperties nestedNestedProperties = new NestedNestedProperties("foo") {
        };
        props.assignNestedProperties(nestedNestedProperties);
        assertEquals(nestedNestedProperties, props.nestedProps.nestedProp);
        assertEquals(nestedNestedProperties, props.nestedInitLater.nestedProp);
    }

    // @Test
    public void testJavaListOfNestedProperties() {
        TestProperties props = (TestProperties) new TestProperties("test").init();
        String javaCode = PropertiesTestUtils.generatedNestedComponentCompatibilitiesJavaCode(props);
        System.out.println(javaCode);
    }

}
