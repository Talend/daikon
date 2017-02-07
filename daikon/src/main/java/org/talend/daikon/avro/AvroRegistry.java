package org.talend.daikon.avro;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.avro.converter.ConvertBigDecimal;
import org.talend.daikon.avro.converter.ConvertBigInteger;
import org.talend.daikon.avro.converter.ConvertByte;
import org.talend.daikon.avro.converter.ConvertCharacter;
import org.talend.daikon.avro.converter.ConvertDate;
import org.talend.daikon.avro.converter.ConvertInetAddress;
import org.talend.daikon.avro.converter.ConvertShort;
import org.talend.daikon.avro.converter.ConvertUUID;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.avro.converter.SingleColumnIndexedRecordConverter;
import org.talend.daikon.java8.SerializableFunction;
import org.talend.daikon.java8.SerializableSupplier;
import org.talend.daikon.java8.Supplier;

/**
 * An AvroRegistry is used to convert specific datum objects and classes into {@link IndexedRecord}s with {@link Schema}
 * s that can be shared between components for common processing.
 */
public class AvroRegistry {

    /**
     * Registry of ways to discover a Schema from an object.
     */
    private Map<Class<?>, SerializableFunction<?, ?>> mapSchemaInferrer = new HashMap<>();

    /**
     * Registry of objects that know how to convert instances of a class to and from its Avro representation. These
     * contain the common objects shared between all components.
     */
    private static Map<Class<?>, AvroConverter<?, ?>> mapSharedConverter = new HashMap<>();

    /**
     * Registry of objects that know how to convert instances of a class to and from its Avro representation.
     */
    private Map<Class<?>, AvroConverter<?, ?>> mapConverter = new HashMap<>();

    /**
     * Registry of ways to create an IndexedRecord from an object based on its class.
     */
    private static Map<Class<?>, SerializableSupplier<? extends IndexedRecordConverter<?, ?>>> mapSharedIndexedRecordConverter = new HashMap<>();

    /**
     * Registers a reusable mechanism to obtain a {@link Schema} from a specific class of object and makes it available
     * to the {@link #inferSchema(Object)} method.
     *
     * @param datumClass The class of the object that can be introspected to obtain an Avro {@link Schema}.
     * @param inferrer A function that can take an instance of the object and return the {@link Schema} that represents
     * its data.
     */
    public <DatumT> void registerSchemaInferrer(Class<DatumT> datumClass, SerializableFunction<? super DatumT, Schema> inferrer) {
        mapSchemaInferrer.put(datumClass, inferrer);
    }

    static {
        // The Avro types that have direct mappings to primitive types.
        // STRING,BYTES,INT,LONG,FLOAT,DOUBLE,BOOLEAN
        registerSharedPrimitiveClass(String.class, Schema.create(Type.STRING));
        registerSharedPrimitiveClass(ByteBuffer.class, Schema.create(Type.BYTES));
        registerSharedPrimitiveClass(Integer.class, Schema.create(Type.INT));
        registerSharedPrimitiveClass(Long.class, Schema.create(Type.LONG));
        registerSharedPrimitiveClass(Float.class, Schema.create(Type.FLOAT));
        registerSharedPrimitiveClass(Double.class, Schema.create(Type.DOUBLE));
        registerSharedPrimitiveClass(Boolean.class, Schema.create(Type.BOOLEAN));

        // Other classes that have well-defined Avro representations.
        registerSharedPrimitiveClass(Byte.class, new ConvertByte());
        registerSharedPrimitiveClass(Character.class, new ConvertCharacter());
        registerSharedPrimitiveClass(Short.class, new ConvertShort());
        registerSharedPrimitiveClass(BigDecimal.class, new ConvertBigDecimal());
        registerSharedPrimitiveClass(BigInteger.class, new ConvertBigInteger());
        registerSharedPrimitiveClass(Date.class, new ConvertDate());
        registerSharedPrimitiveClass(InetAddress.class, new ConvertInetAddress());
        registerSharedPrimitiveClass(UUID.class, new ConvertUUID());

        // The other Avro types require more information in the schema.
        // RECORD,ENUM,ARRAY,MAP,UNION,FIXED,NULL
    }

