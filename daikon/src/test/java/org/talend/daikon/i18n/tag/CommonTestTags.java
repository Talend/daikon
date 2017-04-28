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

import org.talend.daikon.i18n.GlobalI18N;

/**
 * Tags used for testing purpose.
 */
public class CommonTestTags {

    public static final TagImpl COMMON_TAG = new TagImpl("commonTag", null,
            GlobalI18N.getI18nMessageProvider().getI18nMessages(CommonTestTags.class));

}
