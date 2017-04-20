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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.daikon.i18n.ClassBasedI18nMessages;
import org.talend.daikon.i18n.I18nMessages;

/**
 * 
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

        String translatedTagValue = tag.getTranslatedValue();
        assertEquals("Testing tag", translatedTagValue);
    }

    @Test
    public void testHierarchyTag() {
        TagImpl parentTag = new TagImpl("parentTag");
        TagImpl childTag = new TagImpl("childTag", parentTag);
        parentTag.setI18nMessageFormatter(i18nMessages);
        childTag.setI18nMessageFormatter(i18nMessages);

        String translatedParentTagValue = parentTag.getTranslatedValue();
        assertEquals("parent", translatedParentTagValue);
        String translatedChildTagValue = childTag.getTranslatedValue();
        assertEquals("parent/child", translatedChildTagValue);
    }

    @Test
    public void testEmptyTag() {
        TagImpl parentTag = new TagImpl("parentTag");
        TagImpl childTag = new TagImpl("nonExistentTag", parentTag);
        parentTag.setI18nMessageFormatter(i18nMessages);
        childTag.setI18nMessageFormatter(i18nMessages);

        String translatedChildTagValue = childTag.getTranslatedValue();
        assertEquals("parent/nonExistentTag", translatedChildTagValue);
    }

    @Test
    public void testCommonTag() {
        TagImpl childTag = new TagImpl("childTag", CommonTestTags.COMMON_TAG);
        TagImpl childTag1 = new TagImpl("nonExistentTag", CommonTestTags.COMMON_TAG);
        CommonTestTags.COMMON_TAG.setI18nMessageFormatter(i18nMessages);
        childTag.setI18nMessageFormatter(i18nMessages);
        childTag1.setI18nMessageFormatter(i18nMessages);

        assertEquals("Common tag/child", childTag.getTranslatedValue());
        assertEquals("Common tag/nonExistentTag", childTag1.getTranslatedValue());
    }

    @Test
    public void testHasTag() {
        TagImpl tag = new TagImpl("testTag");
        tag.setI18nMessageFormatter(i18nMessages);

        Assert.assertTrue(tag.hasTag("Testing"));
        Assert.assertTrue(tag.hasTag("sting tag"));
        Assert.assertTrue(tag.hasTag("test"));
    }

    @Test
    public void testHasParentTag() {
        TagImpl tag = new TagImpl("testTag", CommonTestTags.COMMON_TAG);
        tag.setI18nMessageFormatter(i18nMessages);

        Assert.assertTrue(tag.hasTag("Common tag"));
        Assert.assertTrue(tag.hasTag("Com"));
        Assert.assertTrue(tag.hasTag("mon tag"));
        Assert.assertTrue(tag.hasTag("Common tag/Testing"));
        Assert.assertTrue(tag.hasTag("Testing"));
    }

    @Test
    public void testDoesntHaveTag() {
        TagImpl tag = new TagImpl("testTag", CommonTestTags.COMMON_TAG);
        tag.setI18nMessageFormatter(i18nMessages);

        Assert.assertFalse(tag.hasTag("MySQL"));
        Assert.assertFalse(tag.hasTag("Cloud"));
    }

}
