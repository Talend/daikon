package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for regular expression matching.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldMatchesRegex implements Atom {

    private final String fieldName;

    private final String regex;

    public FieldMatchesRegex(String fieldName, String regex) {
        this.fieldName = fieldName;
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "FieldMatchesRegex{" + "fieldName='" + fieldName + '\'' + ", regex='" + regex + '\'' + '}';
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getRegex() {
        return regex;
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
