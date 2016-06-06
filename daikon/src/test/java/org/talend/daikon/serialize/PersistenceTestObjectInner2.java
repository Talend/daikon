package org.talend.daikon.serialize;

public class PersistenceTestObjectInner2 implements DeserializeDeletedFieldHandler, PostDeserializeHandler, SerializeSetVersion {

    public static boolean deserializeMigration;
    public static boolean deleteMigration;

    // Changed
    public String string1;

    // Deleted
    // public String string2;

    // replaces deleted string2
    public String string2a;

    public PersistenceTestObjectInner2() {
    }

    public void setup() {
        string1 = "string1";
        // string2 = "string2";
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
        if (deserializeMigration) {
            if (version < 1) {
                string1 = "XXX" + string1;
                return true;
            }
        }
        return false;
    }

    // Migrate to new string2a which replaces string2
    public boolean fieldDeleted_string2(Object value) {
        string2a = (String) value;
        return deleteMigration;
    }

    public void checkMigrate() {
        if (deserializeMigration)
            assert ("XXXstring1".equals(string1));
        assert ("string2".equals(string2a));
    }

}