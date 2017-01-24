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
package org.talend.daikon.properties.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.test.PropertiesTestUtils;
import org.talend.daikon.properties.testproperties.TestProperties;

public class PropertiesServiceTest {

    private PropertiesService<Properties> propService;

    @Before
    public void init() {
        propService = new PropertiesServiceImpl();
    }

    @Test
    public void testBefore() throws Throwable {
        Properties props = new TestProperties(null).init();

        PropertiesTestUtils.checkAndBeforePresent(propService, props.getForm(Form.MAIN), "nameList", props);
        assertEquals(3, ((Property<?>) props.getProperty("nameList")).getPossibleValues().size());
        assertEquals("name1", ((Property<?>) props.getProperty("nameList")).getPossibleValues().get(0));

        PropertiesTestUtils.checkAndBeforeActivate(propService, props.getForm(Form.MAIN), "nameListRef", props);
        assertEquals(3, ((Property<?>) props.getProperty("nameListRef")).getPossibleValues().size());
        assertEquals("namer1", ((Property<?>) props.getProperty("nameListRef")).getPossibleValues().get(0));

        assertFalse(props.getForm(Form.MAIN).getWidget("nameList").isCallBeforeActivate());
        assertFalse(props.getForm(Form.MAIN).getWidget("nameListRef").isCallBeforePresent());
    }

    @Test
    public void testBeforePresentWithValidationResults() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        PropertiesTestUtils.checkAndBeforePresent(propService, props.getForm(Form.MAIN), "nameList", props);
        assertNotNull(props.getValidationResult());
    }

    @Test
    public void testAfterPresentWithValidationResultsWarning() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        PropertiesTestUtils.checkAndAfter(propService, props.getForm("restoreTest"), "integer", props);
        assertEquals(ValidationResult.Result.WARNING, props.getValidationResult().getStatus());
    }

    @Test
    public void testBeforeActivateWithDefaultValidationResults() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        PropertiesTestUtils.checkAndBeforeActivate(propService, props.getForm(Form.MAIN), "nameListRef", props);
        assertNotNull(props.getValidationResult());
    }

    @Test
    // TCOMP-15 Handle OK/Cancel button on advanced properties dialog from Wizard
    // TCOMP-51 Make sure current values in the form are available until canceled
    public void testFormOkCancel() throws Throwable {
        TestProperties props = (TestProperties) new TestProperties(null).init();

        Properties savedProps = props;
        Properties savedNested = props.nestedProps;

        props.userId.setValue("userId");
        props.integer.setValue(1);
        props.decimal.setValue(2);
        props.nestedProps.aGreatProperty.setValue("propPrevious1");
        props.nestedProps.anotherProp.setValue("foo");

        props = (TestProperties) propService.makeFormCancelable(props, "restoreTest");

        Form form = props.getForm("restoreTest");

        form.setValue("userId", "userIdnew");
        form.getChildForm("nestedProps").setValue("aGreatProperty", "propPrevious1new");

        form.setValue("integer", 10);
        form.setValue("decimal", 20);

        assertEquals("userIdnew", props.userId.getValue());
        assertEquals("propPrevious1new", props.nestedProps.aGreatProperty.getValue());
        assertEquals((Integer) 10, props.integer.getValue());
        assertEquals((Integer) 20, props.decimal.getValue());

        assertEquals(savedProps, props);
        assertEquals(savedNested, props.nestedProps);

        props = (TestProperties) propService.cancelFormValues(props, "restoreTest");

        assertEquals("userId", props.userId.getValue());
        assertEquals("propPrevious1", props.nestedProps.aGreatProperty.getValue());
        assertEquals((Integer) 1, props.integer.getValue());
        assertEquals((Integer) 2, props.decimal.getValue());
    }

    @Test
    public void testAfterFormFinish() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        propService.afterFormFinish(Form.MAIN, props);
        assertNotNull(props.getValidationResult());
        assertEquals(Result.ERROR, props.getValidationResult().getStatus());
    }

    @Test
    public void testAfterFormFinishOverridden() throws Throwable {
        final AtomicBoolean afterFormFinishCalled = new AtomicBoolean();
        Properties props = new TestProperties(null) {

            @Override
            public ValidationResult afterFormFinishMain(org.talend.daikon.properties.service.Repository<Properties> repo) {
                afterFormFinishCalled.set(true);
                return null;
            }
        }.init();
        assertFalse(afterFormFinishCalled.get());
        propService.afterFormFinish(Form.MAIN, props);
        assertTrue(afterFormFinishCalled.get());
    }

    @Test
    public void testAfterMethodOverridden() throws Throwable {
        final AtomicBoolean afterUserIdCalled = new AtomicBoolean();
        TestProperties testProps = (TestProperties) new TestProperties("testProps") {

            public void afterUserId() {
                afterUserIdCalled.set(true);
            }

        }.init();
        assertFalse(afterUserIdCalled.get());
        PropertiesTestUtils.checkAndAfter(propService, testProps.getForm(Form.MAIN), testProps.userId.getName(), testProps);
        assertTrue(afterUserIdCalled.get());
    }

    @Test
    public void validatePropertyOverriden() throws Throwable {
        final AtomicBoolean validateUserIdCalled = new AtomicBoolean();
        TestProperties testProps = (TestProperties) new TestProperties("testProps") {

            public void validateUserId() {
                validateUserIdCalled.set(true);
            }

        }.init();
        assertFalse(validateUserIdCalled.get());
        PropertiesTestUtils.checkAndValidate(propService, testProps.getForm(Form.MAIN), testProps.userId.getName(), testProps);
        assertTrue(validateUserIdCalled.get());
    }

}
