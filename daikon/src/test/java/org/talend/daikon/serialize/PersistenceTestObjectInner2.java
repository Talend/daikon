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

    public transient boolean hasNullDeleteInner3 = false;

    public transient boolean hasValuedDeleteInner3 = false;

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
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {
        if (deserializeMigration) {
            if (version < 1) {
                string1 = "XXX" + string1;
                return true;
            }
        }
        return false;
    }

    // Migrate to new string2a which replaces string2
    @Override
    public boolean deletedField(String fieldName, Object value) {
        if (fieldName.equals("string2")) {
            string2a = (String) value;
        } else if (fieldName.equals("innerObject3")) {
            hasNullDeleteInner3 = value == null;
            hasValuedDeleteInner3 = value instanceof PersistenceTestObjectInner3;
        }
        return deleteMigration;
    }

    public void assertMigrateOk() {
        if (deserializeMigration) {
            assert("XXXstring1".equals(string1));
        }
        assert("string2".equals(string2a));
    }

}