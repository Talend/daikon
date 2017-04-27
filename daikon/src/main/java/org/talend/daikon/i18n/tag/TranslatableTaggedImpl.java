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

import java.util.Collections;
import java.util.List;

import org.talend.daikon.i18n.TranslatableImpl;

/**
 * Implementation of HasTags interface with internationalization.
 */
public class TranslatableTaggedImpl extends TranslatableImpl implements HasTags {

    private List<Tag> tags;

    @Override
    public List<Tag> getTags() {
        if (tags == null) {
            tags = doGetTags();
            for (Tag tag : tags) {
                tag.setI18nMessageFormatter(getI18nMessageFormatter());
            }
        }
        return tags;
    }

    /**
     * Get tags. Override this method to implement tags support.
     */
    protected List<Tag> doGetTags() {
        return Collections.emptyList();
    }

}
