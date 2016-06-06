package org.talend.daikon.serialize;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SerializeDeserializeTest {

    static final String oldSer1 = "{\"@type\":\"org.talend.daikon.serialize.PersistenceTestObject\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\","
            + "\"inner\":{\"string1\":\"string1\",\"string2\":\"string2\","
            + "\"innerObject2\":{\"string1\":\"string1\",\"string2\":\"string2\"}}}";

    @Test
    public void testSimple() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObject pt = new PersistenceTestObject();
        pt.setup();
        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(ser, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        pt.checkEqual(deser.object);
        assertFalse(deser.migration.isMigrated());
    }

    @Test
    public void testVersion() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObject pt = new PersistenceTestObject();
        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(ser, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        assertFalse(deser.migration.isMigrated());
        pt.checkEqual(deser.object);
    }

    @Test
    public void testMigrate1() {
        PersistenceTestObject.testMigrate = true;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        assertTrue(deser.migration.isMigrated());
        deser.object.checkMigrate();

        String ser = SerializerDeserializer.toSerialized(deser.object, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);
        assertTrue(ser.contains("__version\":1"));
    }

    @Test
    public void testMigrateInnerOnly() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = true;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        assertTrue(deser.migration.isMigrated());
        deser.object.checkMigrate();
    }

    @Test
    public void testMigrateInnerOnlyDeleted() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = true;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        assertTrue(deser.migration.isMigrated());
        deser.object.checkMigrate();
    }

    @Test
    public void testMigrateInnerOnlyNoDelete() {
        PersistenceTestObject.testMigrate = false;
        PersistenceTestObjectInner2.deserializeMigration = false;
        PersistenceTestObjectInner2.deleteMigration = false;
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        assertFalse(deser.migration.isMigrated());
        deser.object.checkMigrate();
    }

}
