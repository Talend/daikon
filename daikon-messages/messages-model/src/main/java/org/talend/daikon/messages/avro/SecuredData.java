package org.talend.daikon.messages.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

public class SecuredData extends GenericData {

    public static final SecuredData INSTANCE = new SecuredData();

    private SecuredData() {
    }

    public static GenericData get() {
        return INSTANCE;
    }

    /** Renders a Java datum as <a href="http://www.json.org/">JSON</a>. */
    public String toString(Object datum) {
        StringBuilder buffer = new StringBuilder();
        toString(datum, buffer);
        return buffer.toString();
    }

    @Override
    protected void toString(Object datum, StringBuilder buffer) {
        if (isRecord(datum)) {
            buffer.append("{");
            int count = 0;
            Schema schema = getRecordSchema(datum);
            for (Schema.Field f : schema.getFields()) {
                toString(f.name(), buffer);
                buffer.append(": ");
                if (Boolean.valueOf(String.valueOf(f.getObjectProp("secured")))) {
                    buffer.append("<hidden>");
                } else {
                    toString(getField(datum, f.name(), f.pos()), buffer);
                }
                if (++count < schema.getFields().size())
                    buffer.append(", ");
            }
            buffer.append("}");
        } else {
            super.toString(datum, buffer);
        }
    }
}
