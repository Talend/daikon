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
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.AnyPropertyVisitor;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.PropertiesService;
import org.talend.daikon.serialize.SerializerDeserializer;

// import static org.hamcrest.Matchers.*;

public class PropertiesTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesTestUtils.class);

    public static Properties checkSerialize(Properties props, ErrorCollector errorCollector) {
        String s = props.toSerialized();
        SerializerDeserializer.Deserialized<Properties> d = Properties.Helper.fromSerializedPersistent(s, Properties.class);
        Properties deserProps = d.object;
        checkAllI18N(deserProps, errorCollector);
        assertFalse(d.migrated);
        List<NamedThing> newProps = deserProps.getProperties();
        List<Form> newForms = deserProps.getForms();
        int i = 0;
        for (NamedThing prop : props.getProperties()) {
            LOGGER.debug(prop.getName());
            assertEquals(prop.getName(), newProps.get(i).getName());
            i++;
        }
        i = 0;
        for (Form form : props.getForms()) {
            LOGGER.debug("Form: " + form.getName());
            Form newForm = newForms.get(i++);
            assertEquals(form.getName(), form.getName());
            for (Widget widget : form.getWidgets()) {
                NamedThing formChild = widget.getContent();
                String name = formChild.getName();
                if (formChild instanceof Form) {
                    name = ((Form) formChild).getProperties().getName();
                }
                LOGGER.debug("  prop: " + formChild.getName() + " name to be used: " + name);
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
     * check that all Components have theirs internationalisation properties setup correctly.
     */
    static public void checkAllI18N(Properties checkedProps, final ErrorCollector errorCollector) {
        if (checkedProps == null) {
            LOGGER.info("No properties to be checked.");
        } else {
            LOGGER.info("Checking: " + checkedProps.getClass().getName());
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
                    chekProperty(errorCollector, prop, parent);
                }

            }, null);

        }
    }

    /**
     * check that the property has a display name that is translated. We basically checks that is does not end with
     * ".displayName".
     * 
     * @param errorCollector, to collect the error
     * @param prop the property to check for an i18N {@link Property#getDisplayName()}
     * @param parent, used only for the error message to identify the origin of the property
     */
    static public void chekProperty(final ErrorCollector errorCollector, Property<?> prop, Object parent) {
        // check that property.getDisplay name has been translated.
        errorCollector.checkThat(
                "property [" + parent.getClass().getCanonicalName() + "#" + prop.getName()
                        + "] should have a translated message key [property." + prop.getName()
                        + ".displayName] in [the proper messages.properties]",
                prop.getDisplayName().endsWith(".displayName"), is(false));
        if (prop.getDisplayName().endsWith(".displayName")) {// display this to help create the I18N file
            System.out.println("property." + prop.getName() + ".displayName=");
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
