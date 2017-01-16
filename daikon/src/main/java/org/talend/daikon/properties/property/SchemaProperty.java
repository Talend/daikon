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
package org.talend.daikon.properties.property;

import org.apache.avro.Schema;

/**
 * Schema Property that get and set an Avro Schema but store a String internally for serialization optimization. The set
 * Value accepts both Schema and json string for schema. The evaluator is also called with the Schema instance.
 * this is not only for optimization, because json-io fails to deserialize the avro schema here is the explanation.
 * 1. Properties.fromSerializedPersistent() tries to deserialize component properties instance from JSON string.
 * It uses Json-io library.<br>
 * 2. During deserialization Json-io library tries to create instance of Schema$RecordSchema.<br>
 * 3. Schema$RecordSchema has 2 constructors<br>
 * RecordSchema(Name name, String doc, boolean isError)<br>
 * RecordSchema(Name name, String doc, boolean isError, List<Field> fields)<br>
 * 4. Json-io passes default values for primitives and null for objects as arguments:<br>
 * RecordSchema(null, null, false)<br>
 * RecordSchema(null, null, false, null)<br>
 * 5. Some exception is thrown in both constructors.<br>
 * 6. Json-io can't create instance and throws its own exception.<br>
 */
// FIXME - move this back to TCOMP, see TDKN-66
public class SchemaProperty extends Property<Schema> {

    private static final long serialVersionUID = 381787688468063761L;

    public SchemaProperty(String name) {
        super(Schema.class, name);
    }

    SchemaProperty(String type, String name) {
        super(type, name);
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
