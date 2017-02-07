package org.talend.daikon.serialize.jsonschema;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
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
    public static <P extends Properties> P fromJson(String jsonStr, P initialInstance) {
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
     * create an Properties instance using the defRegistryService to find the proper definition that acts as a factory.
     * the json string must contain the @definitionName metadata node.
     * if the metadata is not found a {@link TalendRuntimeException} is thrown with the code
     * {@link CommonErrorCodes#UNABLE_TO_PARSE_JSON}
     * 
     */
    public static Properties fromJson(String jsonStr, DefinitionRegistryService defRegistryService) {
        JsonNode jsonNode;
        try {
            // first try to create Properties instance
            jsonNode = mapper.readTree(jsonStr);
            return fromJsonNode(defRegistryService, jsonNode);
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            throw TalendRuntimeException.createUnexpectedException(e);
        }
    }

    /**
     * returns a Properties instance if any found according to the json description and the registry.
     * The returned properties has been initialized (call to {@link Properties#init()}.
     */
    static Properties fromJsonNode(DefinitionRegistryService defRegistryService, JsonNode jsonNode) throws NoSuchMethodException,
            IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        JsonNode defNameNode = jsonNode.get(JsonSchemaConstants.DEFINITION_NAME_JSON_METADATA);
        if (defNameNode == null) {
            throw TalendRuntimeException.build(CommonErrorCodes.UNABLE_TO_PARSE_JSON).create();
        } // else we got one definition so try to use it
        Definition<?> definition = defRegistryService.getDefinitionsMapByType(Definition.class).get(defNameNode.asText());
        if (definition == null) {// we are trying to use a definition that is not registered
            throw TalendRuntimeException.build(CommonErrorCodes.UNREGISTERED_DEFINITION).set(defNameNode.asText());
        } // else we got a definition so let's use it to create the instance.
        return fromJson(jsonNode, (defRegistryService.createProperties(definition, "")).init());
    }

    /**
     * fills the initialInstance with the properties from the Json-data intput stream
     */
    public static Properties fromJson(InputStream jsonIS, DefinitionRegistryService defRegistryService) {
        try {
            JsonNode jsonNode = mapper.readTree(jsonIS);
            return fromJsonNode(defRegistryService, jsonNode);
        } catch (Exception e) {
            throw TalendRuntimeException.createUnexpectedException(e);
        }
    }

    /**
     * fills the initialInstance with the properties from the Json-data intput stream
     */
    public static <P extends Properties> P fromJson(InputStream inputStream, P initialInstance) {
        try {
            JsonNode jsonNode = mapper.readTree(inputStream);
            return fromJson(jsonNode, initialInstance);
        } catch (Exception e) {
            throw TalendRuntimeException.createUnexpectedException(e);
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

    /**
     * serialize the properties to a ui-specs including a json schema, json data and json ui.
     * The json data has a extra string field named {@link JsonSchemaConstants#DEFINITION_NAME_JSON_METADATA} containing the
     * definitionName
     */
    public static String toJson(Properties cp, String formName, String definitionName) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.set(TAG_JSON_SCHEMA, jsonSchemaGenerator.genSchema(cp, formName));
        objectNode.set(TAG_JSON_DATA, jsonDataGenerator.genData(cp, definitionName));
        if (!cp.getForms().isEmpty()) {
            objectNode.set(TAG_JSON_UI, uiSchemaGenerator.genWidget(cp, formName));
        }
        return objectNode.toString();
    }

}
