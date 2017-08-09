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

import java.util.Map;

import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.serialize.jsonschema.UiSchemaConstants;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Class for ui fields setting in ui schema, passed to ui.
 */
public class Mapper {

    /**
     * ui attribute to be set for the ui widget. It can be a {@link UiSchemaConstants#TAG_WIDGET} or
     * {@link UiSchemaConstants#TAG_CUSTOM_WIDGET}
     */
    protected final String uiAttribute;

    /**
     * 'ui:widget' or 'ui:field' attribute value.
     */
    protected final String uiType;

    /**
     * 
     * @param uiAttribute - ui attribute to be set for the ui widget. It can be a {@link UiSchemaConstants#TAG_WIDGET}
     * or {@link UiSchemaConstants#TAG_CUSTOM_WIDGET}
     * @param uiType - 'ui:widget' or 'ui:field' attribute value.
     */
    public Mapper(String uiAttribute, String uiType) {
        this.uiAttribute = uiAttribute;
        this.uiType = uiType;
    }

    /**
     * Process widget to set all the required attributes to ui schema.
     */
    public void processWidget(Widget widget, ObjectNode schema, boolean[] hasVisible) {
        if (widget.isAutoFocus()) {
            schema.put(UiSchemaConstants.TAG_AUTO_FOCUS, true);
        }
        setType(schema);
        processOptions(widget, schema);
        // Any other widget type than hidden causes means that it is visible.
        if (!UiSchemaConstants.TYPE_HIDDEN.equals(uiType)) {
            hasVisible[0] = true;
        }
    }

    /**
     * Set ui attribute value to schema
     */
    protected void setType(ObjectNode schema) {
        schema.put(uiAttribute, uiType);
    }

    /**
     * Set all required options to ui schema for widget
     */
    protected void processOptions(Widget widget, ObjectNode schema) {
        Map<String, String> optionsMap = UiSchemaConstants.getWidgetOptionsMapping().get(widget.getWidgetType());
        if (optionsMap != null) {
            ObjectNode options = JsonNodeFactory.instance.objectNode();

            for (Map.Entry<String, String> entry : optionsMap.entrySet()) {
                if (widget.getConfigurationValue(entry.getKey()) != null) {
                    options.put(entry.getKey(), widget.getConfigurationValue(entry.getKey()).toString());
                } else {
                    options.put(entry.getKey(), entry.getValue());
                }
            }

            schema.set(UiSchemaConstants.TAG_OPTIONS, options);
        }
    }
}
