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
package org.talend.daikon.sandbox;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SandboxInstanceFactoryTest {

    private static final String TEST_CLASS_NAME = "org.talend.test.MyClass1";

    private Properties previous;

    @Before
    public void setUp() throws Exception {
        previous = System.getProperties();
    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(previous);
    }

    /**
     * Test method for
     * {@link org.talend.daikon.sandbox.SandboxInstanceFactory#createSandboxedInstance(java.lang.String, java.util.Set, java.lang.ClassLoader)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSandboxedInstance() throws Exception {
        // we will check that the created instance object is created properly and created with another class loader.
        ClassLoader parent = new ClassLoader(this.getClass().getClassLoader()) {
            // abstract class but without anything to implement
        };
        URL libUrl = this.getClass().getResource("zeLib-0.0.1.jar");
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(TEST_CLASS_NAME,
                Collections.singletonList(libUrl), parent, true)) {
            assertNotNull(sandboxedInstance);
            Object instance = sandboxedInstance.getInstance();
            assertNotNull(instance);
            assertEquals(TEST_CLASS_NAME, instance.getClass().getCanonicalName());
            ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
            assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader);
            // make sure the parent classloader is the one we gave
            assertEquals(parent, instanceClassLoader.getParent());

        }
    }

    /**
     * Test method for
     * {@link org.talend.daikon.sandbox.SandboxInstanceFactory#createSandboxedInstance(java.lang.String, java.util.Set, java.lang.ClassLoader)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSandboxedInstanceWithNullParenClassLoader() throws Exception {
        URL libUrl = this.getClass().getResource("zeLib-0.0.1.jar");
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(TEST_CLASS_NAME,
                Collections.singletonList(libUrl), null, true)) {
            assertNotNull(sandboxedInstance);
            Object instance = sandboxedInstance.getInstance();
            assertNotNull(instance);
            assertEquals(TEST_CLASS_NAME, instance.getClass().getCanonicalName());
            ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
            assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader);

        }
    }
}
