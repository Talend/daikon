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

    // this swith the current JVM System Properties with our own so that it can handle Thread/ClassLoader isolation
    static {
        ClassLoaderIsolatedSystemProperties isolatedSystemProperties = ClassLoaderIsolatedSystemProperties.getInstance();
        if (System.getProperties() instanceof ClassLoaderIsolatedSystemProperties) {
            System.setProperties(isolatedSystemProperties);
        }

    }

    private Object instance;

    ClassLoader previousContextClassLoader;

    private ClassLoaderIsolatedSystemProperties isoSystemProp;

    Thread isolatedThread;

    SandboxedInstance(Object instance, ClassLoaderIsolatedSystemProperties isoSystemProp) {
        this.instance = instance;
        this.isoSystemProp = isoSystemProp;

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
        if (isolatedThread != null) {// in case getInstance was not called.
            isolatedThread.setContextClassLoader(previousContextClassLoader);
        }
        ClassLoader instanceClassLoader = instance.getClass().getClassLoader();
        isoSystemProp.disconnectClassLoader(instanceClassLoader);
        if (instanceClassLoader instanceof AutoCloseable) {
            try {
                ((AutoCloseable) instanceClassLoader).close();
            } catch (Exception e) {
                new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        }
    }

    /**
     * Return the instance created by the {@link SandboxInstanceFactory} that now isolates the System Properties.
     * <b>WARNING</b> : this also changes the current thread contextClassloader with the classloader used to create the instance,
     * this
     * will enable the isoltion to work. The contextClassLoader will be reset upon the class {@link #close()} call.<br>
     * {@link #close()} must always be called.<br>
     * <b>Please</b> read carefully the {@link #close()} javadoc.
     * 
     * @return the instance
     */
    public Object getInstance() {
        isolatedThread = Thread.currentThread();
        previousContextClassLoader = isolatedThread.getContextClassLoader();
        ClassLoaderIsolatedSystemProperties.getInstance().isolateClassLoader(instance.getClass().getClassLoader(),
                StandardPropertiesStrategyFactory.create().getStandardProperties());
        isolatedThread.setContextClassLoader(instance.getClass().getClassLoader());
        return this.instance;
    }

}
