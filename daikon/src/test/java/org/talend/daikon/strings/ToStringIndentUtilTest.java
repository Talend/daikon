// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.strings;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.strings.ToStringIndentUtil;

/**
 * created by pbailly on 16 Dec 2015 Detailled comment
 *
 */
public class ToStringIndentUtilTest {

    @Test
    public void test() {
        assertEquals("", ToStringIndentUtil.indentString(-1));
        assertEquals("", ToStringIndentUtil.indentString(0));
        assertEquals(" ", ToStringIndentUtil.indentString(1));
        assertEquals("  ", ToStringIndentUtil.indentString(2));
        assertEquals("    ", ToStringIndentUtil.indentString(4));
        assertEquals("          ", ToStringIndentUtil.indentString(10));
    }

}
