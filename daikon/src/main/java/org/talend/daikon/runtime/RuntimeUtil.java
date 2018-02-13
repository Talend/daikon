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
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.ops4j.pax.url.mvn.MavenResolver;
import org.ops4j.pax.url.mvn.MavenResolvers;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.ops4j.pax.url.mvn.internal.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.sandbox.SandboxInstanceFactory;
import org.talend.daikon.sandbox.SandboxedInstance;

public class RuntimeUtil {

    static private final Logger LOG = LoggerFactory.getLogger(RuntimeUtil.class);

    /**
     *
     */
    public static final class MavenUrlStreamHandler extends URLStreamHandler {

        @Override
        public URLConnection openConnection(URL url) throws IOException {
            MavenResolver resolver = MavenResolvers.createMavenResolver(null, ServiceConstants.PID);
            Connection conn = new Connection(url, resolver);
            conn.setUseCaches(false);// to avoid concurent thread to have an IllegalStateException.
            return conn;
        }

        /**
         * only allow parsing of url with "mvn:" if, the url is a complete mvn url then no spec can be added. We hereby
         * assume all urls are set using the complete url and filter the one that may construct url from an existing one
         * This would prevent the UrlClassloader isung mvn protocol to try to load class-path jars and INDEX.LIST jar
         * resources by adding them to a complete mvn url (TCOMP-402)
         *
         */
        @Override
        protected void parseURL(URL u, String spec, int start, int limit) {
            if (!"mvn:".equals(u.toString())) {// remove the spec to only return the url.
                LOG.debug("ignoring specs for parseUrl with url[" + u + "] and spec[" + spec + "]");
                super.parseURL(u, "", 0, 0);
            } else {// simple url being "mvn:" and the rest is specs.
                super.parseURL(u, spec, start, limit);
            }
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

            registerMavenUrlFactory();
        }
    }

    protected static void registerMavenUrlFactory() {
        // If the URL above failed, the mvn protocol needs to be installed.
        // not advice create a wrap URLStreamHandlerFactory class now
        try {
            final Field factoryField = URL.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            final Field lockField = URL.class.getDeclaredField("streamHandlerLock");
            lockField.setAccessible(true);

            synchronized (lockField.get(null)) {
                final URLStreamHandlerFactory factory = (URLStreamHandlerFactory) factoryField.get(null);
                // avoid the factory already defined error
                if (factory != null) {
                    return;
                }

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
        } catch (Exception exception) {
            LOG.warn(exception.getMessage());
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
