package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Tql expression for pattern compliance.
 */

/**
 * Created by bguillon on 23/06/16.
 */
public class FieldCompliesPattern implements Atom {

    private final String fieldName;

    private final String pattern;

    public FieldCompliesPattern(String fieldName, String pattern) {
        this.fieldName = fieldName;
        this.pattern = pattern;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "FieldCompliesPattern{" + "fieldName='" + fieldName + '\'' + ", pattern='" + pattern + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
