package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for empty fields.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldIsEmptyExpression implements Atom {

    private String fieldName;

    public FieldIsEmptyExpression(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "FieldIsEmptyExpression{" + "fieldName='" + fieldName + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
