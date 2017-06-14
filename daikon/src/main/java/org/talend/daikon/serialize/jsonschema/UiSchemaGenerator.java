package org.talend.daikon.serialize.jsonschema;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;

public class UiSchemaGenerator {

    protected <T extends Properties> ObjectNode genWidget(T properties, String formName) {
        return processTPropertiesWidget(properties, formName);
    }

    /**
     * Generate UISchema by the given ComponentProperties and relate Form/Widget Only consider the requested form and
     * Advanced Form
     */
    private ObjectNode processTPropertiesWidget(Properties cProperties, String formName) {
        Form mainForm = cProperties.getPreferredForm(formName);
        return processTPropertiesWidget(mainForm, new boolean[] { false });
    }

    /**
     * ComponentProeprties could use multiple forms in one time to represent the graphic setting, Main & Advanced for
     * instance. ComponentProperties could has Properties/Property which are not in Form, treat it as hidden
     * Properties/Property
     *
     * @param hasVisible Secondary return value: If anything in this form is visible, the first boolean value will be
     * set to true. Otherwise, this remains untouched. (otherwise, this array will be untouched).
     */
    private ObjectNode processTPropertiesWidget(Form form, boolean[] hasVisible) {
        ObjectNode jsonToReturn = JsonNodeFactory.instance.objectNode();
        if (form == null) {
            return jsonToReturn;
        }

        List<JsonWidget> jsonWidgets = listTypedWidget(form);

        // Merge widget in Main and Advanced form together, need the merged order.
        Map<Integer, String> order = new TreeMap<>();

        // all the forms should in same ComponentProperties, so use the first form to get the ComponentProperties is ok.
        Properties cProperties = form.getProperties();
        List<Property> propertyList = getSubProperty(cProperties);
        List<Properties> propertiesList = getSubProperties(cProperties);

        for (JsonWidget jsonWidget : jsonWidgets) {
            NamedThing content = jsonWidget.getContent();
            // If it is a top-level property or PresentationItem, then add it directly to the output.
            if (propertyList.contains(content) || content instanceof PresentationItem) {
                ObjectNode jsonNodes = processTWidget(jsonWidget.getWidget(), JsonNodeFactory.instance.objectNode(), hasVisible);
                if (jsonNodes.size() != 0) {
                    jsonToReturn.set(jsonWidget.getName(), jsonNodes);
                }
                order.put(jsonWidget.getOrder(), jsonWidget.getName());
            } else { // nested Form or Properties
                Properties checkProperties = null;
                Form resolveForm = null;
                if (content instanceof Form) {
                    // ComponentProperties could contains multiple type of Form, form in widget is the current used
                    resolveForm = (Form) content;
                    checkProperties = resolveForm.getProperties();
                } else {// Properties as been added as widget (it is likely associated with a special widget)
                    checkProperties = (Properties) content;
                    resolveForm = null;
                }

                if (propertiesList.contains(checkProperties)) {
                    ObjectNode jsonNodes = null;
                    if (resolveForm != null) {
                        // Properties associated with a form
                        boolean[] subFormVisible = { false };
                        jsonNodes = processTPropertiesWidget(resolveForm, subFormVisible);
                        if (subFormVisible[0])
                            hasVisible[0] = true;
                        jsonNodes = processTWidget(jsonWidget.getWidget(), jsonNodes, hasVisible);
                    } else {// Properties is associated with a widget
                        jsonNodes = processTWidget(jsonWidget.getWidget(), JsonNodeFactory.instance.objectNode(), hasVisible);
                    }
                    order.put(jsonWidget.getOrder(), jsonWidget.getName());
                    if (jsonNodes.size() != 0) {
                        jsonToReturn.set(jsonWidget.getName(), jsonNodes);
                    }
                }
            }
        }

        ArrayNode orderSchema = jsonToReturn.putArray(UiSchemaConstants.TAG_ORDER);
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
                jsonToReturn.set(propName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
            }
        }
        // For the properties which not in the form(hidden properties)
        for (Properties properties : propertiesList) {
            String propName = properties.getName();
            if (!order.values().contains(propName)) {
                jsonToReturn.set(propName, setHiddenWidget(JsonNodeFactory.instance.objectNode()));
                orderSchema.add(propName);
            }
        }

        // If there aren't any visible contents, then set this form as hidden as well.
        if (!hasVisible[0]) {
            setHiddenWidget(jsonToReturn);
        }

        return jsonToReturn;
    }

    /**
     * Process a top-level {@link Property} or {@link PresentationItem} in a form.
     *
     * @param widget The widget associated with the Property or Presentation item.
     * @param schema The uiSchema currently being generated for the widget.
     * @param hasVisible Secondary return value: If this widget is visible, the first boolean value will be set to true.
     * Otherwise, this remains untouched.
     * @return The generated uiSchema.
     */
    private ObjectNode processTWidget(Widget widget, ObjectNode schema, boolean[] hasVisible) {
        if (widget.isHidden()) {
            NamedThing content = widget.getContent();
            if (content != null) {
                return setHiddenWidget(schema);
            } else {// no content so ignors it
                return schema;
            }
        } else {
            String widgetType = UiSchemaConstants.getWidgetMapping().get(widget.getWidgetType());
            if (widgetType != null) {
                if (widget.isAutoFocus()) {
                    schema.put(UiSchemaConstants.TAG_AUTO_FOCUS, true);
                }
                schema.put(UiSchemaConstants.TAG_WIDGET, widgetType);
                Map<String, String> optionsMap = UiSchemaConstants.getWidgetOptionsMapping().get(widget.getWidgetType());
                if (optionsMap != null) {
                    ObjectNode options = JsonNodeFactory.instance.objectNode();

                    for (Map.Entry<String, String> entry : optionsMap.entrySet()) {
                        options.put(entry.getKey(), entry.getValue());
                    }

                    schema.set(UiSchemaConstants.TAG_OPTIONS, options);
                }
                // Any other widget type than hidden causes means that it is visible.
                if (!UiSchemaConstants.TYPE_HIDDEN.equals(widgetType)) {
                    hasVisible[0] = true;
                }
            } else {//no supported widget for this, so if Properties then hide
                NamedThing content = widget.getContent();
                if (content instanceof Properties) {
                    // hide the Properties that do not have a Widget to render it.
                    return setHiddenWidget(schema);
                } else {
                    // else for primitive it means default, and do not add type tag in schema
                    hasVisible[0] = true;
                }
            }
            return addTriggerTWidget(widget, schema);
        }
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

    /**
     * Take an UPPER_CASE String and returns its lowerCase couterpart. Used to serialize enums.
     **/
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
