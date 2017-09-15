package org.talend.daikon.serialize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.serialize.migration.DeserializeDeletedFieldHandler;
import org.talend.daikon.serialize.migration.DeserializeMarker;
import org.talend.daikon.serialize.migration.PostDeserializeHandler;
import org.talend.daikon.serialize.migration.SerializeSetVersion;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.cedarsoftware.util.io.JsonWriter.JsonClassWriterEx;
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

        /**
         * Set by deserialization to indicate the deserialized object has been changed because it was deserialized from
         * an earlier version.
         *
         * This can be used by the caller to notify the user that the object has been migrated and also, if desired, it
         * can be re-saved in its current serialized form (which would be different than the serialized form provided
         * initially).
         */
        public boolean migrated;
    }

    /**
     * Indicates the purpose of the serialization is to persist the object. The object implementation may take different
     * actions depending on whether the object is persisted (for storage purposes) or serialized for communication m *
     * purposes.
     */
    public static final boolean PERSISTENT = true;

    /**
     * Indicated the purpose of the serialization of the object is to communicate it. See {@link #PERSISTENT}.
     */
    public static final boolean TRANSIENT = false;

    private static final String VERSION_FIELD = "__version";

    private static class CustomReader implements JsonReader.JsonClassReaderEx {

        private Map<PostDeserializeHandler, Integer> postDeserializeHandlers;

        CustomReader(Map<PostDeserializeHandler, Integer> handlers) {
            postDeserializeHandlers = handlers;
        }

        @Override
        public Object read(Object jOb, Deque<JsonObject<String, Object>> stack, Map<String, Object> args) {
            ObjectResolver resolver = (ObjectResolver) args.get(JsonReader.OBJECT_RESOLVER);
            JsonObject<String, Object> jsonObject = (JsonObject<String, Object>) jOb;
            Object versionObj = jsonObject.get(VERSION_FIELD);
            long version = 0;
            if (versionObj != null) {
                version = ((Long) versionObj).longValue();
            }
            resolver.traverseFields(stack, (JsonObject<String, Object>) jOb);
            Object target = ((JsonObject<String, Object>) jOb).getTarget();
            if (target instanceof PostDeserializeHandler) {
                postDeserializeHandlers.put((PostDeserializeHandler) target, (int) version);
            }
            return target;
        }
    }

    private static class MissingFieldHandler implements JsonReader.MissingFieldHandler {

        AtomicBoolean migratedDeleted;

        MissingFieldHandler(AtomicBoolean md) {
            migratedDeleted = md;
        }

        @Override
        public void fieldMissing(Object object, String fieldName, Object value) {
            if (!DeserializeDeletedFieldHandler.class.isAssignableFrom(object.getClass())) {
                return;
            }
            Boolean migrated = ((DeserializeDeletedFieldHandler) object).deletedField(fieldName, value);
            if (migrated) {
                migratedDeleted.set(true);
            }
        }
    }

    /**
     * See {@link #fromSerialized(String, Class, PostDeserializeSetup, boolean)}
     */
    public static <T> Deserialized<T> fromSerializedPersistent(String serialized, Class<T> serializedClass) {
        return fromSerialized(serialized, serializedClass, null, PERSISTENT);
    }

    /**
     * See {@link #fromSerialized(String, Class, PostDeserializeSetup, boolean)}
     */
    public static <T> Deserialized<T> fromSerializedPersistent(String serialized, Class<T> serializedClass,
            PostDeserializeSetup setup) {
        return fromSerialized(serialized, serializedClass, setup, PERSISTENT);
    }

    /**
     * Returns a materialized object from a previously serialized JSON String.
     *
     * @param serialized created by {@link #toSerialized(Object object, boolean persistent)}.
     * @param serializedClass the class of the object being deserialized
     * @param persistent see {@link #PERSISTENT} and {@link #TRANSIENT}.
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    public static <T> Deserialized<T> fromSerialized(String serialized, Class<T> serializedClass, PostDeserializeSetup setup,
            boolean persistent) {
        try (InputStream is = new ByteArrayInputStream(serialized.getBytes("UTF-8"))) {
            return fromSerialized(is, serializedClass, setup, persistent);
        } catch (IOException e) {
            throw TalendRuntimeException.createUnexpectedException(e);
        }
    }

    /**
     * Returns a materialized object from a previously serialized JSON String.
     *
     * @param serialized created by {@link #toSerialized(Object object, boolean persistent)}.
     * @param serializedClass the class of the object being deserialized
     * @param persistent see {@link #PERSISTENT} and {@link #TRANSIENT}.
     * @return a {@code Properties} object represented by the {@code serialized} value.
     */
    public static <T> Deserialized<T> fromSerialized(InputStream serialized, Class<T> serializedClass, PostDeserializeSetup setup,
            boolean persistent) {

        // serializedClass is not used, but we have it only for the generic type support so the caller
        // can avoid casting.
        Deserialized<T> d = new Deserialized<T>();

        Map<PostDeserializeHandler, Integer> postDeserializeHandlers = new HashMap<>();

        Map<Class, JsonReader.JsonClassReaderEx> readerMap = new HashMap<>();
        readerMap.put(DeserializeMarker.class, new CustomReader(postDeserializeHandlers));

        final AtomicBoolean migratedDeleted = new AtomicBoolean(false);// we need to pass the ref.

        Map<String, Object> args = new HashMap<>();
        args.put(JsonReader.CUSTOM_READER_MAP, readerMap);
        args.put(JsonReader.MISSING_FIELD_HANDLER, new MissingFieldHandler(migratedDeleted));
        // in case the json has not type we can try to instantiate the expected type.
        if (serializedClass != null) {
            args.put(JsonReader.UNKNOWN_OBJECT, serializedClass.getCanonicalName());
        }

        d.object = (T) JsonReader.jsonToJava(serialized, args);
        boolean migrated = false;
        for (Entry<PostDeserializeHandler, Integer> entry : postDeserializeHandlers.entrySet()) {
            // use Entry key because the hash code may have changed
            migrated |= entry.getKey().postDeserialize(entry.getValue(), setup, persistent);
        }
        d.migrated = migrated || migratedDeleted.get();
        return d;
    }

    /**
     * See {@link #toSerialized(Object, boolean)}
     */
    public static <T> String toSerializedPersistent(T object) {
        return toSerialized(object, PERSISTENT);
    }

    /**
     * Returns a serialized version of the specified object.
     *
     * @return the serialized {@code String}, use {@link #fromSerialized(String, Class, PostDeserializeSetup, boolean)}
     * to materialize the object.
     */
    public static <T> String toSerialized(T object, boolean persistent) {
        return toSerialized(object, persistent, null);
    }

    /**
     * Returns a serialized version of the specified object.
     *
     * @return the serialized {@code String}, use {@link #fromSerialized(String, Class, PostDeserializeSetup, boolean)}
     * to materialize the object.
     */
    public static String toSerialized(Object object, boolean persistent, Map<String, Object> jsonIoOptions) {
        JsonWriter.JsonClassWriterEx writer = new JsonWriter.JsonClassWriterEx() {

            @Override
            public void write(Object o, boolean showType, Writer output, Map<String, Object> args) throws IOException {
                JsonWriter writer = JsonWriter.JsonClassWriterEx.Support.getWriter(args);
                int version = ((SerializeSetVersion) o).getVersionNumber();
                if (version > 0) {
                    output.write("\"" + VERSION_FIELD + "\":" + version + ",");
                }
                writer.writeObject(o, false, true);
            }
        };

        Map<Class, JsonWriter.JsonClassWriterEx> writerMap = new HashMap<>();
        writerMap.put(SerializeSetVersion.class, writer);

        final Map<String, Object> args = new HashMap<>();
        args.put(JsonWriter.CUSTOM_WRITER_MAP, writerMap);
        if (jsonIoOptions != null) {
            Map<String, Object> additionalOption = new HashMap<>(jsonIoOptions);
            // check for CUSTOM_WRITER_MAP
            if (additionalOption.containsKey(JsonWriter.CUSTOM_WRITER_MAP)) {
                Map<Class, JsonWriter.JsonClassWriterEx> additionalWriterMap = (Map<Class, JsonClassWriterEx>) additionalOption
                        .get(JsonWriter.CUSTOM_WRITER_MAP);
                // remove the object from the additionalOption
                additionalOption.remove(additionalWriterMap);
                // add the map elements to our own map
                writerMap.putAll(additionalWriterMap);
            }
            args.putAll(jsonIoOptions);
        }

        return JsonWriter.objectToJson(object, args);
    }

}
