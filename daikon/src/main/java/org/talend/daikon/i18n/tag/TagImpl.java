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

import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.i18n.TranslatableImpl;

/**
 * Default implementation of tag interface with internationalization and tags hierarchy support.
 */
public class TagImpl extends TranslatableImpl implements Tag {

    /**
     * Name of the tag.
     */
    private final String value;

    /**
     * Parent tag of the hierarchical tag.
     */
    private Tag parentTag;

    /**
     * This flag is used to check if this instance already has formatter set. We don't want to set a new formatter for
     * tags, which are created in some other package, as their messages file can be present in some other place.
     */
    private boolean formatterSet = false;

    public TagImpl(String name) {
        this(name, null);
    }

    public TagImpl(String name, Tag parentTag) {
        this(name, parentTag, null);
    }

    public TagImpl(String name, Tag parentTag, I18nMessages i18nMessages) {
        this.value = name;
        this.parentTag = parentTag;
        setI18nMessageFormatter(i18nMessages);
    }

    @Override
    public void setI18nMessageFormatter(I18nMessages i18nMessages) {
        if (!formatterSet && (i18nMessages != null)) {
            super.setI18nMessageFormatter(i18nMessages);
            formatterSet = true;
        }
    }

    /**
     * Returns a full translated path to root for current tag. Translated full path is constructed using translated
     * values of all parent tags separated with "/" sign.
     * 
     * @see {@link TagUtils#getTranslatedPathToRoot}
     */
    @Override
    public String toString() {
        return TagUtils.getTranslatedPathToRoot(this);
    }

    /**
     * Get translated tag value. Translated value is an internationalized value present in corresponding properties file
     * with name "tag.{tagName}". If translated value is missing in properties file, tag name is returned.
     */
    @Override
    public String getTranslatedValue() {
        String fullTagName = TAG_PREFIX + getValue();
        String value = getI18nMessage(fullTagName, new Object());
        return fullTagName.equals(value) ? getValue() : value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Tag getParentTag() {
        return parentTag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((parentTag == null) ? 0 : parentTag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagImpl other = (TagImpl) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (parentTag == null) {
            if (other.parentTag != null)
                return false;
        } else if (!parentTag.equals(other.parentTag))
            return false;
        return true;
    }

}
