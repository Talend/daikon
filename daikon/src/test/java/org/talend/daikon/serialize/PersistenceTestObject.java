package org.talend.daikon.serialize;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class PersistenceTestObject implements DeserializeDeletedFieldHandler, PostDeserializeHandler, SerializeSetVersion {

    public String string1;

    // string2 removed
    // public String string2;

    // Replaces string2
    public String string2a;

    // Changed
    public String string3;

    // New
    public String string4;

    public PersistenceTestObjectInner inner;

    public PersistenceTestObject() {
        inner = new PersistenceTestObjectInner();
    }

    public void setup() {
        // Original values
        string1 = "string1";
        // string2 = "string2";
        string3 = "string3";
        inner = new PersistenceTestObjectInner();
        inner.setup();
    }

    public boolean checkEqual(PersistenceTestObject other) {
        return EqualsBuilder.reflectionEquals(this, other, "inner")
                | EqualsBuilder.reflectionEquals(inner, other.inner, "inner2")
                | EqualsBuilder.reflectionEquals(inner.innerObject2, other.inner.innerObject2);
    }

    //
    // Migration
    //

    @Override
    public int getVersionNumber() {
        // Version 1 has modified string3
        return 1;
    }

    // In place change to string3
    @Override
    public boolean postDeserialize(int version, boolean persistent) {
        if (version < 1) {
            string3 = "XXX" + string3;
            return true;
        }
        return false;
    }

    // Migrate to new string2a which replaces string2
    public void fieldDeleted_string2(int version, Object value) {
        string2a = (String) value;
    }

    public void checkMigrate() {
        assertEquals("string1", string1);
        assertEquals("string2", string2a);
        assertEquals("XXXstring3", string3);
        assertEquals(null, string4);
        inner.checkMigrate();
    }

}
