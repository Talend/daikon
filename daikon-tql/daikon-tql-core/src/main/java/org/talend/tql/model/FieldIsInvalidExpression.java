package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/**
 * Created by gmzoughi on 02/09/16.
 */
public class FieldIsInvalidExpression implements Atom {

    private final TqlElement field;

    public FieldIsInvalidExpression(TqlElement field) {
        this.field = field;
    }

    public TqlElement getField() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldIsInvalidExpression{" + "field='" + field + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
