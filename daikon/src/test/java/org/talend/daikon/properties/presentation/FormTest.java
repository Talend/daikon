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
package org.talend.daikon.properties.presentation;

import static org.junit.Assert.*;
import static org.talend.daikon.properties.presentation.Widget.*;
import static org.talend.daikon.properties.property.PropertyFactory.*;

import org.junit.Test;
import org.talend.daikon.properties.PropertiesImpl;

public class FormTest {

    @Test
    public void testNameEqualsDisplayNameWithUsualConstructor() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        assertEquals("foo", form.getName()); //$NON-NLS-1$
        assertEquals("foo", form.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testGetI18NFields() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo", null); //$NON-NLS-1$
        assertEquals("Ze Form DisplayName", form.getDisplayName()); //$NON-NLS-1$
        assertEquals("Ze Form Title", form.getTitle()); //$NON-NLS-1$
        assertEquals("Ze Form SubTitle", form.getSubtitle()); //$NON-NLS-1$
    }

    @Test
    public void testGetI18NFiledsWithDefaultValue() {
        String displayName = "Default Display Name"; //$NON-NLS-1$
        String subTitle = "Default SubTitle"; //$NON-NLS-1$
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo", displayName); //$NON-NLS-1$
        form.setSubtitle(subTitle);
        assertEquals(displayName, form.getDisplayName());
        assertEquals("Ze Form Title", form.getTitle());
        assertEquals(subTitle, form.getSubtitle());
    }

    @Test
    public void testSetVisible() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        form.addRow(widget(newString("w1")));
        form.addRow(widget(newString("w2")));
        form.addRow(widget(newString("w3")));
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(form.getWidget("w2").isHidden());
        assertFalse(form.getWidget("w3").isHidden());
        form.setHidden(true);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(form.getWidget("w2").isHidden());
        assertTrue(form.getWidget("w3").isHidden());
        form.setHidden(false);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(form.getWidget("w2").isHidden());
        assertFalse(form.getWidget("w3").isHidden());
    }

    @Test
    public void testSetVisibleForNestedForms() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        Form nestedForm = new Form(new PropertiesImpl("foo") { //$NON-NLS-1$
        }, "bar"); //$NON-NLS-1$
        form.addRow(widget(newString("w1")));
        form.addRow(widget(nestedForm));
        nestedForm.addRow(widget(newString("w3")));
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(nestedForm.getWidget("w3").isHidden());
        form.setHidden(true);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(nestedForm.getWidget("w3").isHidden());
        form.setHidden(false);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(nestedForm.getWidget("w3").isHidden());
    }

}
