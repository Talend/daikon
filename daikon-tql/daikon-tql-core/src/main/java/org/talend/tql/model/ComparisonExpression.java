package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql comparison expressions.
 */

/**
 * Created by gmzoughi on 23/06/16.
 */
public class ComparisonExpression implements Atom {

    private final ComparisonOperator operator;

    private final TqlElement field;

    private final TqlElement valueOrField;

    public ComparisonExpression(ComparisonOperator operator, TqlElement field, TqlElement valueOrField) {
        this.operator = operator;
        this.field = field;
        this.valueOrField = valueOrField;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public TqlElement getField() {
        return field;
    }

    public TqlElement getValueOrField() {
        return valueOrField;
    }

    @Override
    public String toString() {
        return "ComparisonExpression{" + "operator=" + operator + ", field=" + field + ", valueOrField=" + valueOrField + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
