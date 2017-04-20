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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * created by dmytro.chmyga on Apr 20, 2017
 */
public class TranslatableTaggedImplTest {

    private static class TaggedTestDefinition extends TranslatableTaggedImpl {

        private Collection<TagImpl> tags;

        public void setTags(Collection<TagImpl> tags) {
            this.tags = tags;
        }

        protected Collection<TagImpl> doGetTags() {
            return tags;
        }

    }

    @Test
    public void testCommonTag() {
        TaggedTestDefinition def = new TaggedTestDefinition();
        def.setTags(Arrays.asList(CommonTestTags.COMMON_TAG));

        assertEquals(1, def.getTags().size());

        assertTrue(def.getTags().iterator().next().hasTag("common tag"));
    }

}
