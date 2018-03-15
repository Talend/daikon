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
import org.talend.daikon.properties.PropertiesList;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonDataGenerator {

    protected ObjectNode genData(Properties properties, String formName, String definitionName) {
        Form mainForm = properties.getPreferredForm(formName);
        ObjectNode propertiesJsonDataObject = processTPropertiesData(mainForm, properties);
        propertiesJsonDataObject.put(JsonSchemaConstants.DEFINITION_NAME_JSON_METADATA, definitionName);
        return propertiesJsonDataObject;
    }

    ObjectNode processTPropertiesData(Form form, Properties cProperties) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();

        List<Property> propertyList = getSubProperty(cProperties);
        for (Property property : propertyList) {
            processTPropertyValue(form, cProperties.getClass().getClassLoader(), property, rootNode);
        }
        List<Properties> propertiesList = getSubProperties(cProperties);
        for (Properties properties : propertiesList) {
            String name = properties.getName();
            // for ReferenceProperties we just create a String node in the root just like any Property
            if (properties instanceof ReferenceProperties<?>) {
                ReferenceProperties<?> referenceProperties = (ReferenceProperties<?>) properties;
                rootNode.put(properties.getName(), referenceProperties.referenceDefinitionName.getValue());
            } else if (properties instanceof PropertiesList) {
                ArrayNode arrayNode = rootNode.putArray(properties.getName());
                for (Properties props : ((PropertiesList<?>) properties).getPropertiesList()) {
                    fillValue(form, arrayNode, Properties.class, props);
                }
            } else {
                rootNode.set(name, processTPropertiesData(form, properties));
            }
        }
        return rootNode;
    }

    private ObjectNode processTPropertyValue(Form form, ClassLoader classLoader, Property property, ObjectNode node) {
        // If the widget has a placeholder and the property has a null or empty value, then do not include it in the
        // generated data. The placeholder will be used, but is stored in the UISchema.
        Widget widget = form != null ? form.getWidget(property.getName()) : null;
        if (widget != null) {
            String placeholder = (String) widget.getConfigurationValue(Widget.PLACEHOLDER_WIDGET_CONF);
            if (placeholder != null && (property.getValue() == null || property.getValue().equals("")))
                return node;
        }
        String javaType = property.getType();
        String pName = property.getName();
        Object pValue = property.getValue();
        if (pValue == null) {
            // unset if the value is null
            // node.set(pName, node.nullNode());
        } else if (isListClass(javaType)) {
            Class type = findClass(classLoader, getListInnerClassName(javaType));
            ArrayNode arrayNode = node.putArray(pName);
            for (Object value : ((List) pValue)) {
                fillValue(form, arrayNode, type, value);
            }
        } else {
            fillValue(node, findClass(classLoader, javaType), pName, pValue);
        }
        return node;
    }

    private void fillValue(Form form, ArrayNode node, Class type, Object value) {
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
        } else if (Long.class.equals(type)) {
            node.add((Long) value);
        } else if (Date.class.equals(type)) {
            node.add(dateFormatter.format((Date) value));
        } else if (Properties.class.equals(type)) {
            node.add(processTPropertiesData(form, (Properties) value));
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
        } else if (Long.class.equals(type)) {
            node.put(key, (Long) value);
        } else if (Date.class.equals(type)) {
            node.put(key, dateFormatter.format((Date) value));
        } else {
            throw new RuntimeException("Do not support type " + type + " yet.");
        }
    }

}
