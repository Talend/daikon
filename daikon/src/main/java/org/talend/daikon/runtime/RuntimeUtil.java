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
package org.talend.daikon.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.ops4j.pax.url.mvn.Handler;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.talend.daikon.sandbox.SandboxInstanceFactory;
import org.talend.daikon.sandbox.SandboxedInstance;

public class RuntimeUtil {

    static {
        // The mvn: protocol is always necessary for the methods in this class.
        registerMavenUrlHandler();
    }

    /**
     * Install the mvn protocol handler for URLs.
     */
    public static void registerMavenUrlHandler() {
        try {
            new URL("mvn:foo/bar");
        } catch (MalformedURLException e) {
            // If the URL above failed, the mvn protocol needs to be installed.
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {

                @Override
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    if (ServiceConstants.PROTOCOL.equals(protocol)) {
                        return new Handler() {

                            @Override
                            protected URLConnection openConnection(URL url) throws IOException {
                                URLConnection conn = super.openConnection(url);
                                conn.setUseCaches(false);// to avoid concurent thread to have an IllegalStateException.
                                return conn;
                            }
                        };
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    /**
     * this will create a {@link SandboxedInstance} class based on the RuntimeInfo and using <code>parentClassLoader</code> if any
     * is provided.
     * If you want to cast the sandboxed instance to some existing classes you are strongly advised to use the Properties
     * classloader used to determine the <code>runtimeInfo<code>.
     * The sandboxed instance will be created in a new ClassLoader and isolated from the current JVM system properties. You must
     * not forget to call {@link SandboxedInstance#close()} in order to release the classloader and remove the System properties
     * isolation, please read carefully the {@link SandboxedInstance} javadoc.
     */
    public static SandboxedInstance createRuntimeClass(RuntimeInfo runtimeInfo, ClassLoader parentClassLoader) {
        return SandboxInstanceFactory.createSandboxedInstance(runtimeInfo.getRuntimeClassName(),
                runtimeInfo.getMavenUrlDependencies(), parentClassLoader, false);
    }

    public static SandboxedInstance createRuntimeClassWithCurrentJVMProperties(RuntimeInfo runtimeInfo,
            ClassLoader parentClassLoader) {
        return SandboxInstanceFactory.createSandboxedInstance(runtimeInfo.getRuntimeClassName(),
                runtimeInfo.getMavenUrlDependencies(), parentClassLoader, true);
    }

}
