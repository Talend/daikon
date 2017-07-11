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
package org.talend.daikon.sandbox;

/**
 * Provide finer-grained control for the creation of {@link SandboxedInstance}s.
 *
 * A {@link org.talend.daikon.runtime.RuntimeInfo} can also optionally implement this class to guide the creation of
 * runtime instances.
 */
public interface SandboxControl {

    /**
     * Most {@link org.talend.daikon.runtime.RuntimeInfo} instances can reuse the classloader that dynamically adds
     * dependencies to the classpath. This can largely improve performance. However, in certain cases, the loaded
     * classes retain state between calls and should not be reused (notably when static members of loaded classes
     * contain state information, and can't be reset within the runtime instance). In this case, this method can be
     * overridden to determine whether the ClassLoader can be obtained from the cache (if available) and saved in the
     * cache for future use.
     * 
     * @return true if the ClassLoader can be cached for future reuse. If false, a new ClassLoader should be created and
     * never reused for future runtime instances.
     */
    boolean isClassLoaderReusable();
}
