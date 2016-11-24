package org.talend.daikon.serialize.jsonio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonWriter;

import org.talend.daikon.serialize.SerializerDeserializer;
import shaded.org.apache.commons.io.IOUtils;

public class SerializeDeserializeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializeDeserializeTest.class);

    static final String oldSer1 = "{\"@type\":\"org.talend.daikon.serialize.jsonio.PersistenceTestObject\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\","
            + "\"inner\":{\"string1\":\"string1\",\"string2\":\"string2\","
            + "\"innerObject2\":{\"string1\":\"string1\",\"string2\":\"string2\"}}}";

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
}
