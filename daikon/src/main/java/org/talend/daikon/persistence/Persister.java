package org.talend.daikon.persistence;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.cedarsoftware.util.io.ObjectResolver;

/**
 * Handles persistent serialization and deserialization to/from a String and supports migration of serialized data to
 * newer versions of classes.
 */
public class Persister {

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
     * Returns a materialized object from a previously serialized JSON String.
     *
     * @param serialized      created by {@link #toSerialized(Object object)}.
     * @param serializedClass the class of the object being deserialized
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    public static <T> Deserialized<T> fromSerialized(String serialized, Class<T> serializedClass) {
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

            final Map<MigrationPostDeserializeHandler, Integer> postDeserializeHandlers = new HashMap<>();

            Map<Class, JsonReader.JsonClassReaderEx> readerMap = new HashMap<>();
            JsonReader.JsonClassReaderEx reader = new JsonReader.JsonClassReaderEx() {
                @Override
                public Object read(Object jOb, Deque<JsonObject<String, Object>> stack, Map<String, Object> args) {
                    JsonReader reader = Support.getReader(args);
                    ObjectResolver resolver = (ObjectResolver) args.get(JsonReader.OBJECT_RESOLVER);
                    int version = 0;
                    resolver.traverseFields(stack, (JsonObject<String, Object>) jOb);
                    Object target = ((JsonObject<String, Object>) jOb).getTarget();
                    if (target instanceof MigrationPostDeserializeHandler)
                        postDeserializeHandlers.put((MigrationPostDeserializeHandler) target, version);
                    return target;
                }
            };
            if (reader != null) {
                readerMap.put(MigrationDeserializeMarker.class, reader);
                args.put(JsonReader.CUSTOM_READER_MAP, readerMap);
            }
            d.object = (T) JsonReader.jsonToJava(serialized, args);
            for (MigrationPostDeserializeHandler obj : postDeserializeHandlers.keySet()) {
                obj.postDeserialize(postDeserializeHandlers.get(obj));
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return d;
    }

    /**
     * Returns a serialized version of the specified object.
     *
     * @return the serialized {@code String}, use {@link #fromSerialized(String, Class)} to materialize the object.
     */
    public static <T> String toSerialized(T object) {
        return JsonWriter.objectToJson(object);
    }

}
