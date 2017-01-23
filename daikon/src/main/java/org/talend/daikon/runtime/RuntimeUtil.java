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

import org.ops4j.pax.url.mvn.MavenResolver;
import org.ops4j.pax.url.mvn.MavenResolvers;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.ops4j.pax.url.mvn.internal.Connection;
import org.ops4j.pax.url.mvn.internal.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.sandbox.SandboxInstanceFactory;
import org.talend.daikon.sandbox.SandboxedInstance;

public class RuntimeUtil {

    static private final Logger LOG = LoggerFactory.getLogger(RuntimeUtil.class);

    public static final class MavenUrlStreamHandler extends URLStreamHandler {

        @Override
        public URLConnection openConnection(URL url) throws IOException {
            // check for URL to not have library name instead of version see (TCOMP-402)
            // unfortunately we need to use an pax mvn internal api (Parser).
            if (new Parser(url.getPath()).getVersion().endsWith(".jar")) {
                LOG.debug("Ignor this artifact : " + url.toString());
                throw new IOException("Ignoring supposedly wrong URL :" + url.toString());
            }
            MavenResolver resolver = MavenResolvers.createMavenResolver(null, ServiceConstants.PID);
            Connection conn = new Connection(url, resolver);
            conn.setUseCaches(false);// to avoid concurent thread to have an IllegalStateException.
            return conn;
        }
    }

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

            // handles mvn local repository
            String mvnLocalRepo = System.getProperty("maven.repo.local");
            if (mvnLocalRepo != null) {
                System.setProperty("org.ops4j.pax.url.mvn.localRepository", mvnLocalRepo);
            }

            // If the URL above failed, the mvn protocol needs to be installed.
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {

                @Override
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    if (ServiceConstants.PROTOCOL.equals(protocol)) {
                        return new MavenUrlStreamHandler();
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    /**
     * this will create a {@link SandboxedInstance} class based on the RuntimeInfo and using
     * <code>parentClassLoader</code> if any is provided. If you want to cast the sandboxed instance to some existing
     * classes you are strongly advised to use the Properties classloader used to determine the <code>runtimeInfo<code>.
     * The sandboxed instance will be created in a new ClassLoader and isolated from the current JVM system properties.
     * You must not forget to call {@link SandboxedInstance#close()} in order to release the classloader and remove the
     * System properties isolation, please read carefully the {@link SandboxedInstance} javadoc.
     */
    public static SandboxedInstance createRuntimeClass(RuntimeInfo runtimeInfo, ClassLoader parentClassLoader) {
        return SandboxInstanceFactory.createSandboxedInstance(runtimeInfo, parentClassLoader, false);
    }

    public static SandboxedInstance createRuntimeClassWithCurrentJVMProperties(RuntimeInfo runtimeInfo,
            ClassLoader parentClassLoader) {
        return SandboxInstanceFactory.createSandboxedInstance(runtimeInfo, parentClassLoader, true);
    }

}
