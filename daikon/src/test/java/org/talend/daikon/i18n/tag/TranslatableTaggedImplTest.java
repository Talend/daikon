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
package org.talend.daikon.i18n.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.talend.daikon.i18n.GlobalI18N;

/**
 * created by dmytro.chmyga on Apr 20, 2017
 */
public class TranslatableTaggedImplTest {

    private static class TaggedTestDefinition extends TranslatableTaggedImpl {

        private Collection<TagImpl> tagsList;

        public void setTags(Collection<TagImpl> tags) {
            this.tagsList = tags;
        }

        protected Collection<TagImpl> doGetTags() {
            return tagsList;
        }

    }

    @Test
    public void testCommonTag() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setTags(Arrays.asList(CommonTestTags.COMMON_TAG));

        assertEquals(1, def.getTags().size());

        assertTrue(def.getTags().iterator().next().hasTag("common tag"));
    }

    @Test
    public void testTags() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setI18nMessageFormatter(GlobalI18N.getI18nMessageProvider().getI18nMessages(this.getClass()));
        TagImpl tag = new TagImpl("testTag");
        def.setTags(Arrays.asList(tag));

        assertEquals(1, def.getTags().size());

        assertTrue(def.getTags().iterator().next().hasTag("Testing"));
    }

    @Test
    public void testDoesntHaveTags() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setI18nMessageFormatter(GlobalI18N.getI18nMessageProvider().getI18nMessages(this.getClass()));
        TagImpl tag = new TagImpl("testTag");
        def.setTags(Arrays.asList(tag));

        assertEquals(1, def.getTags().size());

        assertFalse(def.getTags().iterator().next().hasTag("MySQL"));
    }

}
