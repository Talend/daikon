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

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.properties.ReferenceExampleProperties.TestAProperties;
import org.talend.daikon.properties.ReferenceExampleProperties.TestBProperties;
import org.talend.daikon.properties.test.PropertiesTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ReferencePropertiesTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void testI18N() {
        PropertiesTestUtils.checkAllI18N(new ReferenceProperties<>("", "").init(), errorCollector);
    }

    @Test
    public void testResolveReferencePropertiesWithMaps() throws ParseException, JsonProcessingException, IOException {

        ReferenceExampleProperties refEProp = new ReferenceExampleProperties(null);
        TestAProperties testAProp = new TestAProperties(null);
        TestBProperties testBProp = new TestBProperties(null);

        assertNull(refEProp.testAPropReference.getReference());
        assertNull(testAProp.testBPropReference.getReference());

        // merge everything to the parent
        Map<String, Properties> definition2PropertiesMap = new HashMap<>();
        definition2PropertiesMap.put("no_used", refEProp);
        definition2PropertiesMap.put(TestAProperties.TEST_A_PROPERTIES_DEFINTION_NAME, testAProp);
        definition2PropertiesMap.put(TestBProperties.TEST_B_PROPERTIES_DEFINTION_NAME, testBProp);
        ReferenceProperties.resolveReferenceProperties(definition2PropertiesMap);

        assertEquals(testAProp, refEProp.testAPropReference.getReference());
        assertEquals(testBProp, testAProp.testBPropReference.getReference());
    }

    @Test
    public void testResolveReferencePropertiesWithRegistryDef() throws ParseException, JsonProcessingException, IOException {

        ReferenceExampleProperties refEProp = new ReferenceExampleProperties(null);
        TestAProperties testAProp = new TestAProperties(null);
        TestBProperties testBProp = new TestBProperties(null);

        assertNull(refEProp.testAPropReference.getReference());
        assertNull(testAProp.testBPropReference.getReference());

        // mock the registry
        DefinitionRegistryService defRegServ = mock(DefinitionRegistryService.class);
        Definition repDef = when(mock(Definition.class).getName()).thenReturn("we_dont_care").getMock();
        when(defRegServ.getDefinitionForPropertiesType(ReferenceExampleProperties.class))
                .thenReturn(Collections.singleton(repDef));
        Definition apDef = when(mock(Definition.class).getName()).thenReturn(TestAProperties.TEST_A_PROPERTIES_DEFINTION_NAME)
                .getMock();
        when(defRegServ.getDefinitionForPropertiesType(TestAProperties.class)).thenReturn(Collections.singleton(apDef));
        Definition bpDef = when(mock(Definition.class).getName()).thenReturn(TestBProperties.TEST_B_PROPERTIES_DEFINTION_NAME)
                .getMock();
        when(defRegServ.getDefinitionForPropertiesType(TestBProperties.class)).thenReturn(Collections.singleton(bpDef));

        // resolve everything
        ReferenceProperties.resolveReferenceProperties(Arrays.asList(refEProp, testAProp, testBProp), defRegServ);

        assertEquals(testAProp, refEProp.testAPropReference.getReference());
        assertEquals(testBProp, testAProp.testBPropReference.getReference());
    }
}