    /**
     * Internal utility method to add the primitive class to the shared converters and factory adapters to be reused by
     * all components.
     */
    private static <DatumT> void registerSharedPrimitiveClass(Class<DatumT> primitiveClass, Schema schema) {
        mapSharedConverter.put(primitiveClass, new Unconverted<>(primitiveClass, schema));
        mapSharedIndexedRecordConverter.put(primitiveClass,
                new LambdaSingleColumnIndexedRecordConverterSupplier<>(primitiveClass, schema));
    }

    /**
     * Internal utility method to add the primitive class to the shared converters and {@link IndexedRecordConverter} to
     * be reused by all components.
     */
    private static <DatumT> void registerSharedPrimitiveClass(Class<DatumT> primitiveClass, AvroConverter<DatumT, ?> converter) {
        mapSharedConverter.put(primitiveClass, converter);
        mapSharedIndexedRecordConverter.put(primitiveClass,
                new LambdaSingleColumnIndexedRecordConverterSupplier<>(primitiveClass, converter.getSchema()));
    }

    /**
     * Given an object, attempts to construct an Avro {@link Schema} that corresponds to it. This uses the functions
     * provided by {@link #registerSchemaInferrer(Class, SerializableFunction)} to find the best matching schema based
     * on the class of the incoming data.
     * <p>
     * Classes that use this method should guarantee that the registry knows how to infer {@link Schema}s for its datum
     * classes.
     *
     * @param datum the object.
     * @return The schema for that object if one could be created
     * @throws RuntimeException if a Schema can not be created for the data.
     */
    public <DatumT> Schema inferSchema(DatumT datum) {
        if (datum == null) {
            return null;
        }
        // This is safe because of the contract of the registerSchemaInferrer method.
        SerializableFunction<? super DatumT, Schema> inferrer = getFromClassRegistry(mapSchemaInferrer, datum.getClass());

        // If the inferrer is null, see if we can find a converter (this is useful for primitive types).
        if (inferrer == null) {
            AvroConverter<?, ?> converter = getConverter(datum.getClass());
            if (converter != null) {
                inferrer = new LambdaReturnConstantSchemaFunction<>(converter.getSchema());
            }
            // Remember the inferrer for later.
            mapSchemaInferrer.put(datum.getClass(), inferrer);
        }

        if (inferrer == null) {
            throw new RuntimeException("Cannot infer the schema from " + datum.getClass()); //$NON-NLS-1$
        }

        return inferrer.apply(datum);
    }

    /**
     * Registers a reusable mechanism that can convert between a datum object and a instance that is recognizable in
     * Avro.
     *
     * @param specificClass The class of the object that should be converted.
     * @param avroConverter An instance that can convert to and from instances of the DatumT class to an Avro-compatible
     * instance.
     */
    public <DatumT> void registerConverter(Class<DatumT> specificClass, AvroConverter<DatumT, ?> avroConverter) {
        mapConverter.put(specificClass, avroConverter);
    }

    /**
     * Gets a converter for instances of the given class, capable of converting or wrapping them to Avro-compatible
     * instances.
     *
     * @param datumClass The class of the object that should be converted.
     * @return An instance that can convert to and from instances of the DatumT class to an Avro-compatible instance.
     * This instance can be reused.
     */
    public <T> AvroConverter<T, ?> getConverter(Class<T> datumClass) {
        // If a converter exists, it is guaranteed to be correctly typed because of the register methods.

        // Try to get a private converter first.
        AvroConverter<T, ?> converter = getFromClassRegistry(mapConverter, datumClass);
        if (converter != null) {
            return converter;
        }

        // Fall-back on the shared converters.
        return getFromClassRegistry(mapSharedConverter, datumClass);
    }

