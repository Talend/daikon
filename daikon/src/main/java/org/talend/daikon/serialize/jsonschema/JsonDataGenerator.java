package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.dateFormatter;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.findClass;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getListInnerClassName;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.isListClass;

import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonDataGenerator {

    protected <T extends Properties> ObjectNode genData(T properties) {
        return processTPropertiesData(properties);
    }

    private ObjectNode processTPropertiesData(Properties cProperties) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();

        List<Property> propertyList = getSubProperty(cProperties);
        for (Property property : propertyList) {
            processTPropertyValue(property, rootNode);
        }
        List<Properties> propertiesList = getSubProperties(cProperties);
        for (Properties properties : propertiesList) {
            if (!ReferenceProperties.class.isAssignableFrom(properties.getClass())) {
                String name = properties.getName();
                rootNode.set(name, processTPropertiesData(properties));
            }
        }
        return rootNode;
    }

    private ObjectNode processTPropertyValue(Property property, ObjectNode node) {
        String javaType = property.getType();
        String pName = property.getName();
        Object pValue = property.getValue();
        if (pValue == null) {
            // unset if the value is null
            // node.set(pName, node.nullNode());
        } else if (isListClass(javaType)) {
            Class type = findClass(getListInnerClassName(javaType));
            ArrayNode arrayNode = node.putArray(pName);
            for (Object value : ((List) pValue)) {
                fillValue(arrayNode, type, value);
            }
        } else {
            fillValue(node, findClass(javaType), pName, pValue);
        }
        return node;
    }

    private void fillValue(ArrayNode node, Class type, Object value) {
        if (String.class.equals(type)) {
            node.add((String) value);
        } else if (Integer.class.equals(type)) {
            node.add((Integer) value);
        } else if (type.isEnum()) {
            node.add(value.toString());
        } else if (Boolean.class.equals(type)) {
            node.add((Boolean) value);
        } else if (Schema.class.equals(type)) {
            node.add(value.toString());
        } else if (Double.class.equals(type)) {
            node.add((Double) value);
        } else if (Float.class.equals(type)) {
            node.add((Float) value);
        } else if (Date.class.equals(type)) {
            node.add(dateFormatter.format((Date) value));
        } else {
            throw new RuntimeException("Do not support type " + type + " yet.");
        }
    }

    private void fillValue(ObjectNode node, Class type, String key, Object value) {
        if (String.class.equals(type)) {
            node.put(key, (String) value);
        } else if (Integer.class.equals(type)) {
            node.put(key, (Integer) value);
        } else if (type.isEnum()) {
            node.put(key, value.toString());
        } else if (Boolean.class.equals(type)) {
            node.put(key, (Boolean) value);
        } else if (Schema.class.equals(type)) {
            node.put(key, value.toString());
        } else if (Double.class.equals(type)) {
            node.put(key, (Double) value);
        } else if (Float.class.equals(type)) {
            node.put(key, (Float) value);
        } else if (Date.class.equals(type)) {
            node.put(key, dateFormatter.format((Date) value));
        } else {
            throw new RuntimeException("Do not support type " + type + " yet.");
        }
    }

}
