package org.talend.daikon.persistence;

public class PersistenceTestObject1 {

    public String string1;

    public String string2;

    public String string3;

    public PersistenceTestObjectInner inner;

    public PersistenceTestObject1() {
        string1 = "string1";
        string2 = "string2";
        string3 = "string3";
        inner = new PersistenceTestObjectInner();
    }
}
