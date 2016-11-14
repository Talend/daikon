package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getListInnerClassName;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.isListClass;

import java.util.Date;
import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.property.EnumListProperty;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Generator JSONSchema from Properties
 */
public class JsonSchemaGenerator {

    protected ObjectNode genSchema(Properties properties) {
        return processTProperties(properties);
    }

    private ObjectNode processTProperties(Properties cProperties) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        schema.put(JsonSchemaConstants.TAG_TITLE, cProperties.getDisplayName());
        schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.TYPE_OBJECT);
        schema.putObject(JsonSchemaConstants.TAG_PROPERTIES);

        List<Property> propertyList = getSubProperty(cProperties);
        for (Property property : propertyList) {
            String name = property.getName();
            if (property.isRequired()) {
                addToRequired(schema, name);
            }
            ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).set(name, processTProperty(property));
        }
        List<Properties> propertiesList = getSubProperties(cProperties);
        for (Properties properties : propertiesList) {
            String name = properties.getName();
            if (ReferenceProperties.class.isAssignableFrom(properties.getClass())) {
                ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).put(name,
                        ((ReferenceProperties) properties).componentType.getValue());
            } else {
                ((ObjectNode) schema.get(JsonSchemaConstants.TAG_PROPERTIES)).set(name, processTProperties(properties));
            }
        }
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
                schema.put(JsonSchemaConstants.TAG_TYPE, JsonSchemaConstants.getTypeMapping().get(property.getType()));
                ArrayNode enumList = schema.putArray(JsonSchemaConstants.TAG_ENUM);
                List possibleValues = property.getPossibleValues();
                for (Object possibleValue : possibleValues) {
                    String value = possibleValue.toString();
                    if (NamedThing.class.isAssignableFrom(possibleValue.getClass())) {
                        value = ((NamedThing) possibleValue).getName();
                    }
                    enumList.add(value);
                }
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

}
