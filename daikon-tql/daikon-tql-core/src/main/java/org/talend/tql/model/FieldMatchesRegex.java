package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for regular expression matching.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldMatchesRegex implements Atom {

    private final TqlElement field;

    private final String regex;

    public FieldMatchesRegex(TqlElement field, String regex) {
        this.field = field;
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "FieldMatchesRegex{" + "field='" + field + '\'' + ", regex='" + regex + '\'' + '}';
    }

    public TqlElement getField() {
        return field;
    }

    public String getRegex() {
        return regex;
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
