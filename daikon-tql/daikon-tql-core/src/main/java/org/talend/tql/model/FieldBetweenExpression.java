package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for range matching.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldBetweenExpression implements Atom {

    private final TqlElement field;

    private final LiteralValue left;

    private final LiteralValue right;

    private final boolean isLowerOpen;

    private final boolean isUpperOpen;

    public FieldBetweenExpression(TqlElement field, LiteralValue left, LiteralValue right, boolean isLowerOpen,
            boolean isUpperOpen) {
        this.field = field;
        this.left = left;
        this.right = right;
        this.isLowerOpen = isLowerOpen;
        this.isUpperOpen = isUpperOpen;
    }

    public TqlElement getField() {
        return field;
    }

    public LiteralValue getLeft() {
        return left;
    }

    public LiteralValue getRight() {
        return right;
    }

    public boolean isLowerOpen() {
        return isLowerOpen;
    }

    public boolean isUpperOpen() {
        return isUpperOpen;
    }

    @Override
    public String toString() {
        return "FieldBetweenExpression{" + "field='" + field + '\'' + ", left=" + left + ", right=" + right + ", isLowerOpen="
                + isLowerOpen + ", isUpperOpen=" + isUpperOpen + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldBetweenExpression
                && new EqualsBuilder().append(field, ((FieldBetweenExpression) expression).field)
                        .append(left, ((FieldBetweenExpression) expression).left)
                        .append(right, ((FieldBetweenExpression) expression).right)
                        .append(isLowerOpen, ((FieldBetweenExpression) expression).isLowerOpen)
                        .append(isUpperOpen, ((FieldBetweenExpression) expression).isUpperOpen).isEquals();
    }
}
