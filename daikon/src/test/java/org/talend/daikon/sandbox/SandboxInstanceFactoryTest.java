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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.runtime.RuntimeUtil;

public class SandboxInstanceFactoryTest {

    static final Logger LOG = LoggerFactory.getLogger(SandboxInstanceFactoryTest.class);

    private class Runnable1 implements Runnable {

        private final AtomicBoolean firstSandBCreated;

        private final AtomicBoolean secondSandBCreated;

        private final AtomicBoolean firstSandBClosed;

        private boolean success;

        private Runnable1(AtomicBoolean firstSandBCreated, AtomicBoolean secondSandBCreated, AtomicBoolean firstSandBClosed) {
            this.firstSandBCreated = firstSandBCreated;
            this.secondSandBCreated = secondSandBCreated;
            this.firstSandBClosed = firstSandBClosed;
        }

        @Override
        public void run() {
            URL libUrl;
            try {
                libUrl = new URL("mvn:org.talend.test/zeLib/0.0.1");
                try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(TEST_CLASS_NAME,
                        Collections.singletonList(libUrl), null, false)) {
                    this.firstSandBCreated.set(true);
                    Object obj = sandboxedInstance.getInstance();
                    waitTrue(this.secondSandBCreated, "secondSandBCreated");
                    success = true;
                } finally {
                    this.firstSandBClosed.set(true);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        public void assertSuccess() {
            assertTrue(success);
        }

    }

    private class Runnable2 implements Runnable {

        private final AtomicBoolean secondSandBCreated;

        private final AtomicBoolean firstSandBClosed;

        private final AtomicBoolean firstSandBCreated;

        private boolean success;

        private Runnable2(AtomicBoolean secondSandBCreated, AtomicBoolean firstSandBClosed, AtomicBoolean firstSandBCreated) {
            this.secondSandBCreated = secondSandBCreated;
            this.firstSandBClosed = firstSandBClosed;
            this.firstSandBCreated = firstSandBCreated;
        }

        @Override
        public void run() {
            // wait the first sandbox is created
            waitTrue(this.firstSandBCreated, "firstSandBCreated");
            URL libUrl;
            try {
                libUrl = new URL("mvn:org.talend.test/zeLib/0.0.1");
                try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(TEST_CLASS_NAME,
                        Collections.singletonList(libUrl), null, false)) {
                    Object obj = sandboxedInstance.getInstance();
                    this.secondSandBCreated.set(true);
                    waitTrue(this.firstSandBClosed, "firstSandBClosed");
                    // the next line will throw an IllegalStateException if classloader caches jars.
                    obj.getClass().getClassLoader().loadClass("foo");
                } catch (ClassNotFoundException e) {
                    // expected
                    success = true;
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        public void assertSuccess() {
            assertTrue(success);
        }
    }

    private static final String TEST_CLASS_NAME = "org.talend.test.MyClass1";

    private Properties previous;

    @BeforeClass
    static public void setupMvnHandler() {
        RuntimeUtil.registerMavenUrlHandler();
    }

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

    @Test
    public void test2ThreadCallSameRuntimeWithDifferentClassLoaders() throws InterruptedException {
        final AtomicBoolean firstSandBCreated = new AtomicBoolean(false);
        final AtomicBoolean secondSandBCreated = new AtomicBoolean(false);
        final AtomicBoolean firstSandBClosed = new AtomicBoolean(false);
        Runnable1 runnable1 = new Runnable1(firstSandBCreated, secondSandBCreated, firstSandBClosed);
        Runnable2 runnable2 = new Runnable2(secondSandBCreated, firstSandBClosed, firstSandBCreated);
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        runnable1.assertSuccess();
        runnable2.assertSuccess();
    }

    private void waitTrue(final AtomicBoolean valuetoWaitForTrue, String mess) {
        LOG.debug("waiting for " + mess);
        while (!valuetoWaitForTrue.get()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
        LOG.debug(mess + " set to true.");
    }

}
