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

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.testproperties.TestProperties;
import org.talend.daikon.properties.Property;

public class PropertiesServiceTest {

    PropertiesService<Properties> propService;

    @Before
    public void init() {
        propService = new PropertiesServiceImpl();
    }

    @Test
    public void testBefore() throws Throwable {
        Properties props = new TestProperties(null).init();

        checkAndBeforePresent(propService, props.getForm(Form.MAIN), "nameList", props);
        assertEquals(3, ((Property) props.getProperty("nameList")).getPossibleValues().size());
        assertEquals("name1", ((Property) props.getProperty("nameList")).getPossibleValues().get(0));

        checkAndBeforeActivate(propService, props.getForm(Form.MAIN), "nameListRef", props);
        assertEquals(3, ((Property) props.getProperty("nameListRef")).getPossibleValues().size());
        assertEquals("namer1", ((Property) props.getProperty("nameListRef")).getPossibleValues().get(0));

        assertFalse(props.getForm(Form.MAIN).getWidget("nameList").isCallBeforeActivate());
        assertFalse(props.getForm(Form.MAIN).getWidget("nameListRef").isCallBeforePresent());
    }

    @Test
    public void testBeforePresentWithValidationResults() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        checkAndBeforePresent(propService, props.getForm(Form.MAIN), "nameList", props);
        assertNotNull(props.getValidationResult());
    }

    @Test
    public void testAfterPresentWithValidationResultsWarning() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        checkAndAfter(propService, props.getForm("restoreTest"), "integer", props);
        assertEquals(ValidationResult.Result.WARNING, props.getValidationResult().getStatus());
    }

    @Test
    public void testBeforeActivateWithDefaultValidationResults() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        checkAndBeforeActivate(propService, props.getForm(Form.MAIN), "nameListRef", props);
        assertNotNull(props.getValidationResult());
    }

    @Test
    // TCOMP-15 Handle OK/Cancel button on advanced properties dialog from Wizard
    // TCOMP-51 Make sure current values in the form are available until canceled
    public void testFormOkCancel() throws Throwable {
        TestProperties props = (TestProperties) new TestProperties(null).init();

        Properties savedProps = props;
        Properties savedNested = props.nestedProps;

        Date dateNow = new Date();
        dateNow.setTime(System.currentTimeMillis());
        Date dateLater = new Date();
        dateLater.setTime(dateLater.getTime() + 10000);

        Calendar calNow = Calendar.getInstance();
        calNow.setTime(dateNow);
        Calendar calLater = Calendar.getInstance();
        calLater.setTime(dateLater);

        props.userId.setValue("userId");
        props.integer.setValue(1);
        props.decimal.setValue(2);
        props.date.setValue(dateNow);
        props.dateTime.setValue(dateNow);
        props.nestedProps.aGreatProperty.setValue("propPrevious1");
        props.nestedProps.anotherProp.setValue("propPrevious2");

        props = (TestProperties) propService.makeFormCancelable(props, "restoreTest");

        Form form = props.getForm("restoreTest");

        form.setValue("userId", "userIdnew");
        form.getChildForm("nestedProps").setValue("aGreatProperty", "propPrevious1new");

        form.setValue("integer", 10);
        form.setValue("decimal", 20);
        form.setValue("date", dateLater);
        form.setValue("dateTime", dateLater);

        assertEquals("userIdnew", props.userId.getValue());
        assertEquals("propPrevious1new", props.nestedProps.aGreatProperty.getValue());
        assertEquals(10, props.integer.getIntValue());
        assertEquals(20, props.decimal.getIntValue());
        assertEquals(calLater, props.date.getCalendarValue());
        assertEquals(calLater, props.dateTime.getCalendarValue());

        assertTrue(props == savedProps);
        assertTrue(props.nestedProps == savedNested);

        props = (TestProperties) propService.cancelFormValues(props, "restoreTest");

        assertEquals("userId", props.userId.getValue());
        assertEquals("propPrevious1", props.nestedProps.aGreatProperty.getValue());
        assertEquals(1, props.integer.getIntValue());
        assertEquals(2, props.decimal.getIntValue());
        assertEquals(calNow, props.date.getCalendarValue());
        assertEquals(calNow, props.dateTime.getCalendarValue());
    }

    @Test
    public void testAfterFormFinish() throws Throwable {
        Properties props = new TestProperties(null).init();
        assertNull(props.getValidationResult());
        propService.afterFormFinish(Form.MAIN, props);
        assertNotNull(props.getValidationResult());
        assertEquals(Result.ERROR, props.getValidationResult().getStatus());
    }

    public static Properties checkAndBeforeActivate(PropertiesService propServ, Form form, String propName, Properties props)
            throws Throwable {
        assertTrue(form.getWidget(propName).isCallBeforeActivate());
        return propServ.beforePropertyActivate(propName, props);
    }

    public static Properties checkAndBeforePresent(PropertiesService propServ, Form form, String propName, Properties props)
            throws Throwable {
        assertTrue(form.getWidget(propName).isCallBeforePresent());
        return propServ.beforePropertyPresent(propName, props);
    }

    public static Properties checkAndAfter(PropertiesService propServ, Form form, String propName, Properties props)
            throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        return propServ.afterProperty(propName, props);
    }

    public static Properties checkAndValidate(PropertiesService propServ, Form form, String propName, Properties props)
            throws Throwable {
        assertTrue(form.getWidget(propName).isCallValidate());
        return propServ.validateProperty(propName, props);
    }

}
