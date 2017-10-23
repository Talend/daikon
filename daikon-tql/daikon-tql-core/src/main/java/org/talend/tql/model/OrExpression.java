package org.talend.tql.model;

import java.util.Arrays;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Logical disjunction of given set of Tql expressions.
 */

/**
 * Created by gmzoughi on 23/06/16.
 */
public class OrExpression implements Expression {

    private final Expression[] expressions;

    public OrExpression(Expression... andExpressions) {
        this.expressions = andExpressions;
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
}
