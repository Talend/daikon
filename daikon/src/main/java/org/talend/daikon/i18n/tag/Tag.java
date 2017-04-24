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

/**
 * Interface for Tag presentation.
 */
public interface Tag {

    public static final String TAG_PREFIX = "tag.";

    /**
     * Check whether tag contains the given string value.
     * 
     * @param tag String representation of tag to be found.
     */
    public boolean hasTag(String tag);

    /**
     * Get translated tag value.
     */
    public String getTranslatedValue();

}
