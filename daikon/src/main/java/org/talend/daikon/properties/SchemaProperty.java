// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;

/**
 * Schema Property that get and set an Avro Schema but store a String internally for serialization optimization. The set
 * Value accepts both Schema and json string for schema. The evaluator is also called with the Schema instance.
 */
public class SchemaProperty extends Property<Schema> implements AnyProperty {

    public SchemaProperty(String name) {
        this(name, null);
    }

    public SchemaProperty(String name, String title) {
        super(new TypeLiteral<Schema>() {// empty on purpose
        }, name, title);
    }

    @Override
    public Property<Schema> setValue(Schema value) {
        // convert to string to optimize serialization
        storedValue = (value == null ? null : value.toString());
        return this;
    }

    /**
     * @return the value of the property. This value may not be the one Stored with setValue(), it may be evaluated with
     * {@link PropertyValueEvaluator}.
     * 
     * 
     */
    @Override
    public Schema getValue() {
        Schema returnValue = null;
        if (storedValue != null) {
            returnValue = new Schema.Parser().parse(storedValue.toString());
            if (propertyValueEvaluator != null) {
                returnValue = propertyValueEvaluator.evaluate(this, returnValue);
            } // else not evaluator so return the storedValue
        }
        return returnValue;
    }

}
