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

package org.talend.daikon.sandbox.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implementation of {@link Properties} that keeps a different instance for each registered ("isolated") thread and
 * default properties for non registered threads ("integrated").
 * </p>
 * <p>
 * Note: implementation is designed to be thread safe.
 * </p>
 *
 * @see #startIsolateClassLoader(Thread, java.util.Properties)
 * @see #integrateThread(Thread)
 */
public class ClassLoaderIsolatedSystemProperties extends Properties {

    /**
     * 
     */
    public static final String ORG_OPS4J_PAX_URL_MVN_LOCAL_REPOSITORY = "org.ops4j.pax.url.mvn.localRepository";

    private static final Map<ClassLoader, Properties> classLoaderProperties = new HashMap<ClassLoader, Properties>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderIsolatedSystemProperties.class);

    private static ClassLoaderIsolatedSystemProperties instance;

    private final Properties defaultSystemProperties;

    private ClassLoaderIsolatedSystemProperties(Properties defaultSystemProperties) {
        this.defaultSystemProperties = defaultSystemProperties;
    }

    /**
     * @return Returns {@link ClassLoaderIsolatedSystemProperties} singleton instance.
     */
    public static ClassLoaderIsolatedSystemProperties getInstance() {
        if (instance == null) {
            instance = new ClassLoaderIsolatedSystemProperties(System.getProperties());
        }
        return instance;
    }

    /**
     * "Isolates" a Classloader: this means calls to this class methods will setup isolation mechanism so that
     * classloader-specific properties be set with the <code>theClassLoaderProperties</code>.<br>
     * WARNING : The isolation mechanism will only work <b>if and only if</b> the current Thread contextClassLoader is set to this
     * <code>classLoader</code> class loader.
     *
     * @param classloader A {@link ClassLoader} used as an isolation key.
     * @param classLoaderProperties Default properties for the <code>classLoader</code>. Please note registered ClassLoader will
     * work on <b>a copy</b> of the <code>theClassLoaderProperties</code> parameter.
     */
    public void startIsolateClassLoader(ClassLoader classloader, Properties theClassLoaderProperties) {
        synchronized (classLoaderProperties) {
            if (theClassLoaderProperties == this) {
                // Prevents infinite loops for system property lookup.
                throw new IllegalArgumentException(
                        "Cannot accept instance " + classLoaderProperties.getClass().getName() + " as parameter."); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Isolating ClassLoader '" + classloader.toString() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // we are cloning here cause the new Properties(Properties default) is using default as a backup when a key is not
            // found and does not have any key/values in it's properties
            Properties clonedProperties = (Properties) theClassLoaderProperties.clone();
            // setup pax.url local repo for a proper classloader resolution when using mvn protocol
            // TODO setup a generic way to copy certain properties to isolated CL.
            if (defaultSystemProperties.containsKey(ORG_OPS4J_PAX_URL_MVN_LOCAL_REPOSITORY)) {
                clonedProperties.setProperty(ORG_OPS4J_PAX_URL_MVN_LOCAL_REPOSITORY,
                        defaultSystemProperties.getProperty(ORG_OPS4J_PAX_URL_MVN_LOCAL_REPOSITORY));
            }
            classLoaderProperties.put(classloader, clonedProperties);
        }
    }

    /**
     * disconnect the isolation mechanism from the classloader so that it will now work on shared properties.
     *
     * @param classLoader The {@link ClassLoader} to disconnect.
     */
    public void stopIsolateClassLoader(ClassLoader classLoader) {
        synchronized (classLoaderProperties) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stops isolating ClassLoader '" + classLoader.toString() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            classLoaderProperties.remove(classLoader);
        }
    }

    /**
     * checks is the isoaltion mechanism is setup for the given <code>classloader</code>
     * 
     * @param the ClassLoader to be checked
     * @return whether the ClassLoader is handled by the isolation mechanism or not
     */
    public boolean isIsolated(ClassLoader classloader) {
        return classLoaderProperties.containsKey(classloader);
    }

    /**
     * @param thread A {@link Thread} instance.
     * @return Returns current thread {@link Properties} instance.
     */
    public Properties getThreadProperties(Thread thread) {
        synchronized (classLoaderProperties) {
            if (thread == null) {
                return defaultSystemProperties;
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Requesting for thread system properties '" + thread.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            Properties currentThreadProperties = classLoaderProperties.get(thread.getContextClassLoader());
            if (currentThreadProperties == null) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread '" + thread.getName() + "' is not isolated. Return default properties"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return defaultSystemProperties;
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Thread '" + thread.getName() + "' is isolated. Return thread properties (dump below)."); //$NON-NLS-1$ //$NON-NLS-2$
                    LOGGER.trace(currentThreadProperties.toString());
                }
                return currentThreadProperties;
            }
        }
    }

    /**
     * this will return the system properties instance that was used before starting the first isolation
     */
    public Properties getDefaultSystemProperties() {
        return defaultSystemProperties;
    }

    /**
     * @return Returns current running thread {@link Properties} instance.
     */
    public Properties getThreadProperties() {
        return getThreadProperties(Thread.currentThread());
    }

    /*
     * DELEGATE METHODS for java.util.Hashtable
     */
    @Override
    public synchronized void putAll(Map<?, ?> map) {
        Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
        for (Map.Entry<?, ?> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return setProperty(((String) key), ((String) value));
    }

    @Override
    public synchronized int size() {
        return getThreadProperties().size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return getThreadProperties().isEmpty();
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return getThreadProperties().keys();
    }

    @Override
    public synchronized Enumeration<Object> elements() {
        return getThreadProperties().elements();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return getThreadProperties().contains(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return getThreadProperties().containsValue(o);
    }

    @Override
    public synchronized boolean containsKey(Object o) {
        return getThreadProperties().containsKey(o);
    }

    @Override
    public synchronized Object get(Object o) {
        return getThreadProperties().get(o);
    }

    @Override
    public synchronized Object remove(Object o) {
        return getThreadProperties().remove(o);
    }

    @Override
    public synchronized void clear() {
        getThreadProperties().clear();
    }

    @Override
    public synchronized Object clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized String toString() {
        return getThreadProperties().toString();
    }

    @Override
    public Set<Object> keySet() {
        return getThreadProperties().keySet();
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return getThreadProperties().entrySet();
    }

    @Override
    public Collection<Object> values() {
        return getThreadProperties().values();
    }

    /*
     * DELEGATE METHODS for Properties
     */
    @Override
    public Object setProperty(String key, String value) {
        return getThreadProperties().setProperty(key, value);
    }

    @Override
    public void load(Reader reader) throws IOException {
        getThreadProperties().load(reader);
    }

    @Override
    public void load(InputStream inStream) throws IOException {
        getThreadProperties().load(inStream);
    }

    @Override
    @Deprecated
    public void save(OutputStream out, String comments) {
        getThreadProperties().save(out, comments);
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        getThreadProperties().store(writer, comments);
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        getThreadProperties().store(out, comments);
    }

    @Override
    public void loadFromXML(InputStream in) throws IOException {
        getThreadProperties().loadFromXML(in);
    }

    @Override
    public void storeToXML(OutputStream os, String comment) throws IOException {
        getThreadProperties().storeToXML(os, comment);
    }

    @Override
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        getThreadProperties().storeToXML(os, comment, encoding);
    }

    @Override
    public String getProperty(String key) {
        return getThreadProperties().getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getThreadProperties().getProperty(key, defaultValue);
    }

    @Override
    public Enumeration<?> propertyNames() {
        return getThreadProperties().propertyNames();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return getThreadProperties().stringPropertyNames();
    }

    @Override
    public void list(PrintStream out) {
        getThreadProperties().list(out);
    }

    @Override
    public void list(PrintWriter out) {
        getThreadProperties().list(out);
    }
}
