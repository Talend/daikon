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
import java.net.URLClassLoader;
import java.util.Collections;

import org.junit.Test;
import org.talend.daikon.sandbox.properties.ClassLoaderIsolatedSystemProperties;

public class SandboxedInstanceTest {

    private static final String TEST_CLASS_NAME = "org.talend.test.MyClass1";

    /**
     * Test method for {@link org.talend.daikon.sandbox.SandboxedInstance#close()}.
     * 
     * @throws Exception
     */
    @Test
    public void testClose() throws Exception {
        Object initialInstance = createNewInstanceWithNewClassLoader();
        SandboxedInstance sandboxedInstance = new SandboxedInstance(initialInstance,
                ClassLoaderIsolatedSystemProperties.getInstance());
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        sandboxedInstance.getInstance();
        assertTrue(ClassLoaderIsolatedSystemProperties.getInstance().isIsolated(initialInstance.getClass().getClassLoader()));
        sandboxedInstance.close();
        assertEquals(previousClassLoader, Thread.currentThread().getContextClassLoader());
        assertFalse(ClassLoaderIsolatedSystemProperties.getInstance().isIsolated(initialInstance.getClass().getClassLoader()));
    }

    /**
     * Test method for {@link org.talend.daikon.sandbox.SandboxedInstance#getInstance()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetInstance() throws Exception {
        Object initialInstance = createNewInstanceWithNewClassLoader();
        ClassLoader classLoader = initialInstance.getClass().getClassLoader();
        try (SandboxedInstance sandboxedInstance = new SandboxedInstance(initialInstance,
                ClassLoaderIsolatedSystemProperties.getInstance())) {
            assertNull(sandboxedInstance.isolatedThread);
            assertNull(sandboxedInstance.previousContextClassLoader);
            ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
            assertNotEquals(classLoader, previousClassLoader);
            assertFalse(ClassLoaderIsolatedSystemProperties.getInstance().isIsolated(classLoader));
            Object instance = sandboxedInstance.getInstance();
            assertTrue(ClassLoaderIsolatedSystemProperties.getInstance().isIsolated(classLoader));
            assertEquals(initialInstance, instance);
            assertEquals(Thread.currentThread(), sandboxedInstance.isolatedThread);
            assertEquals(previousClassLoader, sandboxedInstance.previousContextClassLoader);
            assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
        }
    }

    public Object createNewInstanceWithNewClassLoader()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        URLClassLoader urlClassLoader = URLClassLoader
                .newInstance(Collections.singleton(this.getClass().getResource("zeLib-0.0.1-SNAPSHOT.jar")).toArray(new URL[1]));
        Class<?> testClass = urlClassLoader.loadClass(TEST_CLASS_NAME);
        return testClass.newInstance();
    }

}
