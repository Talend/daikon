package org.talend.daikon.serialize.jsonio;

import org.talend.daikon.serialize.DeserializeDeletedFieldHandler;

public class PersistenceObjectForFieldRemoved implements DeserializeDeletedFieldHandler {

    public String persObjStr = "persObjStrValue";

    public InnerClass1 in1 = new InnerClass1();

    public Object in2;// we don't use Inner2 to have an @type in the serialization

    public boolean isdeletedFieldIn2_right_type;

    public static class InnerClass1 {

        public String inner1Str = "inner1Str";
    }

    public static class InnerClass2 {

        public String inner2Str = "inner2Str";

        public InnerClass1 in1inIn2;
    }

    public PersistenceObjectForFieldRemoved() {
        InnerClass2 aIn2 = new InnerClass2();
        aIn2.in1inIn2 = in1;
        in2 = aIn2;
    }

    @Override
    public boolean deletedField(String fieldName, Object value) {
        boolean migrated = false;
        if ("in2Old".equals(fieldName)) {
            isdeletedFieldIn2_right_type = (value != null && (value instanceof InnerClass2));
            migrated = true;
        }
        return migrated;
    }
}
