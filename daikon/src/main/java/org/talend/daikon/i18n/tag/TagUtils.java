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

/**
 * Utility class to work with tags
 */
public class TagUtils {

    /**
     * Check whether the tag contains a keyword
     * 
     * @param tag to be checked
     * @param keyword String translated tag value to be found
     */
    public static boolean hasTag(Tag tag, String keyword) {
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String value = getTranslatedPathToRoot(tag);
        return pattern.matcher(value).find();
    }

    /**
     * Get full translated path to root for tag.
     * 
     * @param tag to be presented with a full path to root tag
     * @return full translated path to root tag
     */
    public static String getTranslatedPathToRoot(Tag tag) {
        Tag parentTag = tag.getParentTag();
        if (parentTag == null) {
            return tag.getTranslatedValue();
        }
        return getTranslatedPathToRoot(parentTag) + "/" + tag.getTranslatedValue();
    }

}
