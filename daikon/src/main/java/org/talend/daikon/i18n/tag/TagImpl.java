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

import java.util.regex.Pattern;

import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.i18n.TranslatableImpl;

/**
 * Default implementation of tag interface with internationalization and tags hierarchy support.
 */
public class TagImpl extends TranslatableImpl implements Tag {

    private final String name;

    private String displayName;

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
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getTranslatedValue();
    }

    /**
     * Get hierarchical tag translated value. Translated value is represented as a concatenation of all parent tags and
     * current tag delimited by "/". <br/>
     */
    @Override
    public String getTranslatedValue() {
        if (displayName == null) {
            StringBuilder tagValueSb = new StringBuilder();
            if (parentTag != null) {
                tagValueSb.append(parentTag.getTranslatedValue()).append("/");
            }
            tagValueSb.append(getStringValue());
            displayName = tagValueSb.toString();
        }
        return displayName;
    }

    protected String getStringValue() {
        String fullTagName = TAG_PREFIX + getName();
        String value = getI18nMessage(fullTagName, new Object());
        return fullTagName.equals(value) ? getName() : value;
    }

    public boolean hasTag(String tag) {
        Pattern pattern = Pattern.compile(tag, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String value = getTranslatedValue();
        return pattern.matcher(value).find();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parentTag == null) {
            if (other.parentTag != null)
                return false;
        } else if (!parentTag.equals(other.parentTag))
            return false;
        return true;
    }

}
