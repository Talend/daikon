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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.junit.Test;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;

public class FormTest {

    @Test
    public void testNameEqualsDisplayNameWithUsualConstructor() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        assertEquals("foo", form.getName()); //$NON-NLS-1$
        assertEquals("Ze Form DisplayName", form.getDisplayName()); //$NON-NLS-1$
    }

    @Test
    public void testGetI18NFields() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        assertEquals("Ze Form Title", form.getTitle()); //$NON-NLS-1$
        assertEquals("Ze Form SubTitle", form.getSubtitle()); //$NON-NLS-1$
    }

    @Test
    public void testGetI18NFiledsWithDefaultValue() {
        String subTitle = "Default SubTitle"; //$NON-NLS-1$
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        form.setSubtitle(subTitle);
        assertEquals("Ze Form Title", form.getTitle());
        assertEquals(subTitle, form.getSubtitle());
    }

    @Test
    public void testGetWidget() {
        Property<String> w1 = newString("w1");
        Property<String> w2 = newString("w2");
        Property<String> w3 = newString("w3");
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        form.addRow(widget(w1));
        form.addRow(widget(w2));
        form.addRow(widget(w3));

        assertEquals(w1, form.getWidget("w1").getContent());
        assertEquals(w2, form.getWidget("w2").getContent());
        assertEquals(w3, form.getWidget("w3").getContent());

        assertEquals(w1, form.getWidget(w1.getName()).getContent());
        assertEquals(w2, form.getWidget(w2.getName()).getContent());
        assertEquals(w3, form.getWidget(w3.getName()).getContent());

        assertEquals(w1, form.getWidget(w1).getContent());
        assertEquals(w2, form.getWidget(w2).getContent());
        assertEquals(w3, form.getWidget(w3).getContent());
    }

    @Test
    public void testSetHidden() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        form.addRow(widget(newString("w1")));
        form.addRow(widget(newString("w2")));
        form.addRow(widget(newString("w3")));
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(form.getWidget("w2").isHidden());
        assertFalse(form.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(form.getWidget("w2").isVisible());
        assertTrue(form.getWidget("w3").isVisible());
        form.setHidden(true);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(form.getWidget("w2").isHidden());
        assertTrue(form.getWidget("w3").isHidden());
        assertFalse(form.getWidget("w1").isVisible());
        assertFalse(form.getWidget("w2").isVisible());
        assertFalse(form.getWidget("w3").isVisible());
        form.setHidden(false);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(form.getWidget("w2").isHidden());
        assertFalse(form.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(form.getWidget("w2").isVisible());
        assertTrue(form.getWidget("w3").isVisible());
    }

    @Test
    public void testSetHiddenForNestedForms() {
        Form form = new Form(new PropertiesImpl("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        Form nestedForm = new Form(new PropertiesImpl("foo") { //$NON-NLS-1$
        }, "bar"); //$NON-NLS-1$
        form.addRow(widget(newString("w1")));
        form.addRow(widget(nestedForm));
        nestedForm.addRow(widget(newString("w3")));
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(nestedForm.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(nestedForm.getWidget("w3").isVisible());
        form.setHidden(true);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(nestedForm.getWidget("w3").isHidden());
        assertFalse(form.getWidget("w1").isVisible());
        assertFalse(nestedForm.getWidget("w3").isVisible());
        form.setHidden(false);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(nestedForm.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(nestedForm.getWidget("w3").isVisible());
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
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(form.getWidget("w2").isVisible());
        assertTrue(form.getWidget("w3").isVisible());
        form.setVisible(false);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(form.getWidget("w2").isHidden());
        assertTrue(form.getWidget("w3").isHidden());
        assertFalse(form.getWidget("w1").isVisible());
        assertFalse(form.getWidget("w2").isVisible());
        assertFalse(form.getWidget("w3").isVisible());
        form.setVisible(true);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(form.getWidget("w2").isHidden());
        assertFalse(form.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(form.getWidget("w2").isVisible());
        assertTrue(form.getWidget("w3").isVisible());
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
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(nestedForm.getWidget("w3").isVisible());
        form.setVisible(false);
        assertTrue(form.getWidget("w1").isHidden());
        assertTrue(nestedForm.getWidget("w3").isHidden());
        assertFalse(form.getWidget("w1").isVisible());
        assertFalse(nestedForm.getWidget("w3").isVisible());
        form.setVisible(true);
        assertFalse(form.getWidget("w1").isHidden());
        assertFalse(nestedForm.getWidget("w3").isHidden());
        assertTrue(form.getWidget("w1").isVisible());
        assertTrue(nestedForm.getWidget("w3").isVisible());
    }

    @Test
    public void testReplaceWidgetOnForm() {
        Form form = new Form(new PropertiesWithReplaceables("bar") { //$NON-NLS-1$
        }, "foo"); //$NON-NLS-1$
        Form nestedForm = new Form(new PropertiesImpl("foo") { //$NON-NLS-1$
        }, "bar"); //$NON-NLS-1$
        form.addRow(widget(newString("w1")));
        form.addColumn(widget(newString("w2")));
        form.addRow(widget(nestedForm));
        assertEquals(form.getWidget("w2").getRow(), 1);
        assertEquals(form.getWidget("w2").getOrder(), 2);

        Widget newWidget = widget(newString("w3"));
        form.replaceRow("w2", newWidget);
        assertTrue(form.getWidget("w2") == null);
        assertEquals(form.getWidget("w3").getRow(), 1);
        assertEquals(form.getWidget("w3").getOrder(), 2);

        assertTrue(newWidget.isCallAfter());
        assertTrue(newWidget.isCallBeforePresent());
        assertFalse(newWidget.isCallBeforeActivate());
        assertFalse(newWidget.isCallValidate());
    }

    private static class PropertiesWithReplaceables extends PropertiesImpl {

        public PropertiesWithReplaceables(String name) {
            super(name);
        }

        /**
         * Method to test the layout methods setting during widget replacement
         */
        public void afterW3() {
        }

        /**
         * Method to test the layout methods setting during widget replacement
         */
        public void beforeW3() {
        }

    }

}
