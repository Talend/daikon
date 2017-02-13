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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ErrorCollector;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.NamedThing;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.properties.AnyPropertyVisitor;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.PropertiesService;
import org.talend.daikon.serialize.SerializerDeserializer;

// import static org.hamcrest.Matchers.*;

public class PropertiesTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesTestUtils.class);

    /**
     * if we are running from maven and a local repo is used, we also configure pax url to use this local repos.
     */
    public static void setupPaxUrlFromMavenLaunch() {
        String localRepo = System.getProperty("maven.repo.local", "");
        if (!"".equals(localRepo)) {// local repo set
            if (!new File(localRepo).isAbsolute()) {// must be absolute.
                throw new RuntimeException("maven.repo.local system properties must be absolute.");
            } else {
                System.setProperty(ServiceConstants.PID + "." + ServiceConstants.PROPERTY_LOCAL_REPOSITORY, localRepo);
            }
        } // no local repo set so do nothing
    }

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
     * check all properties of a component for i18n, check form i18n, check ComponentProperties title is i18n
     * 
     * @param defRegistry where to get all the definitions
     * @param errorCollector used to collect all errors at once. @see <a
     * href="http://junit.org/apidocs/org/junit/rules/ErrorCollector.html">ErrorCollector</a>
     */
    static public void assertAlli18nAreSetup(DefinitionRegistryService defRegistry, ErrorCollector errorCollector) {
        Collection<Definition> allDefs = defRegistry.getDefinitionsMapByType(Definition.class).values();
        for (Definition def : allDefs) {
            Class propertiesClass = def.getPropertiesClass();
            if (propertiesClass == null) {
                // log it but do not consider it as a error because
                // tComp Wizard ses it with null (this is bad)
                LOGGER.error("Properties class for definition [" + def.getName() + "] should never be null.");
                continue;
            }
            Properties props = PropertiesImpl.createNewInstance(propertiesClass, "root").init();
            // check all properties
            if (props != null) {
                checkAllI18N(props, errorCollector);
            } else {
                LOGGER.info("No properties to check fo I18n for :" + def.getName());
            }
            // check definition name
            errorCollector.checkThat("displayName for definition [" + def.getClass().getName() + "] must not be null",
                    def.getDisplayName(), notNullValue());
            errorCollector.checkThat(
                    "missing I18n displayName [" + def.getDisplayName() + "] for definition [" + def.getClass().getName() + "]",
                    def.getDisplayName(), not(endsWith(Definition.I18N_DISPLAY_NAME_SUFFIX)));
            // check definition title
            errorCollector.checkThat("title for definition [" + def.getClass().getName() + "] must not be null", def.getTitle(),
                    notNullValue());
            errorCollector.checkThat(
                    "missing I18n title [" + def.getTitle() + "] for definition [" + def.getClass().getName() + "]",
                    def.getTitle(), not(endsWith(Definition.I18N_TITLE_NAME_SUFFIX)));
        }
    }

    /**
     * Check that all Components and Wizards have at least one icon image set.
     * 
     * @param defRegistry service to get the components to be checked.
     */
    public static void assertAnIconIsSetup(DefinitionRegistryService defRegistry, ErrorCollector errorCollector) {
        Collection<Definition> allDefs = defRegistry.getDefinitionsMapByType(Definition.class).values();
        for (Definition def : allDefs) {
            String pngImagePath = def.getImagePath(DefinitionImageType.PALETTE_ICON_32X32);
            String svgImagePath = def.getImagePath(DefinitionImageType.SVG_ICON);
            String iconKey = def.getIconKey();
            // At least one of the icon resources must be present.
            if (pngImagePath == null && svgImagePath == null && iconKey == null) {
                errorCollector
                        .addError(new Throwable("the definition [" + def.getName() + "] must have at least one icon resource."));
            }
            if (pngImagePath != null) {// check that the image resource exists
                InputStream resourceAsStream = def.getClass().getResourceAsStream(pngImagePath);
                errorCollector
                        .checkThat(
                                "Failed to find the image for path [" + pngImagePath + "] for the definition [" + def.getName()
                                        + "].\nIt should be located at ["
                                        + def.getClass().getPackage().getName().replace('.', '/') + "/" + pngImagePath + "]",
                                resourceAsStream, notNullValue());
            }
            if (svgImagePath != null) {// check that the image resource exists
                InputStream resourceAsStream = def.getClass().getResourceAsStream(svgImagePath);
                errorCollector
                        .checkThat(
                                "Failed to find the image for path [" + svgImagePath + "] for the definition [" + def.getName()
                                        + "].\nIt should be located at ["
                                        + def.getClass().getPackage().getName().replace('.', '/') + "/" + svgImagePath + "]",
                                resourceAsStream, notNullValue());
            }
            // There is no test for iconKey -- the product is responsible for obtaining the icon corresponding to this
            // value.
        }
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
                if (!properties.equals(prop)) {
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
