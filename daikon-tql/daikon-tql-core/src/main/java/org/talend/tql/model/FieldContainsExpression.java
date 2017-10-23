package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for field containing value.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldContainsExpression implements Atom {

    private final String fieldName;

    private final String value;

    public FieldContainsExpression(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;

    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FieldContainsExpression{" + "fieldName='" + fieldName + '\'' + ", value='" + value + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
