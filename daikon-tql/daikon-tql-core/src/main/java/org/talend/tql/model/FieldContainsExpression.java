package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for field containing value.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldContainsExpression implements Atom {

    private final TqlElement field;

    private final String value;

    private final boolean caseSensitive;

    public FieldContainsExpression(TqlElement field, String value) {
        this.field = field;
        this.value = value;
        this.caseSensitive = true;
    }

    public FieldContainsExpression(TqlElement field, String value, boolean caseSensitive) {
        this.field = field;
        this.value = value;
        this.caseSensitive = caseSensitive;
    }

    public TqlElement getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    @Override
    public String toString() {
        return "FieldContainsExpression{" + "field='" + field + '\'' + ", value='" + value + '\'' + ", caseSensitive="
                + caseSensitive + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldContainsExpression
                && new EqualsBuilder().append(field, ((FieldContainsExpression) expression).field)
                        .append(value, ((FieldContainsExpression) expression).value)
                        .append(caseSensitive, ((FieldContainsExpression) expression).caseSensitive).isEquals();
    }
}
