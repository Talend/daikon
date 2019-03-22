package org.talend.tql.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Logical disjunction of given set of Tql expressions.
 */

/**
 * Created by gmzoughi on 23/06/16.
 */
public class OrExpression implements Expression {

    private final Expression[] expressions;

    public OrExpression(Expression... orExpressions) {
        this.expressions = orExpressions;
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "OrExpression{" + "expressions=" + Arrays.toString(expressions) + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof OrExpression
                && new EqualsBuilder().append(expressions, ((OrExpression) expression).getExpressions()).isEquals();
    }
}
