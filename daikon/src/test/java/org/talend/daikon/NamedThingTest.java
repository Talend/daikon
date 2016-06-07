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
package org.talend.daikon;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;

public class NamedThingTest {

    @Test
    public void testSimpleNamedThing() {
        NamedThing nameAndLabel = new SimpleNamedThing("testName", "testLabel");
        assertEquals("testName", nameAndLabel.getName());
        assertEquals("testLabel", nameAndLabel.getDisplayName());
        assertNull(nameAndLabel.getTitle());
        nameAndLabel = new SimpleNamedThing("testName", "testLabel", "testTitle");
        assertEquals("testName", nameAndLabel.getName());
        assertEquals("testLabel", nameAndLabel.getDisplayName());
        assertEquals("testTitle", nameAndLabel.getTitle());
    }

}
