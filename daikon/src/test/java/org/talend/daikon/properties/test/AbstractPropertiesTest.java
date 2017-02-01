// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;

public abstract class AbstractPropertiesTest {

    // for benchmarking the apis, one suggestion is to use http://openjdk.java.net/projects/code-tools/jmh/.
    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    abstract public DefinitionRegistryService getDefinitionRegistry();

    /**
     * checks that all properties created from all the definition in the registry have a proper i18n displayName and
     * title. As well as checking for each Property and nested Properties.
     */
    @Test
    public void testAlli18n() {
        PropertiesTestUtils.assertAlli18nAreSetup(getDefinitionRegistry(), errorCollector);
    }

    /**
     * checks that all definitions provide an image path to an existing image.
     */
    @Test
    public void testAllImages() {
        PropertiesTestUtils.assertAllImagesAreSetup(getDefinitionRegistry(), errorCollector);
    }

    /**
     * @deprecated Use {@link #assertComponentIsRegistered(Class, String, Class)} with the expected class types.
     */
    public void assertComponentIsRegistered(String definitionName) {
        Definition definition = getDefinitionRegistry().getDefinitionsMapByType(Definition.class).get(definitionName);
        assertNotNull("Could not find the definition [" + definitionName + "], please check the registered definitions",
                definition);
    }

    /**
     * Check that the set of Definitions that correspond to requestClass contain the value with the given definitionName
     * and definitionClass.
     * 
     * @param requestClass The set of definitions to request by superclass.
     * @param definitionName The name of the definition to request.
     * @param definitionClass The expected type of class returned.
     */
    public void assertComponentIsRegistered(Class<? extends Definition> requestClass, String definitionName,
            Class<? extends Definition> definitionClass) {
        assertThat("Could not find the definition [" + definitionName + ", " + definitionClass + "]",
                getDefinitionRegistry().getDefinitionsMapByType(requestClass),
                hasEntry(is(definitionName), instanceOf(definitionClass)));
    }

}
