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

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.test.PropertiesTestUtils;
import org.talend.daikon.properties.testproperties.TestProperties;

public class TestPropertiesTestUtils {

    private class ADefinition implements Definition {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public void setI18nMessageFormatter(I18nMessages i18nMessages) {

        }

        @Override
        public String getI18nMessage(String key, Object... arguments) {
            return null;
        }

        @Override
        public Class getPropertiesClass() {
            return null;
        }

        @Override
        public String getImagePath() {
            return null;
        }

        @Override
        public String getImagePath(DefinitionImageType type) {
            return null;
        }

        @Override
        public String getIconKey() {
            return null;
        }
    }

    @Test
    public void testAssertAlli18nAreSetup() {
        DefinitionRegistryService defRegServ = mock(DefinitionRegistryService.class);
        Definition repDef = when(mock(Definition.class).getName()).thenReturn("NAME").getMock();
        when(repDef.getPropertiesClass()).thenReturn(TestProperties.class);
        when(defRegServ.getDefinitionsMapByType(Definition.class)).thenReturn(Collections.singletonMap("NAME", repDef));

        ErrorCollector errorCollector = spy(new ErrorCollector());
        // check when everything is fine
        assertThat(repDef, notNullValue());
        when(repDef.getDisplayName()).thenReturn("foo");
        when(repDef.getTitle()).thenReturn("bar");

        PropertiesTestUtils.assertAlli18nAreSetup(defRegServ, errorCollector);

        verify(errorCollector, times(0)).addError(any(Throwable.class));

        // check when displayName and title is missing
        errorCollector = spy(new ErrorCollector());
        when(repDef.getDisplayName()).thenReturn(Definition.I18N_DISPLAY_NAME_SUFFIX);
        when(repDef.getTitle()).thenReturn(Definition.I18N_TITLE_NAME_SUFFIX);

        PropertiesTestUtils.assertAlli18nAreSetup(defRegServ, errorCollector);

        verify(errorCollector, times(2)).addError(any(Throwable.class));

        // check when displayName and title are null
        errorCollector = spy(new ErrorCollector());
        when(repDef.getDisplayName()).thenReturn(null);
        when(repDef.getTitle()).thenReturn(null);

        PropertiesTestUtils.assertAlli18nAreSetup(defRegServ, errorCollector);

        verify(errorCollector, times(2)).addError(any(Throwable.class));
    }

    @Test
    public void testAssertAnIconIsSetup() {
        // create a registry with one definition
        DefinitionRegistryService defRegServ = mock(DefinitionRegistryService.class);
        Definition repDef = when(mock(ADefinition.class).getName()).thenReturn("NAME").getMock();
        when(repDef.getPropertiesClass()).thenReturn(TestProperties.class);
        when(defRegServ.getDefinitionsMapByType(Definition.class)).thenReturn(Collections.singletonMap("NAME", repDef));

        // check when everything is fine
        assertThat(repDef, notNullValue());

        // There is a PNG icon available.
        ErrorCollector errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32))
                .thenReturn("/org/talend/daikon/properties/messages.properties");
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn(null);
        when(repDef.getIconKey()).thenReturn(null);

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(0)).addError(any(Throwable.class));

        // There is an SVG icon available.
        errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32)).thenReturn(null);
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn("/org/talend/daikon/properties/messages.properties");
        when(repDef.getIconKey()).thenReturn(null);

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(0)).addError(any(Throwable.class));

        // There is an iconKey available.
        errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32)).thenReturn(null);
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn(null);
        when(repDef.getIconKey()).thenReturn("icon-key");

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(0)).addError(any(Throwable.class));

        // check when no icon information is present
        errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32)).thenReturn(null);
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn(null);
        when(repDef.getIconKey()).thenReturn(null);

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(1)).addError(any(Throwable.class));

        // There is a PNG icon available, but the path is wrong
        errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32)).thenReturn("foo");
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn(null);
        when(repDef.getIconKey()).thenReturn(null);

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(1)).addError(any(Throwable.class));

        // There is an SVG icon available.
        errorCollector = spy(new ErrorCollector());
        when(repDef.getImagePath(DefinitionImageType.PALETTE_ICON_32X32)).thenReturn(null);
        when(repDef.getImagePath(DefinitionImageType.SVG_ICON)).thenReturn("foo");
        when(repDef.getIconKey()).thenReturn(null);

        PropertiesTestUtils.assertAnIconIsSetup(defRegServ, errorCollector);
        verify(errorCollector, times(1)).addError(any(Throwable.class));
    }

}
