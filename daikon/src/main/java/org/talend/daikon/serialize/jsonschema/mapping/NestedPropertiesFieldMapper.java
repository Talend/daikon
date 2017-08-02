// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.serialize.jsonschema.mapping;

import org.talend.daikon.serialize.jsonschema.JsonSchemaConstants;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Mapper for Nested properties ui:field.
 */
public class NestedPropertiesFieldMapper extends FieldMapper {

    public NestedPropertiesFieldMapper(String uiType) {
        super(uiType);
    }

    @Override
    protected void setType(ObjectNode schema) {
        ObjectNode items = JsonNodeFactory.instance.objectNode();
        schema.set(JsonSchemaConstants.TAG_ITEMS, items);
        super.setType(items);
    }

}