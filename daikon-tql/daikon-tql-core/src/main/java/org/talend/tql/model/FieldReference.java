package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/*
 * Field reference within a Tql expression.
 */

/**
 * Created by bguillon on 24/06/16.
 */
public class FieldReference implements TqlElement {

    private final String path;

    public FieldReference(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "FieldReference{" + "path='" + path + '\'' + '}';
    }

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
