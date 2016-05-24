// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class EnumPropertyTest {

    public enum TestEnum {
        FOO,
        BAR,
        FOOBAR
    }

    /**
     * Test method for {@link org.talend.daikon.properties.EnumProperty#getValue()}.
     */
    @Test
    public void testGetValue() {
        EnumProperty<TestEnum> enumProperty = PropertyFactory.newEnum("prop", TestEnum.class);
        assertThat((List<TestEnum>) enumProperty.getPossibleValues(), contains(TestEnum.FOO, TestEnum.BAR, TestEnum.FOOBAR));
        // test the Stored value as a String
        enumProperty.setStoredValue("FOO");
        assertEquals(TestEnum.FOO, enumProperty.getValue());
    }

}
