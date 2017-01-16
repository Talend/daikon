// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

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
     * checks that all properties created from all the definition in the registry have a proper i18n displayName and title.
     * As well as checking for each Property and nested Properties.
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

    public void assertComponentIsRegistered(String definitionName) {
        Definition definition = getDefinitionRegistry().getDefinitionsMapByType(Definition.class).get(definitionName);
        assertNotNull("Could not find the definition [" + definitionName + "], please check the registered definitions",
                definition);
    }

}
