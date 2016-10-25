// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.exception;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.daikon.exception.TalendRuntimeException.TalendRuntimeExceptionBuilder;

/**
 * Additional context for {@link TalendRuntimeException}s when they occur, in the form of key/value pairs.
 */
public class ExceptionContext implements Serializable {

    private static final long serialVersionUID = -8905451634883948364L;

    /**
     * The internal context. is a LinkedHashMap to guarantee the order of the context entries to help the exception builder. see
     * {@link TalendRuntimeExceptionBuilder#set(String...)}
     */
    private LinkedHashMap<String, Object> context;

    private ExceptionContext() {
        context = new LinkedHashMap<>();
    }

    /**
     * Creates a context from a builder
     */
    private ExceptionContext(ExceptionContextBuilder builder) {
        context = builder.context;
    }

    public static final String KEY_MESSAGE = "message";

    /**
     * Put the given key/value into this context.
     *
     * @param key the key entry.
     * @param value the value entry.
     * @return the context itself so that 'put' calls can be chained.
     */
    public ExceptionContext put(String key, Object value) {
        context.put(key, value);
        return this;
    }

    /**
     * @return a fresh new context.
     */
    public static ExceptionContext build() {
        return new ExceptionContext();
    }

    /**
     * Creates an ExceptionContext with a builder
     *
     * <pre>
     * <code>
     *     ExceptionContext.withBuilder().put("key1", value1).put("key2".value2).build();
     * </code>
     * </pre>
     *
     * @return the builder
     */
    public static ExceptionContextBuilder withBuilder() {
        return new ExceptionContextBuilder();
    }

    /**
     * @return wrapper for the Map.entrySet method so that one can iterate over this context entries.
     * @see Map#entrySet()
     */
    public Iterable<Map.Entry<String, Object>> entries() {
        return context.entrySet();
    }

    /**
     * @param key the key to check.
     * @return true if this context contains the given key.
     */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * @param key the key to get the associated value .
     * @return value associated with the given key.
     */
    public Object get(String key) {
        return context.get(key);
    }

    public ExceptionContext from(Map<String, Object> context) {
        if (context != null) {
            this.context.putAll(context);
        }
        return this;
    }

    @Override
    public String toString() {
        return context != null ? context.toString() : super.toString();

    }

    /**
     * Exception Context Builder. Used with {@link ExceptionContext#withBuilder()}
     */
    public static class ExceptionContextBuilder {

        private LinkedHashMap<String, Object> context = new LinkedHashMap<>();

        /**
         * Adds a new key / value pair in the context
         */
        public ExceptionContextBuilder put(String key, Object value) {
            context.put(key, value);
            return this;
        }

        /**
         * @return the ExceptionContext resulting from the different calls to this builder
         */
        public ExceptionContext build() {
            return new ExceptionContext(this);
        }

        public int getContextSize() {
            return context.size();
        }

        @Override
        public String toString() {
            return context.toString();
        }
    }

}
