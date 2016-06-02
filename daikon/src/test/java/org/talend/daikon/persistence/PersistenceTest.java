package org.talend.daikon.persistence;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

public class PersistenceTest {

    static final String oldSer1 = "{\"@type\":\"org.talend.daikon.persistence.PersistenceTestObject2\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\"}";
    static final String oldSer2 = "{\"@type\":\"org.talend.daikon.persistence.PersistenceTestObject1\",\"string1\":\"string1\",\"string2\":\"string2\",\"string3\":\"string3\",\"inner\":{\"inner1\":\"inner1\",\"inner2\":\"inner2\"}}";

    @Test
    public void testSimple() {
        PersistenceTestObject1 pt1 = new PersistenceTestObject1();
        String ser = Persister.toSerialized(pt1);
        System.out.println(ser);

        Persister.Deserialized<PersistenceTestObject1> deser;
        deser = Persister.fromSerialized(ser, PersistenceTestObject1.class);
        assertTrue(EqualsBuilder.reflectionEquals(pt1, deser.object));
    }

    @Test
    public void testMigrate1() {
        Persister.Deserialized<PersistenceTestObject2> deser;
        deser = Persister.fromSerialized(oldSer1, PersistenceTestObject2.class);
        deser.object.checkMigrate();
    }

    @Test
    public void testMigrate2() {
        Persister.Deserialized<PersistenceTestObject2> deser;
        deser = Persister.fromSerialized(oldSer2, PersistenceTestObject2.class);
        deser.object.checkMigrate();
    }

}
