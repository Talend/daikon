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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

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

    @Test
    public void testEquals() {
        NamedThing snt1 = new SimpleNamedThing("testName", "testLabel");
        NamedThing snt2 = new SimpleNamedThing("testName");
        NamedThing snt3 = new SimpleNamedThing("testName2");
        /* Reflexive */
        assertThat(snt1.equals(snt1), is(Boolean.TRUE));
        assertThat(snt2.equals(snt2), is(Boolean.TRUE));

        /* Symmetric */
        assertThat(snt1.equals(snt2), is(Boolean.TRUE));
        assertThat(snt2.equals(snt1), is(Boolean.TRUE));

        /* Transitive */
        assertThat(snt1.equals(null), is(Boolean.FALSE));
        assertThat(snt2.equals(null), is(Boolean.FALSE));

        assertThat(snt1.equals(snt3), is(Boolean.FALSE));

    }

}
