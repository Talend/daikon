package org.talend.daikon.persistence;

import static org.junit.Assert.assertEquals;

public class PersistenceTestObject2
        implements MigrationDeletedFieldHandler, MigrationPostDeserializeHandler, MigrationSetVersion {

    public String string1;

    // string2 removed
    // Replaces string2
    public String string2a;

    // Changed
    public String string3;

    // New
    public String string4;

    public PersistenceTestObject2() {
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
    public boolean postDeserialize(int version) {
        if (version < 1) {
            string3 = "XXX" + (String) string3;
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
    }

}
