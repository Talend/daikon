package org.talend.tql.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.talend.tql.visitor.IASTVisitor;

/**
 * Created by gmzoughi on 02/09/16.
 */
public class FieldIsValidExpression implements Atom {

    private final TqlElement field;

    public FieldIsValidExpression(TqlElement field) {
        this.field = field;
    }

    public TqlElement getField() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldIsValidExpression{" + "field='" + field + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object expression) {
        return expression instanceof FieldIsValidExpression
                && new EqualsBuilder().append(field, ((FieldIsValidExpression) expression).field).isEquals();
    }
}
