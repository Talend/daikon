package org.talend.daikon.serialize.jsonio;

import org.talend.daikon.serialize.DeserializeDeletedFieldHandler;
import org.talend.daikon.serialize.PostDeserializeHandler;
import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.SerializeSetVersion;

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
    public boolean deletedField(String fieldName, Object value) {
        if (fieldName.equals("string2")) {
            string2a = (String) value;
        }
        return deleteMigration;
    }

    public void checkMigrate() {
        if (deserializeMigration)
            assert ("XXXstring1".equals(string1));
        assert ("string2".equals(string2a));
    }

}
