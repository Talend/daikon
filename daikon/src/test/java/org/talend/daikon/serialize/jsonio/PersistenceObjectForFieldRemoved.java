package org.talend.daikon.serialize.jsonio;

import org.talend.daikon.serialize.DeserializeDeletedFieldHandler;

public class PersistenceObjectForFieldRemoved implements DeserializeDeletedFieldHandler {

    public static class InnerClass1 {

        String inner1Str = "inner1Str";
    }

    public static class InnerClass2 {

        String inner2Str = "inner2Str";

        InnerClass1 in1inIn2;
    }

    String persObjStr = "persObjStrValue";

    InnerClass1 in1 = new InnerClass1();

    Object in2;// we don't use Inner2 to have an @type in the serialization

    public boolean isdeletedFieldIn2_right_type;

    public PersistenceObjectForFieldRemoved() {
        InnerClass2 aIn2 = new InnerClass2();
        aIn2.in1inIn2 = in1;
        in2 = aIn2;
    }

    @Override
    public boolean deletedField(String fieldName, Object value) {
        boolean migrated = false;
        if (fieldName.equals("in2Old")) {
            isdeletedFieldIn2_right_type = (value != null && (value instanceof InnerClass2));
            migrated = true;
        }
        return migrated;
    }
}
