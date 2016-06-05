package org.talend.daikon.serialize;

import org.junit.Test;

public class SerializeDeserializeTest {

    static final String oldSer1 = "{\"@type\":\"org.talend.daikon.serialize.PersistenceTestObject\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\","
            + "\"inner\":{\"string1\":\"string1\",\"string2\":\"string2\","
            + "\"innerObject2\":{\"string1\":\"string1\",\"string2\":\"string2\"}}}";

    @Test
    public void testSimple() {
        PersistenceTestObject pt = new PersistenceTestObject();
        pt.setup();
        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(ser, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        pt.checkEqual(deser.object);
    }

    @Test
    public void testVersion() {
        PersistenceTestObject pt = new PersistenceTestObject();
        String ser = SerializerDeserializer.toSerialized(pt, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(ser, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        pt.checkEqual(deser.object);
    }

    @Test
    public void testMigrate1() {
        SerializerDeserializer.Deserialized<PersistenceTestObject> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject.class, SerializerDeserializer.PERSISTENT);
        deser.object.checkMigrate();
    }

}
