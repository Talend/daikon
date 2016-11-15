package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UiSchemaGenerator {

    protected <T extends Properties> ObjectNode genWidget(T properties) {
        return processTPropertiesWidget(properties);
    }

    /**
     * Generate UISchema by the given ComponentProperties and relate Form/Widget Only consider Main and Advanced Form
     *
     * @param cProperties
     * @return UISchema
     */
    private ObjectNode processTPropertiesWidget(Properties cProperties) {
        Form mainForm = cProperties.getForm(Form.MAIN);
        Form advancedForm = cProperties.getForm(Form.ADVANCED);
        return processTPropertiesWidget(mainForm, advancedForm);
    }

    /**
     * ComponentProeprties could use multiple forms in one time to represent the graphic setting, Main & Advanced for
     * instance. ComponentProperties could has Properties/Property which are not in Form, treat it as hidden
     * Properties/Property
     *
     * @param forms
     * @return
     */
    private ObjectNode processTPropertiesWidget(Form... forms) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        if (forms.length <= 0) {
            return schema;
        }

        List<JsonWidget> jsonWidgets = new ArrayList<>();
        for (Form form : forms) {
            if (form != null) {
                jsonWidgets.addAll(listTypedWidget(form));
            }
        }

        // Merge widget in Main and Advanced form together, need the merged order.
        Map<Integer, String> order = new TreeMap<>();

        // all the forms should in same ComponentProperties, so use the first form to get the ComponentProperties is ok.
        Properties cProperties = forms[0].getProperties();
        List<Property> propertyList = getSubProperty(cProperties);
        List<Properties> propertiesList = getSubProperties(cProperties);

        for (JsonWidget jsonWidget : jsonWidgets) {
            NamedThing content = jsonWidget.getContent();
            if (propertyList.contains(content) || content instanceof PresentationItem) {
                ObjectNode jsonNodes = processTWidget(jsonWidget.getWidget(), JsonNodeFactory.instance.objectNode());
                if (jsonNodes.size() != 0) {
                    schema.set(jsonWidget.getName(), jsonNodes);
                }
                order.put(jsonWidget.getOrder(), jsonWidget.getName());
            } else { // nested Form or Properties
                Properties checkProperties = null;
                Form resolveForm = null;
                if (content instanceof Form) {
                    // ComponentProperties could contains multiple type of Form, form in widget is the current used
                    resolveForm = (Form) content;
                    checkProperties = resolveForm.getProperties();
                } else {
                    checkProperties = (Properties) content;
                    resolveForm = checkProperties.getForm(Form.MAIN);// It's possible to add Properties in widget, so
                                                                     // find the Main form default
                }
                if (propertiesList.contains(checkProperties) && resolveForm != null) {
                    ObjectNode jsonNodes = processTPropertiesWidget(resolveForm);
                    jsonNodes = processTWidget(jsonWidget.getWidget(), jsonNodes);// add the current
                                                                                  // ComponentProperties/Form widget
                                                                                  // type
                    order.put(jsonWidget.getOrder(), jsonWidget.getName());
                    if (jsonNodes.size() != 0) {
                        schema.set(jsonWidget.getName(), jsonNodes);
                    }
                }
            }
        }

        ArrayNode orderSchema = schema.putArray(UiSchemaConstants.TAG_ORDER);
        // Consider merge Main and Advanced in together, advanced * 100 as default, make sure widget in Advanced will
        // after widget in Main
        for (Integer i : order.keySet()) {
            orderSchema.add(order.get(i));
        }

        // For the property which not in the form(hidden property)
        for (Property property : propertyList) {
            String propName = property.getName();
            if (!order.values().contains(propName)) {
                orderSchema.add(propName);
                schema.set(propName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
            }
        }
        // For the properties which not in the form(hidden properties)
        for (Properties properties : propertiesList) {
            String propName = properties.getName();
            if (!order.values().contains(propName)) {
                orderSchema.add(propName);
                schema.set(propName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
            }
        }

        return schema;
    }

    private ObjectNode processTWidget(Widget widget, ObjectNode schema) {
        if (widget.isHidden()) {
            schema = setHiddenWidget(schema);
        } else {
            String widgetType = UiSchemaConstants.getWidgetMapping().get(widget.getWidgetType());
            if (widgetType != null) {
                schema.put(UiSchemaConstants.TAG_WIDGET, widgetType);
            } else {
                widgetType = UiSchemaConstants.getCustomWidgetMapping().get(widget.getWidgetType());
                if (widgetType != null) {
                    schema.put(UiSchemaConstants.TAG_CUSTOM_WIDGET, widgetType);
                } // else null, null means default, and do not add type tag in schema
            }
            schema = addTriggerTWidget(widget, schema);
        }
        return schema;
    }

    private ObjectNode addTriggerTWidget(Widget widget, ObjectNode schema) {
        ArrayNode jsonNodes = schema.putArray(UiSchemaConstants.TAG_TRIGGER);
        if (widget.isCallAfter()) {
            jsonNodes.add(UiSchemaConstants.TRIGGER_AFTER);
        }
        if (widget.isCallBeforeActivate()) {
            jsonNodes.add(UiSchemaConstants.TRIGGER_BEFORE_ACTIVATE);
        }
        if (widget.isCallBeforePresent()) {
            jsonNodes.add(UiSchemaConstants.TRIGGER_BEFORE_PRESENT);
        }
        if (widget.isCallValidate()) {
            jsonNodes.add(UiSchemaConstants.TRIGGER_VALIDATE);
        }
        if (jsonNodes.size() == 0) {
            schema.remove(UiSchemaConstants.TAG_TRIGGER);
        }
        return schema;
    }

    private List<JsonWidget> listTypedWidget(Form form) {
        List<JsonWidget> results = new ArrayList<>();
        if (form != null) {
            for (Widget widget : form.getWidgets()) {
                NamedThing content = widget.getContent();
                if ((content instanceof Property || content instanceof Properties || content instanceof Form
                        || content instanceof PresentationItem)) {
                    results.add(new JsonWidget(widget, form));
                }
            }
        }
        return results;
    }

    private ObjectNode setHiddenWidget(ObjectNode schema) {
        schema.put(UiSchemaConstants.TAG_WIDGET, UiSchemaConstants.TYPE_HIDDEN);
        return schema;
    }

}
