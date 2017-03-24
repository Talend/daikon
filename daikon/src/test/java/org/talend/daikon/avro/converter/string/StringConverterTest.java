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
package org.talend.daikon.avro.converter.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit-tests for {@link StringConverter} class
 */
public abstract class StringConverterTest {

    abstract StringConverter<?> createConverter();

    /**
     * Checks {@link StringConverter#getDatumClass()} returns "java.lang.String"
     * {@link Class}
     */
    @Test
    public void testGetDatumClass() {
        StringConverter<?> converter = createConverter();
        Class<?> clazz = converter.getDatumClass();
        assertEquals("java.lang.String", clazz.getCanonicalName());
    }
}
