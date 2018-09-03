package org.talend.logging.audit;

import java.util.Map;

import org.talend.logging.audit.impl.DefaultContextImpl;

/**
 * Factory class for audit logging context.
 *
 * <p>
 * For example:
 * 
 * <pre>
 * 
 * Context ctx = ContextBuilder.create("user", "testuser").build();
 * </pre>
 */
public final class ContextBuilder {

    private final Context context = new DefaultContextImpl();

    private ContextBuilder() {
    }

    /**
     * @return empty context instance.
     */
    public static Context emptyContext() {
        return new DefaultContextImpl();
    }

    /**
     * @return an instance of context builder.
     */
    public static ContextBuilder create() {
        return new ContextBuilder();
    }

    /**
     * @param key to be added
     * @param value to be added
     * @return an instance of context builder with given key-value pair in it.
     */
    public static ContextBuilder create(String key, String value) {
        return new ContextBuilder().with(key, value);
    }

    /**
     * @param context to be used as base for the new one
     * @return an instance of context builder with copy of given context map in it.
     */
    public static ContextBuilder create(Map<String, String> context) {
        return new ContextBuilder().with(context);
    }

    /**
     * Adds given key-value pair to context builder.
     *
     * @param key key of the pair.
     * @param value value of the pair.
     * @return this context builder.
     */
    public ContextBuilder with(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        context.put(key, value);
        return this;
    }

    /**
     * Adds given map to context builder.
     *
     * @param map map to add to the context.
     * @return this context builder.
     */
    public ContextBuilder with(Map<String, String> map) {
        if (map == null) {
            throw new IllegalArgumentException("map cannot be null");
        }
        try {
            if (map.containsKey(null)) {
                throw new IllegalArgumentException("map cannot contain null keys");
            }
        } catch (NullPointerException e) {
            // this means the map doesnt support null keys
        }
        try {
            if (map.containsValue(null)) {
                throw new IllegalArgumentException("map cannot contain null values");
            }
        } catch (NullPointerException e) {
            // this means the map doesnt support null values
        }
        context.putAll(map);
        return this;
    }

    /**
     * @return instance of context with all recorded elements.
     */
    public Context build() {
        return context;
    }
}
