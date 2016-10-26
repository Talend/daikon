package org.talend.daikon.serialize.jsonschema;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Util for round trip between ComponentProperties and JSONSchema/UISchema/JSONData
 */
public class JsonSchemaUtil {

    public static final String TAG_JSON_SCHEMA = "jsonSchema";

    public static final String TAG_JSON_UI = "uiSchema";

    public static final String TAG_JSON_DATA = "properties";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator();

    private static final UiSchemaGenerator uiSchemaGenerator = new UiSchemaGenerator();

    private static final JsonDataGenerator jsonDataGenerator = new JsonDataGenerator();

    private static final JsonPropertiesResolver resolver = new JsonPropertiesResolver();

    /**
     * fills the initalInstance with the properties from the Json-data String
     */
    public static <T extends Properties> T fromJson(String jsonStr, T initialInstance) {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(jsonStr);
            return fromJson(jsonNode, initialInstance);
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            throw TalendRuntimeException.createUnexpectedException(e);
        }
    }

    /**
     * fills the initalInstance with the properties from the Json-data intput stream
     */
    public static Properties fromJson(InputStream jsonIS, Properties initialInstance) {
        try {
            JsonNode jsonNode = mapper.readTree(jsonIS);
            return fromJson(jsonNode, initialInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <P extends Properties> P fromJson(JsonNode jsonData, P initialInstance) throws NoSuchMethodException,
            IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        if (jsonData == null) {
            throw TalendRuntimeException
                    .createUnexpectedException(TAG_JSON_SCHEMA + " or " + TAG_JSON_DATA + " should not be null");
        }
        return resolver.resolveJson((ObjectNode) jsonData, initialInstance);
    }

    public static String toJson(Properties cp) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.set(TAG_JSON_SCHEMA, jsonSchemaGenerator.genSchema(cp));
        objectNode.set(TAG_JSON_DATA, jsonDataGenerator.genData(cp));
        if (!cp.getForms().isEmpty()) {
            objectNode.set(TAG_JSON_UI, uiSchemaGenerator.genWidget(cp));
        }
        return objectNode.toString();
    }
}
