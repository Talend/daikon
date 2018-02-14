package org.talend.daikon.logging.kafka;

import org.talend.schema.model.AbstractMessageIdentifier;

public class MessageIdentifierImpl extends AbstractMessageIdentifier {

    private String field1;

    private String field2;

    public MessageIdentifierImpl() {
    }

    public MessageIdentifierImpl(String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MessageIdentifierImpl that = (MessageIdentifierImpl) o;

        if (!field1.equals(that.field1)) {
            return false;
        }
        return field2.equals(that.field2);
    }

    @Override
    public int hashCode() {
        int result = field1.hashCode();
        result = 31 * result + field2.hashCode();
        return result;
    }
}
