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
package org.talend.daikon.i18n.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.daikon.i18n.ClassBasedI18nMessages;
import org.talend.daikon.i18n.I18nMessages;

/**
 * Tests for {@link TagImpl} class
 */
public class TagImplTest {

    private static I18nMessages i18nMessages;

    @BeforeClass
    public static void setUp() {
        i18nMessages = new ClassBasedI18nMessages(TagImplTest.class);
    }

    @Test
    public void testTagMessage() {
        TagImpl tag = new TagImpl("testTag");
        tag.setI18nMessageFormatter(i18nMessages);

        assertEquals("Testing tag", tag.getTranslatedValue());
        assertEquals("testTag", tag.getValue());
        assertNull(tag.getParentTag());
    }

    @Test
    public void testWithParentTag() {
        TagImpl parentTag = new TagImpl("parentTag");
        TagImpl tag = new TagImpl("testTag", parentTag);
        parentTag.setI18nMessageFormatter(i18nMessages);
        tag.setI18nMessageFormatter(i18nMessages);

        assertEquals("Testing tag", tag.getTranslatedValue());
        assertEquals("testTag", tag.getValue());
        assertNotNull(tag.getParentTag());
        Tag retrievedParentTag = tag.getParentTag();
        assertEquals("parentTag", retrievedParentTag.getValue());
        assertEquals("parent", retrievedParentTag.getTranslatedValue());
        assertNull(retrievedParentTag.getParentTag());
    }

    @Test
    public void testEmptyTag() {
        TagImpl tag = new TagImpl("nonExistentTag", null);
        tag.setI18nMessageFormatter(i18nMessages);
        assertEquals("nonExistentTag", tag.getTranslatedValue());
    }

}
