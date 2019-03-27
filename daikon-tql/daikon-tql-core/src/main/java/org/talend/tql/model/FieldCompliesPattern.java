package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for pattern compliance.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldCompliesPattern implements Atom {

    private final TqlElement field;

    private final String pattern;

    public FieldCompliesPattern(TqlElement field, String pattern) {
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
        return "FieldCompliesPattern{" + "field='" + field + '\'' + ", pattern='" + pattern + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldCompliesPattern
                && new EqualsBuilder().append(field, ((FieldCompliesPattern) expression).field)
                        .append(pattern, ((FieldCompliesPattern) expression).pattern).isEquals();
    }
}
