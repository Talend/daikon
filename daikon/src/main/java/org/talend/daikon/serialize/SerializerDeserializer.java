package org.talend.daikon.serialize;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.talend.daikon.exception.TalendRuntimeException;

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

            final Map<PostDeserializeHandler, Integer> postDeserializeHandlers = new HashMap<>();

            JsonReader.JsonClassReaderEx reader = new JsonReader.JsonClassReaderEx() {

                @Override
                public Object read(Object jOb, Deque<JsonObject<String, Object>> stack, Map<String, Object> args) {
                    JsonReader reader = Support.getReader(args);
                    ObjectResolver resolver = (ObjectResolver) args.get(JsonReader.OBJECT_RESOLVER);
                    JsonObject<String, Object> jsonObject = (JsonObject<String, Object>) jOb;
                    Object versionObj = jsonObject.get(VERSION_FIELD);
                    long version = 0;
                    if (versionObj != null)
                        version = ((Long) versionObj).longValue();
                    resolver.traverseFields(stack, (JsonObject<String, Object>) jOb);
                    Object target = ((JsonObject<String, Object>) jOb).getTarget();
                    if (target instanceof PostDeserializeHandler)
                        postDeserializeHandlers.put((PostDeserializeHandler) target, (int) version);
                    return target;
                }
            };

            Map<Class, JsonReader.JsonClassReaderEx> readerMap = new HashMap<>();
            readerMap.put(DeserializeMarker.class, reader);

            JsonReader.MissingFieldHandler missingHandler = new JsonReader.MissingFieldHandler() {

                @Override
                public void fieldMissing(Object object, String fieldName, Object value) {
                    if (!DeserializeDeletedFieldHandler.class.isAssignableFrom(object.getClass()))
                        return;
                    try {
                        Method m = object.getClass().getMethod(DeserializeDeletedFieldHandler.FIELD_DELETED_PREFIX + fieldName,
                                new Class[] { int.class, Object.class });
                        m.invoke(object, new Object[] { 0, value });
                    } catch (NoSuchMethodException e) {
                        // This is OK, just ignore
                    } catch (InvocationTargetException e) {
                        TalendRuntimeException.unexpectedException(e.getCause());
                    } catch (IllegalAccessException e) {
                        TalendRuntimeException.unexpectedException(e.getCause());
                    }
                }
            };

            Map<String, Object> args = new HashMap<>();
            args.put(JsonReader.CUSTOM_READER_MAP, readerMap);
            args.put(JsonReader.MISSING_FIELD_HANDLER, missingHandler);

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
        JsonWriter.JsonClassWriterEx writer = new JsonWriter.JsonClassWriterEx() {

            @Override
            public void write(Object o, boolean showType, Writer output, Map<String, Object> args) throws IOException {
                JsonWriter writer = JsonWriter.JsonClassWriterEx.Support.getWriter(args);
                int version = ((SerializeSetVersion) o).getVersionNumber();
                if (version > 0)
                    output.write("\"" + VERSION_FIELD + "\":" + version + ",");
                writer.writeObject(o, false, true);
            }
        };

        Map<Class, JsonWriter.JsonClassWriterEx> writerMap = new HashMap<>();
        writerMap.put(SerializeSetVersion.class, writer);

        final Map<String, Object> args = new HashMap<>();
        args.put(JsonWriter.CUSTOM_WRITER_MAP, writerMap);

        return JsonWriter.objectToJson(object, args);
    }

}
