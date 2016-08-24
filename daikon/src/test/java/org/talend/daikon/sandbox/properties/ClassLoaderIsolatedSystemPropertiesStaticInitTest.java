package org.talend.daikon.sandbox.properties;
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

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.sandbox.SandboxInstanceFactory;

public class ClassLoaderIsolatedSystemPropertiesStaticInitTest {

    public static final int TEST_TIMES = 5;

    private Properties previous;

    @Before
    public void setUp() throws Exception {
        previous = System.getProperties();
        assertFalse(previous instanceof ClassLoaderIsolatedSystemProperties);
    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(previous);
    }

    @Test
    public void testSetupJVMIsolationProperties() {
        assertFalse(System.getProperties() instanceof ClassLoaderIsolatedSystemProperties);
        // just do the call to have the static initializer called
        SandboxInstanceFactory.createSandboxedInstance(this.getClass().getCanonicalName(), Collections.EMPTY_LIST,
                this.getClass().getClassLoader());
        assertTrue(System.getProperties() instanceof ClassLoaderIsolatedSystemProperties);
    }

}
