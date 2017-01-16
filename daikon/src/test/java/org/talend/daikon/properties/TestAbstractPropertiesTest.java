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
package org.talend.daikon.properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Test;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.properties.test.AbstractPropertiesTest;
import org.talend.daikon.properties.testproperties.TestProperties;

public class TestAbstractPropertiesTest {

    @Test
    public void test() {
        AbstractPropertiesTest propertiesTest = new AbstractPropertiesTest() {

            @Override
            public DefinitionRegistryService getDefinitionRegistry() {
                DefinitionRegistryService defRegServ = mock(DefinitionRegistryService.class);
                Definition repDef = when(mock(Definition.class).getName()).thenReturn("NAME").getMock();
                when(repDef.getPropertiesClass()).thenReturn(TestProperties.class);
                when(defRegServ.getDefinitionsMapByType(Definition.class)).thenReturn(Collections.singletonMap("NAME", repDef));
                return defRegServ;
            }
        };
        // check for an existing definition
        propertiesTest.assertComponentIsRegistered("NAME");
        // check for a non existing def
        try {
            propertiesTest.assertComponentIsRegistered("XXX");
            fail("assertiong should have failed in the above line");
        } catch (AssertionError ae) {
            // this is ok if we have an error
        }

    }

}
