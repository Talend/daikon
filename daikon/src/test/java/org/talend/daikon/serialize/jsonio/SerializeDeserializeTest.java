package org.talend.daikon.serialize.jsonio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.serialize.SerializerDeserializer;
import org.talend.daikon.serialize.SerializerDeserializer.Deserialized;

import com.cedarsoftware.util.io.JsonWriter;

import shaded.org.apache.commons.io.IOUtils;

public class SerializeDeserializeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializeDeserializeTest.class);

    static final private String oldSer1 = "{\"@type\":\"org.talend.daikon.serialize.jsonio.PersistenceTestObject\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\",\"inner\":{\"string1\":\"string1\",\"string2\":\"string2\",\"innerObject2\":{\"string1\":\"string1\",\"string2\":\"string2\",\"innerObject3\":{\"string1\":\"string1\",\"string2\":\"string2\"}}}}";

    @Test
    public void testSimple() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObject pt = new PersistenceTestObject();
        pt.setup();
        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT);
        LOGGER.info(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerializedPersistent(ser, PersistenceTestObject.class);
        pt.checkEqual(deser.object);
        assertFalse(deser.migrated);
    }

    @Test
    public void testVersion() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObject pt = new PersistenceTestObject();
        String ser = SerializerDeserializer.toSerializedPersistent(pt);
        LOGGER.info(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerializedPersistent(ser, PersistenceTestObject.class);
        assertFalse(deser.migrated);
        pt.checkEqual(deser.object);
    }

    @Test
    public void testMigrate1() {
        PersistenceTestObject.testMigrate = true;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, null,
                SerializerDeserializer.PERSISTENT);
        assertTrue(deser.migrated);
        deser.object.checkMigrate();

        String ser = SerializerDeserializer.toSerializedPersistent(deser.object);
        LOGGER.info(ser);
        assertTrue(ser.contains("__version\":1"));
        assertTrue(deser.object.inner.innerObject2.hasNullDeleteInner3);
        assertFalse(deser.object.inner.innerObject2.hasValuedDeleteInner3);
    }

    @Test
    public void testMigrateInnerOnly() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = true;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerializedPersistent(oldSer1, PersistenceTestObject.class);
        assertTrue(deser.migrated);
        deser.object.checkMigrate();
        assertTrue(deser.object.inner.innerObject2.hasNullDeleteInner3);
        assertFalse(deser.object.inner.innerObject2.hasValuedDeleteInner3);
    }

    @Test
    public void testMigrateInnerOnlyDeleted() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = true;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerializedPersistent(oldSer1, PersistenceTestObject.class);
        assertTrue(deser.migrated);
        deser.object.checkMigrate();
        assertTrue(deser.object.inner.innerObject2.hasNullDeleteInner3);
        assertFalse(deser.object.inner.innerObject2.hasValuedDeleteInner3);
    }

    @Test
    public void testMigrateInnerOnlyNoDelete() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerializedPersistent(oldSer1, PersistenceTestObject.class);
        assertFalse(deser.migrated);
        deser.object.checkMigrate();
        assertTrue(deser.object.inner.innerObject2.hasNullDeleteInner3);
        assertFalse(deser.object.inner.innerObject2.hasValuedDeleteInner3);
    }

    @Test
    public void testJsonIoWithNoTypeAndStream() throws IOException {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObject pt = new PersistenceTestObject();
        pt.setup();
        final Map<String, Object> jsonIoOptions = new HashMap<>();
        jsonIoOptions.put(JsonWriter.TYPE, false);

        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT, jsonIoOptions);
        LOGGER.info(ser);

        try (InputStream is = IOUtils.toInputStream(ser, "UTF-8")) {
            SerializerDeserializer.Deserialized<PersistenceTestObject> deser = SerializerDeserializer.fromSerialized(is,
                    PersistenceTestObject.class, null, true);
            pt.checkEqual(deser.object);
        }
    }

    @Test
    public void testDeserializeMissingFieldTyped() {
        String serTyped = "{\"@type\":\"org.talend.daikon.serialize.jsonio.PersistenceTestObject\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\",\"inner\":{\"string1\":\"string1\",\"string2\":\"string2\",\"innerObject2\":{\"string1\":\"string1\",\"string2\":\"string2\",\"innerObject3\":{\"@type\": \"org.talend.daikon.serialize.PersistenceTestObjectInner3\",\"string1\":\"string1\",\"string2\":\"string2\"}}}}";
        Deserialized<PersistenceTestObject> deser = SerializerDeserializer.fromSerializedPersistent(serTyped,
                PersistenceTestObject.class);
        assertFalse(deser.object.inner.innerObject2.hasNullDeleteInner3);
        assertTrue(deser.object.inner.innerObject2.hasValuedDeleteInner3);

    }

    @Test
    public void testDeserializeMissingFieldTypedWithComplexTypeWithRef() {
        String serTyped = "{\"@type\":\"org.talend.daikon.serialize.jsonio.PersistenceObjectForFieldRemoved\",\"persObjStr\":\"persObjStrValue\",\"in1\":{\"@id\":1,\"inner1Str\":\"inner1Str\"},\"in2Old\":{\"@type\":\"org.talend.daikon.serialize.jsonio.PersistenceObjectForFieldRemoved$InnerClass2\",\"inner2Str\":\"inner2Str\",\"in1inIn2\":{\"@ref\":1}},\"isdeletedFieldIn2_right_type\":false}";
        // generate the above serialized string, but it is then manually altered to rename a field
        // PersistenceObjectForFieldRemoved persistenceObjectForFieldRemoved = new PersistenceObjectForFieldRemoved();
        // String ser = SerializerDeserializer.toSerializedPersistent(persistenceObjectForFieldRemoved);
        // LOGGER.info(ser);

        Deserialized<PersistenceObjectForFieldRemoved> deser = SerializerDeserializer.fromSerializedPersistent(serTyped,
                PersistenceObjectForFieldRemoved.class);
        assertTrue(deser.object.isdeletedFieldIn2_right_type);
    }

}
