package org.talend.tql.model;

/*
 * Boolean values in Tql expressions.
 */

/**
 * Created by gmzoughi on 23/06/16.
 */
public class BooleanValue extends LiteralValue {

    public BooleanValue(String value) {
        super(Enum.BOOLEAN, value);
    }
}
