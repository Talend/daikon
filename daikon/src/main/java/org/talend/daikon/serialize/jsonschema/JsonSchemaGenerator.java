package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getListInnerClassName;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getListType;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.isListClass;

import java.util.Date;
import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesList;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumListProperty;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Provide methods to create JSON Schema documents from a {@link Properties}.
 * <p>
 * This JSON Schema can be used to validate a Properties in its "reduced JSON" format as provided by
 * {@link JsonDataGenerator}.
 * <p>
 * <ul>
 * <li>https://spacetelescope.github.io/understanding-json-schema/</li>
 * </ul>
 */
public class JsonSchemaGenerator {

    /**
     * @param properties the properties to create a JSON Schema representation for.
     * @param formName the formName to use to get the title for the form.
     * @return the JSON Schema representation.
     */
    protected ObjectNode generateJsonSchema(Properties properties, String formName) {
        return processTProperties(properties, formName, true);
    }

    /**
     * create a json ObjectNode for a given Properties and recurse into nested Properties. <br>
     * either the Properties is not visible -> it's title shall be set to "".<br>
     * either the Properties is added to form the with a special widget -> it's title shall be the Properties
     * DisplayName, except it the widget is hidden<br>
     * either the Properties is added to the form using one of it's forms -> it's title shall be the DisplayName of the
     * form except if the form is hidden.<br>
     * 
     */
    private ObjectNode processTProperties(Properties cProperties, String formName, boolean visible) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        Form form = cProperties.getPreferredForm(formName);

        setSchemaFieldTitle(cProperties, formName, visible, schema, form);
        computeSchemaType(cProperties, formName, visible, schema);

