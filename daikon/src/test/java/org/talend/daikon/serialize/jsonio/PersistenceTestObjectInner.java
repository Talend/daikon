package org.talend.daikon.serialize.jsonio;

public class PersistenceTestObjectInner {

    public String string1;

    public String string2;

    public PersistenceTestObjectInner2 innerObject2;

    public void setup() {
        string1 = "string1";
        string2 = "string2";
        innerObject2 = new PersistenceTestObjectInner2();
        innerObject2.setup();
    }

    public void checkMigrate() {
        innerObject2.assertMigrateOk();
    }

}
