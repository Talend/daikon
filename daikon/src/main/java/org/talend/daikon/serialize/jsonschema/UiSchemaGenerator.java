package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UiSchemaGenerator {

    protected <T extends Properties> ObjectNode genWidget(T properties, String formName) {
        return processTPropertiesWidget(properties, formName);
    }

    /**
     * Generate UISchema by the given ComponentProperties and relate Form/Widget Only consider the requested form and Advanced Form
     */
    private ObjectNode processTPropertiesWidget(Properties cProperties, String formName) {
        Form mainForm = cProperties.getPreferredForm(formName);
        Form advancedForm = cProperties.getPreferredForm(Form.ADVANCED);
        return processTPropertiesWidget(mainForm, advancedForm);
    }

    /**
     * ComponentProeprties could use multiple forms in one time to represent the graphic setting, Main & Advanced for
     * instance. ComponentProperties could has Properties/Property which are not in Form, treat it as hidden
     * Properties/Property
     */
    private ObjectNode processTPropertiesWidget(Form... forms) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        if (forms.length <= 0) {
            return schema;
        }

        Form firstForm = null;
        List<JsonWidget> jsonWidgets = new ArrayList<>();
        for (Form form : forms) {
            if (form != null) {
                if (firstForm == null) {
                    firstForm = form;
                }
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
                    resolveForm = checkProperties.getForm(firstForm.getName());// It's possible to add Properties in widget, so
                    // find the first form default
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

            // if this is a reference let's consider it as a String and mark it as hidden
            if (properties instanceof ReferenceProperties<?>) {
                if (!order.values().contains(propName)) {
                    orderSchema.add(propName);
                    schema.set(propName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
                }
            }
            // otherwise, let's get all the sub properties and mark them as hidden
            else {
                orderSchema.add(propName);

                final List<Property> subProperties = getSubProperty(properties);
                final ObjectNode subPropertyNode = JsonNodeFactory.instance.objectNode();
                for (Property subProperty : subProperties) {
                    final String subPropertyName = subProperty.getName();
                    subPropertyNode.set(subPropertyName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
                }
                schema.set(propName, subPropertyNode);
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
        ArrayNode jsonNodes = schema.arrayNode();
        if (widget.isCallAfter()) {
            jsonNodes.add(fromUpperCaseToCamel(PropertyTrigger.AFTER.name()));
        }
        if (widget.isCallBeforeActivate()) {
            jsonNodes.add(fromUpperCaseToCamel(PropertyTrigger.BEFORE_ACTIVE.name()));
        }
        if (widget.isCallBeforePresent()) {
            jsonNodes.add(fromUpperCaseToCamel(PropertyTrigger.BEFORE_PRESENT.name()));
        }
        if (widget.isCallValidate()) {
            jsonNodes.add(fromUpperCaseToCamel(PropertyTrigger.VALIDATE.name()));
        }
        if (jsonNodes.size() != 0) {
            schema.set(UiSchemaConstants.TAG_TRIGGER, jsonNodes);
        }
        return schema;
    }

    /** Take an UPPER_CASE String and returns its lowerCase couterpart. Used to serialize enums. **/
    private static String fromUpperCaseToCamel(String upperCase) {
        StringBuilder builder = new StringBuilder();
        String[] tokens = upperCase.toLowerCase().split("_");
        for (String token : tokens) {
            builder.append(StringUtils.capitalize(token));
        }
        return StringUtils.uncapitalize(builder.toString());
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