        traverseAllProperty(cProperties, schema, form);
        traverseNestedProperties(cProperties, schema, form);
        return schema;
    }

    private void computeSchemaType(Properties cProperties, String formName, boolean visible, ObjectNode schema) {

        // Handle PropertiesList type
        if (cProperties instanceof PropertiesList<?>) {
            // Override the title to always use the property display name.  This is necessary because PropertyList UI
            // representations use the title to describe the type of element it contains.
            schema.put(JsonSchemaConstants.TAG_TITLE, cProperties.getDisplayName());
            schema.put(JsonSchemaConstants.TAG_MIN_ITEMS, Integer.parseInt(((PropertiesList<?>) cProperties).getMinItems()));
            schema.put(JsonSchemaConstants.TAG_MAX_ITEMS, Integer.parseInt(((PropertiesList<?>) cProperties).getMaxItems()));
            schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_ARRAY);
            // Generate items node
            ObjectNode itemsObjectNode = processTProperties(((PropertiesList<?>) cProperties).getDefaultProperties(), formName,
                    visible);
            // Add default node to items node
            itemsObjectNode.set(JsonSchemaConstants.TAG_DEFAULT,
                    processDefaultValues(((PropertiesList<?>) cProperties).getDefaultProperties()));
            // Attach items node
            schema.set(JsonSchemaConstants.TAG_ITEMS, itemsObjectNode);
        } else {
            schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_OBJECT);
            schema.putObject(JsonSchemaConstants.TAG_PROPERTIES);
        }
    }

    private void traverseNestedProperties(Properties cProperties, ObjectNode schema, Form form) {
        List<Properties> propertiesList = getSubProperties(cProperties);

        for (Properties properties : propertiesList) {
            String name = properties.getName();
            // if this is a reference then just store it as a string and only store the definition
            if (properties instanceof ReferenceProperties<?>) {
                ReferenceProperties<?> referenceProperties = (ReferenceProperties<?>) properties;
                ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).set(name,
                        processReferenceProperties(referenceProperties));
            } else {
                // compute if the properties is visible, meaning it was added to the current form
                Widget widget = form != null ? form.getWidget(properties.getName()) : null;
                boolean isVisible = widget != null && widget.isVisible();
                // compute the formName is one of the properties form was added as a subform
                String propertiesFormName = null;
                if (isVisible) {
                    if (widget.getContent() instanceof Form) {
                        propertiesFormName = widget.getContent().getName();
                    } else if (properties instanceof PropertiesList) {
                        // PropertiesLists that do not have a Form contents can still be associated with a default form.
                        propertiesFormName = properties.getForm(null).getName();
                    }
                }
                ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).set(name,
                        processTProperties(properties, propertiesFormName, isVisible));
            }
        }
    }

    private void traverseAllProperty(Properties cProperties, ObjectNode schema, Form form) {
        List<Property> propertyList = getSubProperty(cProperties);
        for (Property property : propertyList) {
            String name = property.getName();
            // record required field only if they are visible
            Widget widget = form != null ? form.getWidget(property.getName()) : null;
            boolean isVisible = widget != null && widget.isVisible();
            if (isVisible && property.isRequired()) {
                addToRequired(schema, name);
            } // else not visible or required so not added to the required list.

            ObjectNode propertySchema = processTProperty(property);
            ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).set(name, propertySchema);

            WidgetSpecificJsonSchemaUtils.listViewSpecific(form, property, propertySchema);

        }
    }

    private void setSchemaFieldTitle(Properties cProperties, String formName, boolean visible, ObjectNode schema, Form form) {
        if (visible) {
            if (formName != null) {
                if (form != null) {// form found
                    schema.put(JsonSchemaConstants.TAG_TITLE, form.getDisplayName());
                } else {// wrong form name so hide it.
                    // Hide the current element on the UI schema
                    schema.put(JsonSchemaConstants.TAG_TITLE, "");
                }
            } else {
                // no associated form but visible so use the Properties display Name
                schema.put(JsonSchemaConstants.TAG_TITLE, cProperties.getDisplayName());
            }
        } else {
            // Hide the current element on the UI schema
            schema.put(JsonSchemaConstants.TAG_TITLE, "");
        }
    }

    /**
     * create a simple String definition with the {@link ReferenceProperties#referenceDefinitionName} value
     */
    private JsonNode processReferenceProperties(ReferenceProperties<?> referenceProperties) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        schema.put(JsonSchemaConstants.TAG_TITLE, referenceProperties.getDisplayName());
        schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_STRING);
        return schema;
    }

    private ObjectNode processTProperty(Property property) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        schema.put(JsonSchemaConstants.TAG_TITLE, property.getDisplayName());
        if (!property.getPossibleValues().isEmpty()) {
            if (property instanceof EnumProperty) {
                resolveEnum(schema, property);
            } else if (property instanceof EnumListProperty) {
                resolveList(schema, property);
            } else {
                resolveDefault(schema, property);
            }
        } else if (isListClass(property.getType())) {
            resolveList(schema, property);
        } else {
            schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.getTypeMapping().get(property.getType()));
            if (Date.class.getName().equals(property.getType())) {
                schema.put(JsonSchemaConstants.TAG_FORMAT, "date-time");// Do not support other format for date till
                // Property
                // support it
            }
        }
        return schema;
    }

    private void resolveDefault(ObjectNode schema, Property property) {
        final ArrayNode enumList;
        final ArrayNode enumNames;
        if (isListClass(property.getType())) {
            schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.getTypeMapping().get(getListType()));
            ObjectNode items = schema.putObject(JsonSchemaConstants.TAG_ITEMS);
            items.put(JsonSchemaConstants.TAG_TYPE,
                    JsonSchemaConstants.getTypeMapping().get(getListInnerClassName(property.getType())));
            enumList = items.putArray(JsonSchemaConstants.TAG_ENUM);
            enumNames = items.putArray(JsonSchemaConstants.TAG_ENUM_NAMES);
        } else {
            schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.getTypeMapping().get(property.getType()));
            enumList = schema.putArray(JsonSchemaConstants.TAG_ENUM);
            enumNames = schema.putArray(JsonSchemaConstants.TAG_ENUM_NAMES);
        }
        addEnumsToProperty(enumList, enumNames, property);

        // Set default value if one is stored at the property level
        if (property.getDefaultValue() != null) {
            schema.put(JsonSchemaConstants.TAG_DEFAULT, property.getStringDefaultValue());
        }
    }

    private void addEnumsToProperty(ArrayNode enumList, ArrayNode enumNames, Property property) {
        List possibleValues = property.getPossibleValues();
        for (Object possibleValue : possibleValues) {
            String value = possibleValue.toString();
            if (NamedThing.class.isAssignableFrom(possibleValue.getClass())) {
                value = ((NamedThing) possibleValue).getName();
            }
            enumList.add(value);
            enumNames.add(property.getPossibleValuesDisplayName(possibleValue));
        }
    }

    private void resolveEnum(ObjectNode schema, Property property) {
        schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_STRING);
        ArrayNode enumNames = schema.putArray(JsonSchemaConstants.TAG_ENUM_NAMES);
        ArrayNode enumValues = schema.putArray(JsonSchemaConstants.TAG_ENUM);
        List possibleValues = property.getPossibleValues();
        for (Object possibleValue : possibleValues) {
            enumValues.add(possibleValue.toString());
            enumNames.add(property.getPossibleValuesDisplayName(possibleValue));
        }
    }

    private void resolveList(ObjectNode schema, Property property) {
        String className = property.getType();
        schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_ARRAY);
        ObjectNode items = JsonNodeFactory.instance.objectNode();
        schema.set(JsonSchemaConstants.TAG_ITEMS, items);
        if (property instanceof EnumListProperty) {
            resolveEnum(items, property);
        } else {
            items.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.getTypeMapping().get(getListInnerClassName(className)));
        }
    }

    private void addToRequired(ObjectNode schema, String name) {
        ArrayNode requiredNode;
        if (!schema.has(JsonSchemaConstants.TAG_REQUIRED)) {
            requiredNode = schema.putArray(JsonSchemaConstants.TAG_REQUIRED);
        } else {
            requiredNode = (ArrayNode) schema.get(JsonSchemaConstants.TAG_REQUIRED);
        }
        requiredNode.add(name);
    }

    private ObjectNode processDefaultValues(Properties defaultProperties) {
        ObjectNode defaultValuesNode = JsonNodeFactory.instance.objectNode();
        List<NamedThing> nestedProperties = defaultProperties.getProperties();
        for (NamedThing namedThing : nestedProperties) {
            NamedThing currentNamedThing = defaultProperties.getProperty(namedThing.getName());
            if ((currentNamedThing != null) && (currentNamedThing instanceof Property<?>)) {
                Property<?> currentProperty = (Property<?>) currentNamedThing;
                defaultValuesNode.put(currentNamedThing.getName(), currentProperty.getStringValue());
            }
        }
        return defaultValuesNode;
    }
}
