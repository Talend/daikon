package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.dateFormatter;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.findClass;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getListInnerClassName;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperties;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.getSubProperty;
import static org.talend.daikon.serialize.jsonschema.JsonBaseTool.isListClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPropertiesResolver {

    public <P extends Properties> P resolveJson(ObjectNode jsonData, P cProperties) throws IOException, ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        List<Property> propertyList = getSubProperty(cProperties);
        for (Property property : propertyList) {
            Object newProperty = getTPropertyValue(cProperties.getClass().getClassLoader(), property,
                    jsonData.get(property.getName()));
            // when the Property is empty, keep the default one
            if (newProperty != null) {
                property.setValue(newProperty);
            }
        }
        List<Properties> propertiesList = getSubProperties(cProperties);
        for (Properties properties : propertiesList) {
            if (jsonData.get(properties.getName()) != null
                    && !ReferenceProperties.class.isAssignableFrom(properties.getClass())) {
                resolveJson((ObjectNode) jsonData.get(properties.getName()), cProperties.getProperties(properties.getName()));
            }
        }
        return cProperties;
    }

    private Object getTPropertyValue(ClassLoader classLoader, Property property, JsonNode dataNode) {
        String javaType = property.getType();
        if (dataNode == null || dataNode.isNull()) {
            return null;
        } else if (isListClass(javaType)) {
            Class type = findClass(classLoader, getListInnerClassName(javaType));
            ArrayNode arrayNode = ((ArrayNode) dataNode);
            List values = new ArrayList();
            for (int i = 0; i < arrayNode.size(); i++) {
                values.add(getValue(arrayNode.get(i), type));
            }
            return values;
        } else {
            return getValue(dataNode, findClass(classLoader, javaType));
        }
    }

    private Object getValue(JsonNode dataNode, Class type) {
        if (String.class.equals(type)) {
            return dataNode.textValue();
        } else if (Integer.class.equals(type)) {
            return dataNode.intValue();
        } else if (Double.class.equals(type)) {
            return dataNode.numberValue();
        } else if (Float.class.equals(type)) {
            return dataNode.numberValue().floatValue();
        } else if (Boolean.class.equals(type)) {
            return dataNode.booleanValue();
        } else if (Schema.class.equals(type)) {
            if (StringUtils.isNotBlank(dataNode.textValue())) {
                return new Schema.Parser().parse(dataNode.textValue());
            } else {
                return null;
            }
        } else if (type.isEnum()) {
            return Enum.valueOf(type, dataNode.textValue());
        } else if (Date.class.equals(type)) {
            try {
                return dateFormatter.parse(dataNode.textValue());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (Long.class.equals(type)) {
            return dataNode.longValue();
        } else {
            throw new RuntimeException("Do not support type " + type + " yet.");
        }
    }
}
