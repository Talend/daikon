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
            property.setValue(getTPropertyValue(property, jsonData.get(property.getName())));
        }
        List<Properties> propertiesList = getSubProperties(cProperties);
        for (Properties properties : propertiesList) {
            if (!ReferenceProperties.class.isAssignableFrom(properties.getClass())) {
                resolveJson((ObjectNode) jsonData.get(properties.getName()), cProperties.getProperties(properties.getName()));
            }
        }
        return cProperties;
    }

    private Object getTPropertyValue(Property property, JsonNode dataNode) {
        String javaType = property.getType();
        if (dataNode == null || dataNode.isNull()) {
            return null;
        } else if (isListClass(javaType)) {
            Class type = findClass(getListInnerClassName(javaType));
            ArrayNode arrayNode = ((ArrayNode) dataNode);
            List values = new ArrayList();
            for (int i = 0; i < arrayNode.size(); i++) {
                values.add(getValue(arrayNode.get(i), type));
            }
            return values;
        } else {
            return getValue(dataNode, findClass(javaType));
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
            return new Schema.Parser().parse(dataNode.textValue());
        } else if (type.isEnum()) {
            return Enum.valueOf(type, dataNode.textValue());
        } else if (Date.class.equals(type)) {
            try {
                return dateFormatter.parse(dataNode.textValue());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Do not support type " + type + " yet.");
        }
    }
}
