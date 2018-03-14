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

import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesList;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.serialize.jsonschema.JsonSchemaConstants;
import org.talend.daikon.serialize.jsonschema.UiSchemaConstants;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Mapper for Nested properties ui:field.
 */
public class NestedPropertiesFieldMapper extends FieldMapper {

    public NestedPropertiesFieldMapper(String uiType) {
        super(uiType);
    }

    public NestedPropertiesFieldMapper(String uiAttribute, String uiType) {
        super(uiAttribute, uiType);
    }

    protected void setItems(ObjectNode schema, Widget widget) {
        // Create item tag
        ObjectNode items = JsonNodeFactory.instance.objectNode();
        schema.set(JsonSchemaConstants.TAG_ITEMS, items);
        // Add ui:field
        super.setType(items);

        // Process sub widgets
        PropertiesList<?> propertiesList = (PropertiesList<?>) widget.getContent();
        Properties defaultProp = propertiesList.getDefaultProperties();
        List<NamedThing> nestedThings = defaultProp.getProperties();

        ArrayNode uiOrderNode = JsonNodeFactory.instance.arrayNode();

        for (NamedThing namedThing : nestedThings) {

            uiOrderNode.add(namedThing.getName());

            Widget currentWidget = defaultProp.getPreferredForm(Form.MAIN).getWidget(namedThing);
            Mapper uiMapper = UiSchemaConstants.getUiMappers().get(currentWidget.getWidgetType());

            // Add ui:widget subtag if particular widget needed
            if (uiMapper != null) {
                ObjectNode widgetNode = JsonNodeFactory.instance.objectNode();
                widgetNode.put(UiSchemaConstants.TAG_WIDGET, uiMapper.uiType);
                items.set(namedThing.getName(), widgetNode);
            }
        }

        // Add ui:order
        items.set(UiSchemaConstants.TAG_ORDER, uiOrderNode);
        // Add ui:options
        processOptions(widget, items);
    }

    @Override
    public void processWidget(Widget widget, ObjectNode schema, boolean[] hasVisible) {
        if (widget.isAutoFocus()) {
            schema.put(UiSchemaConstants.TAG_AUTO_FOCUS, true);
        }
        setItems(schema, widget);
        // Any other widget type than hidden causes means that it is visible.
        if (!UiSchemaConstants.TYPE_HIDDEN.equals(uiType)) {
            hasVisible[0] = true;
        }
    }

}