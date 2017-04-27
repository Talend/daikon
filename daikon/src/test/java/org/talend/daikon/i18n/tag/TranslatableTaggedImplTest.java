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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.talend.daikon.i18n.GlobalI18N;

/**
 * Tests for {@link TranslatableTaggedImpl} class
 */
public class TranslatableTaggedImplTest {

    private static class TaggedTestDefinition extends TranslatableTaggedImpl {

        private List<TagImpl> tagsList;

        public void setTags(List<TagImpl> tags) {
            this.tagsList = tags;
        }

        protected List<TagImpl> doGetTags() {
            return tagsList;
        }

    }

    @Test
    public void testCommonTag() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setTags(Arrays.asList(CommonTestTags.COMMON_TAG));

        assertEquals(1, def.getTags().size());

        assertTrue(TagUtils.hasTag(def.getTags().get(0), "common tag"));
    }

    @Test
    public void testTags() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setI18nMessageFormatter(GlobalI18N.getI18nMessageProvider().getI18nMessages(this.getClass()));
        TagImpl tag = new TagImpl("testTag");
        def.setTags(Arrays.asList(tag));

        assertEquals(1, def.getTags().size());

        assertTrue(TagUtils.hasTag(def.getTags().get(0), "Testing"));
    }

    @Test
    public void testDoesntHaveTags() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setI18nMessageFormatter(GlobalI18N.getI18nMessageProvider().getI18nMessages(this.getClass()));
        TagImpl tag = new TagImpl("testTag");
        def.setTags(Arrays.asList(tag));

        assertEquals(1, def.getTags().size());

        assertFalse(TagUtils.hasTag(def.getTags().get(0), "MySQL"));
    }

}
