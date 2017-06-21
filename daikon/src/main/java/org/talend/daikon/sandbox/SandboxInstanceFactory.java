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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;

import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.sandbox.properties.ClassLoaderIsolatedSystemProperties;
import org.talend.daikon.sandbox.properties.StandardPropertiesStrategyFactory;
import org.talend.java.util.ClosableLRUMap;

/**
 * this will create class instances from specifc classloader that should not interact with global system properties.
 */
public class SandboxInstanceFactory {

    /**
     * TODO: Add context variable to allow the user to configure the maximum size of the cache. Maybe using a
     * CacheBuilder.
     */
    static Map<RuntimeInfo, ClassLoader> classLoaderCache = Collections
            .synchronizedMap(new ClosableLRUMap<RuntimeInfo, ClassLoader>(3, 10));

    // this swith the current JVM System Properties with our own so that it can handle Thread/ClassLoader isolation
    static {
        ClassLoaderIsolatedSystemProperties isolatedSystemProperties = ClassLoaderIsolatedSystemProperties.getInstance();
        if (!(System.getProperties() instanceof ClassLoaderIsolatedSystemProperties)) {
            System.setProperties(isolatedSystemProperties);
        }
    }

    /**
     * This will create a new class instance base on an URLClassLoader using the <code>classPathUrls</code> and the
     * <code>parentClassLoader</code>. This instance will be embed in a {@link SandboxedInstance} so that it provides a
     * SystemProperty isolation.<brW All the isolation constraints are to be found in the {@link SandboxedInstance}
     * javadoc, please make sure you read it carefully.
     * 
     * @param parentClassLoader used a parent ClassLoader for the newly created classloader, may be null.
     * @param useCurrentJvmProperties if true, a copy of the current jvm system properties will be used, if false then a
     * default jvm set of properties (see {@link StandardPropertiesStrategyFactory} will be used
     * 
     * @return a SandboxedInstance object ready for System Properties isolation
     */
    public static SandboxedInstance createSandboxedInstance(RuntimeInfo runtimeInfo, ClassLoader parentClassLoader,
            boolean useCurrentJvmProperties) {
        if (runtimeInfo.getRuntimeClassName() == null) {
            throw new IllegalArgumentException("classToInstantiate should not be null");
        }

        // Determine whether the ClassLoader for the runtimeInfo can be safely cached for performance.
        ClassLoader sandboxClassLoader;
        boolean reusableClassLoader = true;
        if (runtimeInfo instanceof SandboxControl)
            reusableClassLoader = ((SandboxControl) runtimeInfo).isClassLoaderReusable();

        if (reusableClassLoader) {
            // When the ClassLoader is reusable, use it from the cache.
            synchronized (classLoaderCache) {
                if (classLoaderCache.containsKey(runtimeInfo) && classLoaderCache.get(runtimeInfo) != null) {
                    sandboxClassLoader = classLoaderCache.get(runtimeInfo);
                } else {
                    // the following classloader is closeable so there is a possible resource leak.
                    // if the returned SandboxInstance is properly closed this classLoader shall be closed too.
                    sandboxClassLoader = createClassLoader(runtimeInfo, parentClassLoader);
                    classLoaderCache.put(runtimeInfo, sandboxClassLoader);
                }
            }
        } else {
            // When the ClassLoader is not reusable, never used a cached instance or save it for reuse.
            sandboxClassLoader = createClassLoader(runtimeInfo, parentClassLoader);
        }

        return new SandboxedInstance(runtimeInfo.getRuntimeClassName(), useCurrentJvmProperties, sandboxClassLoader);
    }

    private static URLClassLoader createClassLoader(RuntimeInfo runtimeInfo, ClassLoader parentClassLoader) {
        return new URLClassLoader(
                runtimeInfo.getMavenUrlDependencies().toArray(new URL[runtimeInfo.getMavenUrlDependencies().size()]),
                parentClassLoader) {

            @Override
            public void close() throws IOException {
                super.close();
                ClassLoaderIsolatedSystemProperties.getInstance().stopIsolateClassLoader(this);
            }
        };
    }

    public static void clearCache() {
        classLoaderCache.clear();
    }

}
