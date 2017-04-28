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
     * Check whether the tag contains a keyword. Looking for keyword in full translated path to root and path to root
     * constructed from tags values.</br>
     * The search is case insensitive. If tag value or translated tag value contains the keyword, this method will
     * return true.
     * 
     * @param tag to be checked
     * @param keyword String translated tag value to be found
     * @return true if tag value or translated tag value contains the keyword
     */
    public static boolean hasTag(Tag tag, String keyword) {
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String translatedValue = getTranslatedPathToRoot(tag);
        String value = getPathToRoot(tag);
        return pattern.matcher(translatedValue).find() || pattern.matcher(value).find();
    }

    /**
     * Get full translated path to root for tag. Full translated path is constructed appending all tags translated
     * values from root parent tag up to current tag with "/" delimeter between them.</br>
     * 
     * Example:<br/>
     * tag has translated value "Current", tag has a parent tag parentTag. parentTag has translatedValue "Parent". Full
     * translated path to root would be created as:</br>
     * {@code "Parent" + "/" + "Current"}</br>
     * and will be equal to</br>
     * {@code "Parent/Current"}
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

    /**
     * Get full path to root for tag. Full path is constructed appending all tags values from root parent tag up to
     * current tag with "/" delimeter between them.</br>
     * 
     * Example:<br/>
     * tag has value "tag", tag has a parent tag parentTag. parentTag has value "parentTag". Full translated path to
     * root would be created as:</br>
     * {@code "parentTag" + "/" + "tag"}</br>
     * and will be equal to</br>
     * {@code "parentTag/tag"}
     * 
     * @param tag to be presented with a full path to root tag
     * @return full path to root tag
     */
    public static String getPathToRoot(Tag tag) {
        Tag parentTag = tag.getParentTag();
        if (parentTag == null) {
            return tag.getValue();
        }
        return getPathToRoot(parentTag) + "/" + tag.getValue();
    }

}
