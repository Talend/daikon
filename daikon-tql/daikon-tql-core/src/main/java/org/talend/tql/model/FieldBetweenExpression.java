package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for range matching.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldBetweenExpression implements Atom {

    private final String fieldName;

    private final LiteralValue left;

    private final LiteralValue right;

    public FieldBetweenExpression(String fieldName, LiteralValue left, LiteralValue right) {
        this.fieldName = fieldName;
        this.left = left;
        this.right = right;

    }

    public String getFieldName() {
        return fieldName;
    }

    public LiteralValue getLeft() {
        return left;
    }

    public LiteralValue getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "FieldBetweenExpression{" + "fieldName='" + fieldName + '\'' + ", left='" + left + '\'' + ", right='" + right
                + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
