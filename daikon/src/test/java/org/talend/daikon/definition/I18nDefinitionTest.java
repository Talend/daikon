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
package org.talend.daikon.definition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class I18nDefinitionTest {

    @Test
    public void testGetTitle() {

        // check getTitle with proper i18n
        I18nDefinition i18nDefinition = getMockI18nDef();
        when(i18nDefinition.getI18nMessage("definition.foo.title")).thenReturn("ZeTitle");
        assertEquals("ZeTitle", i18nDefinition.getTitle());

        // check getTitle with no i18n but one available for displayname
        i18nDefinition = getMockI18nDef();
        when(i18nDefinition.getI18nMessage("definition.foo.displayName")).thenReturn("ZedisplayName");
        assertEquals("ZedisplayName", i18nDefinition.getTitle());

        // check getTitle with no i18n and no i18n for display name
        i18nDefinition = getMockI18nDef();
        assertEquals("definition.foo.title", i18nDefinition.getTitle());
    }

    private I18nDefinition getMockI18nDef() {
        I18nDefinition i18nDefinition = spy(new I18nDefinition(""));
        when(i18nDefinition.getName()).thenReturn("foo");
        return i18nDefinition;
    }

}
