package org.talend.tql.model;

import java.util.Arrays;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for field in the set of the specified values.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldInExpression implements Atom {

    private final String fieldName;

    private final LiteralValue[] values;

    public FieldInExpression(String fieldName, LiteralValue[] values) {
        this.fieldName = fieldName;
        this.values = values;
    }

    public String getFieldName() {
        return fieldName;
    }

    public LiteralValue[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "FieldInExpression{" + "fieldName='" + fieldName + '\'' + ", values=" + Arrays.toString(values) + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
