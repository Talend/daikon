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
package org.talend.daikon.properties.test;

import static org.hamcrest.CoreMatchers.*;
// import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.AnyPropertyVisitor;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.Properties.Deserialized;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class PropertiesTestUtils {

    public static Properties checkSerialize(Properties props, ErrorCollector errorCollector) {
        String s = props.toSerialized();
        Deserialized<Properties> d = Properties.fromSerialized(s, Properties.class);
        Properties deserProps = d.properties;
        checkAllI18N(deserProps, errorCollector);
        assertFalse(d.migration.isMigrated());
        List<NamedThing> newProps = deserProps.getProperties();
        List<Form> newForms = deserProps.getForms();
        int i = 0;
        for (NamedThing prop : props.getProperties()) {
            System.out.println(prop.getName());
            assertEquals(prop.getName(), newProps.get(i).getName());
            i++;
        }
        i = 0;
        for (Form form : props.getForms()) {
            System.out.println("Form: " + form.getName());
            Form newForm = newForms.get(i++);
            assertEquals(form.getName(), form.getName());
            for (Widget widget : form.getWidgets()) {
                NamedThing formChild = widget.getContent();
                String name = formChild.getName();
                if (formChild instanceof Form) {
                    name = ((Form) formChild).getProperties().getName();
                }
                System.out.println("  prop: " + formChild.getName() + " name to be used: " + name);
                NamedThing newChild = newForm.getWidget(name).getContent();
                String newName = newChild.getName();
                if (newChild instanceof Form) {
                    newName = ((Form) newChild).getProperties().getName();
                }
                assertEquals(name, newName);

            }
        }
        return deserProps;

    }

    /**
     * check that all Components have theirs internationnalisation properties setup correctly.
     * 
     * @param errorCollector
     * 
     * @param componentService service to get the components to be checked.
     */
    static public void checkAllI18N(Properties checkedProps, final ErrorCollector errorCollector) {
        if (checkedProps == null) {
            System.out.println("No properties to be checked.");
        } else {
            System.out.println("Checking: " + checkedProps);
            checkedProps.accept(new AnyPropertyVisitor() {

                @Override
                public void visit(Properties properties, Properties parent) {
                    // check forms
                    List<Form> forms = properties.getForms();
                    for (Form form : forms) {
                        errorCollector.checkThat(
                                "Form [" + form.getProperties().getClass().getCanonicalName() + "#" + form.getName()
                                        + "] should have a translated message key [form." + form.getName()
                                        + ".displayName] in [the proper messages.properties]",
                                form.getDisplayName().endsWith(".displayName"), is(false));
                        errorCollector.checkThat(
                                "Form [" + form.getProperties().getClass().getCanonicalName() + "#" + form.getName()
                                        + "] should have a translated message key [form." + form.getName()
                                        + ".title] in [the proper messages.properties]",
                                form.getTitle().endsWith(".title"), is(false));

                    }
                }

                @Override
                public void visit(Property prop, Properties parent) {
                    errorCollector.checkThat(
                            "property [" + parent.getClass().getCanonicalName() + "#" + prop.getName()
                                    + "] should have a translated message key [property." + prop.getName()
                                    + ".displayName] in [the proper messages.properties]",
                            prop.getDisplayName().endsWith(".displayName"), is(false));
                }
            }, null);

        }
    }

    /**
     * generate the list of nested class that are of type Properties and format them to used in the Component Definition
     * to tell which are the supported nested classes. The output is formated to have hyper links in Eclipse
     * 
     * @param prop the property to parse
     * @return the sting comma separated list of nested Proerties classes.
     */
    static public String generatedNestedComponentCompatibilitiesJavaCode(final Properties prop) {
        final Set<String> classSet = new HashSet<>();
        prop.accept(new AnyPropertyVisitor() {

            @Override
            public void visit(Properties properties, Properties parent) {
                if (properties != prop) {
                    classSet.add(properties.getClass().getSimpleName() + ".class");
                } // else do not list the prop class itself
            }

            @Override
            public void visit(Property property, Properties parent) {
                // not needed

            }
        }, null);
        return StringUtils.join(classSet, ", ");
    }

}
