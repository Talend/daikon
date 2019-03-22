package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Literal value within a Tql expression.
 */

/**
 * Created by gmzoughi on 24/06/16.
 */
public class LiteralValue implements TqlElement {

    private final Enum literal;

    private final String value;

    public LiteralValue(Enum literal, String value) {
        this.literal = literal;
        this.value = value;
    }

    public Enum getLiteral() {
        return literal;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LiteralValue{" + "literal=" + literal + ", value='" + value + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof LiteralValue
                && new EqualsBuilder().append(((LiteralValue) expression).getValue(), this.getValue())
                        .append(((LiteralValue) expression).getLiteral(), this.getLiteral()).isEquals();
    }

    public enum Enum {
        QUOTED_VALUE,
        INT,
        DECIMAL,
        BOOLEAN
    }
}
