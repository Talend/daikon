package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for word pattern compliance.
 */

public class FieldWordCompliesPattern implements Atom {

    private final TqlElement field;

    private final String pattern;

    public FieldWordCompliesPattern(TqlElement field, String pattern) {
        this.field = field;
        this.pattern = pattern;
    }

    public TqlElement getField() {
        return field;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "FieldWordCompliesPattern{" + "field='" + field + '\'' + ", pattern='" + pattern + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldWordCompliesPattern
                && new EqualsBuilder().append(field, ((FieldWordCompliesPattern) expression).field)
                        .append(pattern, ((FieldWordCompliesPattern) expression).pattern).isEquals();
    }
}
