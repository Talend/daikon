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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.sandbox.properties.ClassLoaderIsolatedSystemProperties;
import org.talend.daikon.sandbox.properties.StandardPropertiesStrategyFactory;

/**
 * This class provide the instance object created with {@link SandboxInstanceFactory} which system properties are isolated so that
 * any change does not leak into the current JVM system properties.
 * This object must be closed (see {@link #close()} so that the thread used when {@link #getInstance()} was called gets it's
 * initial
 * classloader back and that the isolation mechanism be disconnected from the classloader.
 */
public class SandboxedInstance implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SandboxedInstance.class);

    protected ClassLoader previousContextClassLoader;

    protected Thread isolatedThread;

    protected boolean useCurrentJvmProperties;

    private ClassLoader sandboxClassLoader;

    private String classToInstanciate;

    private Object instance;

    private boolean isClosed;

    private final boolean reusableClassLoader;

    SandboxedInstance(String classToInstanciate, boolean useCurrentJvmProperties, ClassLoader sandboxClassLoader,
            boolean reusableClassLoader) {
        this.classToInstanciate = classToInstanciate;
        this.useCurrentJvmProperties = useCurrentJvmProperties;
        this.sandboxClassLoader = sandboxClassLoader;
        this.reusableClassLoader = reusableClassLoader;
    }

    /**
     * this will reset the thread used to get the instance contextClassLoader to it's inital value before the call to
     * {@link #getInstance()}. <br>
     * This will also turn off the classloader system properties isolation<br>
     * This will also release the ClassLoader so the instance shall not be used anymore after the call to close (if the
     * classloader is {@link AutoCloseable}
     * 
     * @throws Exception if the Classloader call to close fails
     * 
     */
    @Override
    public void close() {
        instance = null;
        if (isolatedThread != null) {
            isolatedThread.setContextClassLoader(previousContextClassLoader);
        } // else getInstance was not called so no need to reset context classloader.

        if (!reusableClassLoader) {
            ClassLoaderIsolatedSystemProperties.getInstance().stopIsolateClassLoader(sandboxClassLoader);
            if (sandboxClassLoader instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) sandboxClassLoader).close();
                } catch (Exception e) {
                    TalendRuntimeException.createUnexpectedException(e);
                }
            }
        }

        sandboxClassLoader = null;
        previousContextClassLoader = null;
        isolatedThread = null;
        isClosed = true;
    }

    /**
     * Return the instance created by the {@link SandboxInstanceFactory} that now isolates the System Properties.
     * <b>WARNING</b> : this also changes the current thread contextClassloader with the classloader used to create the instance,
     * this will enable the isoltion to work. The contextClassLoader will be reset upon the class {@link #close()} call.<br>
     * {@link #close()} must always be called.<br>
     * <b>Please</b> read carefully the {@link #close()} javadoc.
     * Also make sure that the instance returned is used in the current thread used to call this method or in one of it's child
     * threads (assuming they use the same contextClassLoader).
     *
     * @return the instance or null if the {@link #close()} method has been called.
     * @throws TalendRuntimeException is the class failed to be instanciated
     */
    public Object getInstance() {
        if (isClosed) {
            throw new IllegalStateException("Object closed");
        }
        if (isolatedThread == null) {
            isolatedThread = Thread.currentThread();
            previousContextClassLoader = isolatedThread.getContextClassLoader();
            Properties isolatedProperties = useCurrentJvmProperties
                    ? ClassLoaderIsolatedSystemProperties.getInstance().getDefaultSystemProperties()
                    : StandardPropertiesStrategyFactory.create().getStandardProperties();
            ClassLoaderIsolatedSystemProperties.getInstance().startIsolateClassLoader(sandboxClassLoader, isolatedProperties);
            isolatedThread.setContextClassLoader(sandboxClassLoader);
            LOGGER.debug("creating instance of class '" + classToInstanciate + "...'"); //$NON-NLS-1$ //$NON-NLS-2$
            Class<?> clazz;
            try {
                clazz = sandboxClassLoader.loadClass(classToInstanciate);
                instance = clazz.newInstance();
                LOGGER.debug("done creating class '" + classToInstanciate + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        } // else getInstance has already been called
        return this.instance;
    }

    public ClassLoader getSandboxClassLoader() {
        return sandboxClassLoader;
    }
}