    /**
     * Registers a reusable mechanism to obtain a {@link IndexedRecordConverter} for a specific class of object and
     * makes it available to the {@link #createIndexedRecordConverter(Class)} method.
     *
     * @param datumClass The class of the object that the {@link IndexedRecordConverter} knows how to wrap.
     * @param converterClass A class that can take a datum and wrap it into a valid IndexedRecord.
     */
    protected <DatumT, ConverterT extends IndexedRecordConverter<? super DatumT, ?>> void registerIndexedRecordConverter(
            Class<DatumT> datumClass, Class<ConverterT> converterClass) {
        mapSharedIndexedRecordConverter.put(datumClass, new LambdaCreateANewInstanceSupplier<>(datumClass, converterClass));
    }

    /**
     * Registers a reusable mechanism to obtain a {@link IndexedRecordConverter} for a specific class of object and
     * makes it available to the {@link #createIndexedRecordConverter(Class)} method.
     *
     * @param datumClass The class of the object that the {@link IndexedRecordConverter} knows how to wrap.
     * @param converterFactory A supplier that will return a new {@link IndexedRecordConverter} for that object.
     */
    protected <T> void registerIndexedRecordConverter(Class<T> datumClass,
            SerializableSupplier<? extends IndexedRecordConverter<? super T, ?>> converterFactory) {
        mapSharedIndexedRecordConverter.put(datumClass, converterFactory);
    }

    /**
     * Creates a new instance of an {@link IndexedRecordConverter} that can be used to wrap or convert objects of the
     * specified class into {@link IndexedRecord}.
     * <p>
     * As a general rule, when the expected output {@link Schema} of the generated {@link IndexedRecord} is the same for
     * all incoming data, the factory should be cached and reused to optimize its performance.
     *
     * @param datumClass The class of the object that the {@link IndexedRecordConverter} knows how to wrap.
     * @return A new {@link IndexedRecordConverter} that can process instances of the datumClass, or null if none
     * exists.
     */
    public <DatumT> IndexedRecordConverter<? super DatumT, ?> createIndexedRecordConverter(Class<DatumT> datumClass) {
        // This is guaranteed to be correctly typed if it exists, because of the register methods.
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Supplier<? extends IndexedRecordConverter<DatumT, ?>> converter = (Supplier) getFromClassRegistry(
                mapSharedIndexedRecordConverter, datumClass);

        if (converter == null && IndexedRecord.class.isAssignableFrom(datumClass)) {
            @SuppressWarnings("unchecked")
            IndexedRecordConverter<DatumT, ?> unconverted = (IndexedRecordConverter<DatumT, ?>) new UnconvertedIndexedRecordConverter<>();
            return unconverted;
        }

        return converter == null ? null : converter.get();
    }

    /**
     * Given a Map that takes a class and returns an value (likely a functor) that knows how to deal with that class.
     * <p>
     * If there is no exact match on the datum class, all of the super-classes are looked up then all of the interface
     * classes.
     *
     * @param map A map keyed on Class.
     * @param datumClass The class to look up.
     * @return The best matching value from the map, or null if none is found.
     */
    @SuppressWarnings("unchecked")
    public static <T, T2 extends T> T2 getFromClassRegistry(Map<Class<?>, ? extends T> map, Class<?> datumClass) {
        if (datumClass == null) {
            return null;
        }

        T2 match = (T2) map.get(datumClass);
        if (match != null) {
            return match;
        }

        // Attempt to go up through the super classes.
        match = getFromClassRegistry(map, datumClass.getSuperclass());
        if (match != null) {
            return match;
        }

        // Attempt a match from all of the interface classes.
        for (Class<?> iClass : datumClass.getInterfaces()) {
            match = getFromClassRegistry(map, iClass);
            if (match != null) {
                return match;
            }
        }

        return null;
    }

