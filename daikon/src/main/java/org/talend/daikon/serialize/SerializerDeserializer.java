package org.talend.daikon.serialize;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.cedarsoftware.util.io.ObjectResolver;

/**
 * Handles serialization and deserialization to/from a String and supports migration of serialized data to newer
 * versions of classes.
 */
public class SerializerDeserializer {

    /**
     * Holder class for the results of a deserialization.
     */
    public static class Deserialized<T> {

        public T object;

        public MigrationInformation migration;
    }

    protected static JsonReader.FieldReplacer setupReplacer(Class cls) {

        JsonReader.FieldReplacer replacer = new JsonReader.FieldReplacer() {

            @Override
            public Object replace(String fieldName, Object value) {
                System.out.println("replace: " + fieldName + " value: " + value);
                return value;
            }
        };
        return replacer;
    }

    /**
     * Indicates the purpose of the serialization is to persist the object. The object implementation may take different
     * actions depending on whether the object is persisted (for storage purposes) or serialized for communication
     * purposes.
     * 
     */
    public static final boolean PERSISTENT = true;

    /**
     * Indicated the purpose of the serialization of the object is to communicate it. See {@link #PERSISTENT}.
     */
    public static final boolean TRANSIENT = false;

    /**
     * Returns a materialized object from a previously serialized JSON String.
     *
     * @param serialized created by {@link #toSerialized(Object object, boolean persistent)}.
     * @param serializedClass the class of the object being deserialized
     * @param persistent see {@link #PERSISTENT} and {@link #TRANSIENT}.
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    public static <T> Deserialized<T> fromSerialized(String serialized, Class<T> serializedClass, boolean persistent) {
        Deserialized<T> d = new Deserialized<T>();
        d.migration = new MigrationInformationImpl();
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // OSGi requires the the classloader for the target class
            Thread.currentThread().setContextClassLoader(serializedClass.getClassLoader());

            Map<String, Object> args = new HashMap<>();
            Map<Class, JsonReader.FieldReplacer> replacerMap = new HashMap<>();
            JsonReader.FieldReplacer replacer = setupReplacer(serializedClass);
            if (replacer != null) {
                replacerMap.put(serializedClass, replacer);
                args.put(JsonReader.FIELD_REPLACER_MAP, replacerMap);
            }

            final Map<PostDeserializeHandler, Integer> postDeserializeHandlers = new HashMap<>();

            Map<Class, JsonReader.JsonClassReaderEx> readerMap = new HashMap<>();
            JsonReader.JsonClassReaderEx reader = new JsonReader.JsonClassReaderEx() {

                @Override
                public Object read(Object jOb, Deque<JsonObject<String, Object>> stack, Map<String, Object> args) {
                    JsonReader reader = Support.getReader(args);
                    ObjectResolver resolver = (ObjectResolver) args.get(JsonReader.OBJECT_RESOLVER);
                    // FIXME
                    int version = 0;
                    resolver.traverseFields(stack, (JsonObject<String, Object>) jOb);
                    Object target = ((JsonObject<String, Object>) jOb).getTarget();
                    if (target instanceof PostDeserializeHandler)
                        postDeserializeHandlers.put((PostDeserializeHandler) target, version);
                    return target;
                }
            };
            if (reader != null) {
                readerMap.put(DeserializeMarker.class, reader);
                args.put(JsonReader.CUSTOM_READER_MAP, readerMap);
            }
            d.object = (T) JsonReader.jsonToJava(serialized, args);
            for (PostDeserializeHandler obj : postDeserializeHandlers.keySet()) {
                obj.postDeserialize(postDeserializeHandlers.get(obj), persistent);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return d;
    }

    /**
     * Returns a serialized version of the specified object.
     *
     * @return the serialized {@code String}, use {@link #fromSerialized(String, Class, boolean)} to materialize the
     * object.
     */
    public static <T> String toSerialized(T object, boolean persistent) {
        return JsonWriter.objectToJson(object);
    }

}
