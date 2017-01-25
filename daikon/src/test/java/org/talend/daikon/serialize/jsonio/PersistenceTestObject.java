
package org.talend.daikon.serialize.jsonio;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.daikon.serialize.DeserializeDeletedFieldHandler;
import org.talend.daikon.serialize.PostDeserializeHandler;
import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.SerializeSetVersion;

public class PersistenceTestObject implements DeserializeDeletedFieldHandler, PostDeserializeHandler, SerializeSetVersion {

    public static boolean testMigrate;

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
        inner.setup();
    }

    public boolean checkEqual(PersistenceTestObject other) {
        return EqualsBuilder.reflectionEquals(this, other, "inner") | EqualsBuilder.reflectionEquals(inner, other.inner, "inner2")
                | EqualsBuilder.reflectionEquals(inner.innerObject2, other.inner.innerObject2);
    }

    //
    // Migration
    //

    @Override
    public int getVersionNumber() {
        if (testMigrate) {
            // Version 1 has modified string3
            return 1;
        }
        return 0;
    }

    // In place change to string3
    @Override
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {
        if (testMigrate) {
            if (version < 1) {
                string3 = "XXX" + string3;
                return true;
            }
        }
        return false;
    }

    // Migrate to new string2a which replaces string2
    @Override
    public boolean deletedField(String fieldName, Object value) {
        if (testMigrate) {
            if ("string2".equals(fieldName)) {
                string2a = (String) value;
                return true;
            }
        }
        return false;
    }

    public void checkMigrate() {
        assertEquals("string1", string1);
        assertEquals(null, string4);
        if (testMigrate) {
            assertEquals("string2", string2a);
            assertEquals("XXXstring3", string3);
        } else {
            assertEquals(null, string2a);
            assertEquals("string3", string3);
        }
        inner.checkMigrate();
    }

}
