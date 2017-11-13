package org.talend.tql.model;

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

    public FieldContainsExpression(TqlElement field, String value) {
        this.field = field;
        this.value = value;

    }

    public TqlElement getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FieldContainsExpression{" + "field='" + field + '\'' + ", value='" + value + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