    /**
     * Given a class and a parameter, attempts to create an instance of that object by looking for a constructor.
     * <p>
     * If a constructor with the parameter exists, it is preferred, otherwise the empty constructor is used.
     *
     * @param cls The class to create.
     * @param optionalParam An optional parameter to use while finding the constructor.
     * @return An instance
     * @throws RuntimeException if any errors occurred while creating the instance.
     */
    public static <T> T createANew(Class<T> cls, Object optionalParam) {
        try {
            // Attempt to find a constructor that can be parameterized with the datum
            // class.
            return cls.getConstructor(optionalParam.getClass()).newInstance(optionalParam);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // Ignore any errors if this attempt failed, since we'll try the empty
            // constructor next.
        }
        try {
            // Attempt to find a constructor that can be parameterized with the datum
            // class.
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility class that doesn't perform any conversion.
     */
    public static class Unconverted<T> implements AvroConverter<T, T> {

        private final Class<T> mSpecificClass;

        private final Schema mSchema;

        public Unconverted(Class<T> specificClass, Schema schema) {
            mSpecificClass = specificClass;
            mSchema = schema;
        }

        @Override
        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public Class<T> getDatumClass() {
            return mSpecificClass;
        }

        @Override
        public T convertToDatum(T value) {
            return value;
        }

        @Override
        public T convertToAvro(T value) {
            return value;
        }
    }

    /**
     * Passes through an indexed record without modification.
     */
    public static class UnconvertedIndexedRecordConverter<T extends IndexedRecord>
            implements IndexedRecordConverter<T, IndexedRecord> {

        private Schema mSchema;

        @Override
        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public void setSchema(Schema schema) {
            mSchema = schema;
        }

        @Override
        public Class<T> getDatumClass() {
            return null;
        }

        @Override
        public IndexedRecord convertToAvro(T value) {
            return value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T convertToDatum(IndexedRecord value) {
            return (T) value;
        }
    }

    /**
     * Java 7 implementation for a lambda SerializableFunction.
     */
    private static class LambdaReturnConstantSchemaFunction<DatumT> implements SerializableFunction<DatumT, Schema> {

        /**
         * Default serial version UID.
         */
        private static final long serialVersionUID = 1L;

        private final Schema schema;

        public LambdaReturnConstantSchemaFunction(Schema schema) {
            this.schema = schema;
        }

        @Override
        public Schema apply(DatumT t) {
            return schema;
        }
    }

    /**
     * Java 7 implementation for a lambda SerializableSupplier.
     */
    private static class LambdaSingleColumnIndexedRecordConverterSupplier<DatumT>
            implements SerializableSupplier<SingleColumnIndexedRecordConverter<DatumT>> {

        /**
         * Default serial version UID.
         */
        private static final long serialVersionUID = 1L;

        private final Class<DatumT> primitiveClass;

        private final Schema schema;

        public LambdaSingleColumnIndexedRecordConverterSupplier(Class<DatumT> primitiveClass, Schema schema) {
            this.primitiveClass = primitiveClass;
            this.schema = schema;
        }

        @Override
        public SingleColumnIndexedRecordConverter<DatumT> get() {
            return new SingleColumnIndexedRecordConverter<>(primitiveClass, schema);
        }

    }

    /**
     * Java 7 implementation for a lambda SerializableSupplier.
     */
    private static class LambdaCreateANewInstanceSupplier<DatumT, ConverterT extends IndexedRecordConverter<? super DatumT, ?>>
            implements SerializableSupplier<ConverterT> {

        /**
         * Default serial version UID.
         */
        private static final long serialVersionUID = 1L;

        private final Class<DatumT> datumClass;

        private final Class<ConverterT> converterClass;

        public LambdaCreateANewInstanceSupplier(Class<DatumT> datumClass, Class<ConverterT> converterClass) {
            this.datumClass = datumClass;
            this.converterClass = converterClass;
        }

        @Override
        public ConverterT get() {
            return createANew(converterClass, datumClass);
        }

    }
}
