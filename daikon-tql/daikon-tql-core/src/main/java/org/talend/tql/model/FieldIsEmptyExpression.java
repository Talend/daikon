package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for empty fields.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldIsEmptyExpression implements Atom {

    private TqlElement field;

    public FieldIsEmptyExpression(TqlElement field) {
        this.field = field;
    }

    public TqlElement getField() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldIsEmptyExpression{" + "field='" + field + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldIsEmptyExpression
                && new EqualsBuilder().append(field, ((FieldIsEmptyExpression) expression).field).isEquals();
    }
}
