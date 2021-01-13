package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for null fields.
 */
public class FieldIsNullExpression implements Atom {

    private TqlElement field;

    public FieldIsNullExpression(TqlElement field) {
        this.field = field;
    }

    public TqlElement getField() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldIsNullExpression{" + "field='" + field + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldIsNullExpression
                && new EqualsBuilder().append(field, ((FieldIsNullExpression) expression).field).isEquals();
    }
}
