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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.properties.ClassLoaderIsolatedSystemProperties;
import org.talend.java.util.ClosableLRUMap;

public class SandboxInstanceFactoryTest {

    static final private Logger LOG = LoggerFactory.getLogger(SandboxInstanceFactoryTest.class);

    private class TestRuntime implements RuntimeInfo, SandboxControl {

        private boolean reusable = true;

        private String cacheSuffix = "";

        public TestRuntime() {
        }

        public TestRuntime(String cacheSuffix) {
            this.cacheSuffix = cacheSuffix;
        }

        public TestRuntime(String cacheSuffix, boolean reusable) {
            this(cacheSuffix);
            this.reusable = reusable;
        }

        @Override
        public String getRuntimeClassName() {
            return TEST_CLASS_NAME;
        }

        @Override
        public List<URL> getMavenUrlDependencies() {
            try {
                return Collections.singletonList(new URL("mvn:org.talend.test/zeLib/0.0.1"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isClassLoaderReusable() {
            return reusable;
        }

        @Override
        public String toString() {
            return TEST_CLASS_NAME + cacheSuffix;
        }

        @Override
        public boolean equals(Object obj) {
            return (TEST_CLASS_NAME + cacheSuffix).equals(obj.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

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
            try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime(), null,
                    false);) {
                this.firstSandBCreated.set(true);
                sandboxedInstance.getInstance();
                waitTrue(this.secondSandBCreated, "secondSandBCreated");
                success = true;
            } finally {
                this.firstSandBClosed.set(true);
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
            try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime(), null,
                    false)) {
                Object obj = sandboxedInstance.getInstance();
                this.secondSandBCreated.set(true);
                waitTrue(this.firstSandBClosed, "firstSandBClosed");
                // the next line will throw an IllegalStateException if classloader caches jars.
                obj.getClass().getClassLoader().loadClass("foo");
            } catch (ClassNotFoundException e) {
                // expected
                success = true;
            }
        }

        public void assertSuccess() {
            assertTrue(success);
        }
    }

    private class ThreadIsolationRunnableTest implements Runnable {

        private boolean success;

        @Override
        public void run() {
            try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime(), null,
                    false)) {
                Object obj = sandboxedInstance.getInstance();
                // check that the thread is correctly sandboxed
                boolean objOk = obj != null;
                boolean isIsolated = ClassLoaderIsolatedSystemProperties.getInstance()
                        .isIsolated(Thread.currentThread().getContextClassLoader());
                success = objOk && isIsolated;
                if (!success) {
                    LOG.error("Thread isolation test failed [objOk:" + objOk + ",isIsolated:" + isIsolated + "]");
                }
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
        SandboxInstanceFactory.clearCache();

    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(previous);
    }

    /**
     * Test method for {@link org.talend.daikon.sandbox.SandboxInstanceFactory#createSandboxedInstance}
     *
     * @throws Exception
     */
    @Test
    public void testCreateSandboxedInstance() throws Exception {
        // we will check that the created instance object is created properly and created with another class loader.
        ClassLoader parent = new ClassLoader(this.getClass().getClassLoader()) {
            // abstract class but without anything to implement
        };
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test1"),
                parent, true)) {
            assertNotNull(sandboxedInstance);
            Object instance = sandboxedInstance.getInstance();
            assertNotNull(instance);
            assertEquals(TEST_CLASS_NAME, instance.getClass().getCanonicalName());
            ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
            // WARNING the following test may fail from Eclipse or IntelliJ cause the
            // runtime jar is on the classpath for the tests and should not (see pom.xml)
            assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader);
            // make sure the parent classloader is the one we gave
            assertEquals(parent, instanceClassLoader.getParent());

        }
    }

    /**
     * Create two sandboxed instance and check if they are correctly sharing the same ClassLoader.
     */
    @Test
    public void testCreate2SandboxedInstance() throws Exception {
        // we will check that the created instance object is created properly and created with another class loader.
        ClassLoader parent = new ClassLoader(this.getClass().getClassLoader()) {
            // abstract class but without anything to implement
        };
        ClassLoader classLoader = null;
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test2"),
                parent, true)) {
            assertNotNull(sandboxedInstance);
            Object instance = sandboxedInstance.getInstance();
            assertNotNull(instance);
            // WARNING the following test may fail from Eclipse or IntelliJ cause the
            // runtime jar is on the classpath for the tests and should not (see pom.xml)
            assertEquals(TEST_CLASS_NAME, instance.getClass().getCanonicalName());
            ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
            assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader);
            // make sure the parent classloader is the one we gave
            assertEquals(parent, instanceClassLoader.getParent());
            classLoader = sandboxedInstance.getSandboxClassLoader();
            try (SandboxedInstance sandboxedInstance2 = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test2"),
                    parent, true)) {
                assertNotNull(sandboxedInstance2);
                Object instance2 = sandboxedInstance2.getInstance();
                assertNotNull(instance2);
                assertEquals(TEST_CLASS_NAME, instance2.getClass().getCanonicalName());
                ClassLoader instanceClassLoader2 = instance2.getClass().getClassLoader();
                assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader2);
                // make sure the parent classloader is the one we gave
                assertEquals(parent, instanceClassLoader2.getParent());

                assertEquals(classLoader, sandboxedInstance2.getSandboxClassLoader());
            }

        }
    }

    /**
     * Create two sandboxed instance that do not have a reusable runtime and check if they are not sharing the same
     * ClassLoader.
     */
    @Test
    public void testCreate2SandboxedInstanceUnreusedClassLoader() throws Exception {
        // we will check that the created instance object is created properly and created with another class loader.
        ClassLoader parent = new ClassLoader(this.getClass().getClassLoader()) {
            // abstract class but without anything to implement
        };
        ClassLoader classLoader = null;
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test2", false),
                parent, true)) {
            assertNotNull(sandboxedInstance);
            Object instance = sandboxedInstance.getInstance();
            assertNotNull(instance);
            // WARNING the following test may fail from Eclipse or IntelliJ cause the
            // runtime jar is on the classpath for the tests and should not (see pom.xml)
            assertEquals(TEST_CLASS_NAME, instance.getClass().getCanonicalName());
            ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
            assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader);
            // make sure the parent classloader is the one we gave
            assertEquals(parent, instanceClassLoader.getParent());
            classLoader = sandboxedInstance.getSandboxClassLoader();
            try (SandboxedInstance sandboxedInstance2 = SandboxInstanceFactory
                    .createSandboxedInstance(new TestRuntime("test2", false), parent, true)) {
                assertNotNull(sandboxedInstance2);
                Object instance2 = sandboxedInstance2.getInstance();
                assertNotNull(instance2);
                assertEquals(TEST_CLASS_NAME, instance2.getClass().getCanonicalName());
                ClassLoader instanceClassLoader2 = instance2.getClass().getClassLoader();
                assertNotEquals(this.getClass().getClassLoader(), instanceClassLoader2);
                // make sure the parent classloader is the one we gave
                assertEquals(parent, instanceClassLoader2.getParent());

                // The classloaders must not be the same.
                assertNotSame(classLoader, sandboxedInstance2.getSandboxClassLoader());
            }
        }
    }

    @Test
    public void testCacheClassLoaderClosedAndNotIsolated() throws Exception {
        try {
            SandboxInstanceFactory.classLoaderCache = Collections
                    .synchronizedMap(new ClosableLRUMap<RuntimeInfo, ClassLoader>(1, 1));
            // we will check that the created instance object is created properly and created with another class loader.
            ClassLoader parent = new ClassLoader(this.getClass().getClassLoader()) {
                // abstract class but without anything to implement
            };
            try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test2"),
                    parent, true)) {
                assertNotNull(sandboxedInstance);
                sandboxedInstance.getInstance();// start isolation
                try (SandboxedInstance sandboxedInstance2 = SandboxInstanceFactory
                        .createSandboxedInstance(new TestRuntime("test1"), parent, true)) {
                    sandboxedInstance2.getInstance();// start isolation
                    assertFalse(ClassLoaderIsolatedSystemProperties.getInstance()
                            .isIsolated(sandboxedInstance.getSandboxClassLoader()));
                    assertTrue(ClassLoaderIsolatedSystemProperties.getInstance()
                            .isIsolated(sandboxedInstance2.getSandboxClassLoader()));

                }
            }
        } finally {
            SandboxInstanceFactory.classLoaderCache = Collections
                    .synchronizedMap(new ClosableLRUMap<RuntimeInfo, ClassLoader>(3, 10));
        }
    }

    /**
     * Test method for {@link org.talend.daikon.sandbox.SandboxInstanceFactory#createSandboxedInstance} .
     *
     * @throws Exception
     */
    @Test
    public void testCreateSandboxedInstanceWithNullParenClassLoader() throws Exception {
        try (SandboxedInstance sandboxedInstance = SandboxInstanceFactory.createSandboxedInstance(new TestRuntime("test3"), null,
                true)) {
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

    @Test
    public void test2ThreadHaveProperIsolation() throws InterruptedException {
        ThreadIsolationRunnableTest isol1 = new ThreadIsolationRunnableTest();
        Thread thread1 = new Thread(isol1);
        ThreadIsolationRunnableTest isol2 = new ThreadIsolationRunnableTest();
        Thread thread2 = new Thread(isol2);
        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        isol1.assertSuccess();
        isol2.assertSuccess();
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
