package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/**
 * Created by gmzoughi on 02/09/16.
 */
public class FieldIsValidExpression implements Atom {

    private String fieldName;

    public FieldIsValidExpression(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "FieldIsValidExpression{" + "fieldName='" + fieldName + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
