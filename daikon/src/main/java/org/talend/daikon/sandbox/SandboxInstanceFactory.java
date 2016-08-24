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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.sandbox.properties.ClassLoaderIsolatedSystemProperties;

/**
 * this will create class instances from specifc classloader that should not interact with global system properties.
 */
public class SandboxInstanceFactory {

    // this swith the current JVM System Properties with our own so that it can handle Thread/ClassLoader isolation
    static {
        ClassLoaderIsolatedSystemProperties isolatedSystemProperties = ClassLoaderIsolatedSystemProperties.getInstance();
        if (!(System.getProperties() instanceof ClassLoaderIsolatedSystemProperties)) {
            System.setProperties(isolatedSystemProperties);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SandboxInstanceFactory.class);

    /**
     * This will create a new class instance base on an URLClassLoader using the <code>classPathUrls</code> and the
     * <code>parentClassLoader</code>.
     * This instance will be embed in a {@link SandboxedInstance} so that it provides a SystemProperty isolation.<brW
     * All the isolation constraints are to be found in the {@link SandboxedInstance} javadoc, please make sure you read it
     * carefully.
     * 
     * @param classToInstanciated name of the class to instantiate
     * @param classPathUrls set of URLs to be used for creating a new {@link ClassLoader}.
     * @param parentClassLoader used a parent ClassLoader for the newly created classloader.
     * @return a SandboxedInstance object ready for System Properties isolation
     */
    public static SandboxedInstance createSandboxedInstance(String classToInstanciated, List<URL> classPathUrls,
            ClassLoader parentClassLoader) {
        if (classToInstanciated == null) {
            throw new IllegalArgumentException("classToInstanciate should not be null");
        }
        ClassLoaderIsolatedSystemProperties isolatedSystemProperties = ClassLoaderIsolatedSystemProperties.getInstance();

        try {

            LOGGER.info("creating class '" + classToInstanciated + "'"); //$NON-NLS-1$ //$NON-NLS-2$

            // the following classloader is closeable so there is a possible resource leak.
            // if the returned SandboxInstance is properly closed this classLoader shall be closed too.
            ClassLoader sandboxClassLoader = new URLClassLoader(classPathUrls.toArray(new URL[classPathUrls.size()]),
                    parentClassLoader);

            Class<?> clazz = sandboxClassLoader.loadClass(classToInstanciated);
            return new SandboxedInstance(clazz.newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
