package org.talend.daikon.serialize;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

public class SerializeDeserializeTest {

    static final String oldSer1 = "{\"@type\":\"org.talend.daikon.persistence.PersistenceTestObject2\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\"}";

    static final String oldSer2 = "{\"@type\":\"org.talend.daikon.persistence.PersistenceTestObject1\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\",\"inner\":{\"inner1\":\"inner1\",\"inner2\":\"inner2\"}}";

    @Test
    public void testSimple() {
        PersistenceTestObject1 pt1 = new PersistenceTestObject1();
        String ser = SerializerDeserializer.toSerialized(pt1, SerializerDeserializer.PERSISTENT);
        System.out.println(ser);

        SerializerDeserializer.Deserialized<PersistenceTestObject1> deser;
        deser = SerializerDeserializer.fromSerialized(ser, PersistenceTestObject1.class, SerializerDeserializer.PERSISTENT);
        assertTrue(EqualsBuilder.reflectionEquals(pt1, deser.object));
    }

    @Test
    public void testMigrate1() {
        SerializerDeserializer.Deserialized<PersistenceTestObject2> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer1, PersistenceTestObject2.class, SerializerDeserializer.PERSISTENT);
        deser.object.checkMigrate();
    }

    @Test
    public void testMigrate2() {
        SerializerDeserializer.Deserialized<PersistenceTestObject2> deser;
        deser = SerializerDeserializer.fromSerialized(oldSer2, PersistenceTestObject2.class, SerializerDeserializer.PERSISTENT);
        deser.object.checkMigrate();
    }

}
