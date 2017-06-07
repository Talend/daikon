package org.talend.daikon.serialize.jsonschema;

import java.util.*;

import org.apache.avro.Schema;

/**
 * Metadata of JSONSchema, refer to http://json-schema.org/
 */
public class JsonSchemaConstants {

    /**
     * Build-in tag. JSON schema primitive types followed http://json-schema.org/latest/json-schema-core.html#anchor8
     */
    public static final String TAG_TYPE = "type";

    public static final String TYPE_STRING = "string";

    public static final String TYPE_NUMBER = "number";

    public static final String TYPE_INTEGER = "integer";

    public static final String TYPE_BOOLEAN = "boolean";

    public static final String TYPE_ARRAY = "array";

    public static final String TYPE_OBJECT = "object";

    public static final String TYPE_NULL = "null";

    /**
     * Custom tag. Indicate the unique id of properties
     */
    public static final String CUSTOM_TAG_ID = "id";

    /**
     * Built-in tag. A json object which contains all the sub property
     */
    public static final String TAG_PROPERTIES = "properties";

    /**
     * Built-in tag. A json array contains all the required sub property's name
     */
    public static final String TAG_REQUIRED = "required";

    /**
     * Built-in tag. Combine with string type, provide special widget such as date/date-time
     * https://github.com/mozilla-services/react-jsonschema-form#string-formats
     */
    public static final String TAG_FORMAT = "format";

    /**
     * Built-in tag. Combine with string type, provide a optional items
     */
    public static final String TAG_ENUM = "enum";

    /**
     * Built-in tag. i18n for enum
     */
    public static final String TAG_ENUM_NAMES = "enumNames";

    /**
     * Built-in tag. i18n for Property
     */
    public static final String TAG_TITLE = "title";

    /**
     * Built-in tag. Combine with array type, a json object which represent the element of array
     */
    public static final String TAG_ITEMS = "items";

    /**
     * Built-in tag. Combine with array type, a json object which represent the minimum number of items of the array
     */
    public static final String TAG_MIN_ITEMS = "minItems";

    /**
     * Built-in tag. Combine with array type, a json object which represent the maximum number of items of the array
     */
    public static final String TAG_MAX_ITEMS = "maxItems";

    /**
     * Built-in tag. Combine with array type, a json object which mark an array as containing all unique values
     */
    public static final String TAG_UNIQUE_ITEMS = "uniqueItems";

    // TODO(bchen) How it work when Property do not support default value, but use current value as default value, think
    // about cell of table
    /**
     * Built-in tag. Default value
     */
    public static final String TAG_DEFAULT = "default";

    public static final String DEFINITION_NAME_JSON_METADATA = "@definitionName";

    // Mapping between Property type and json-schema type
    private static final Map<String, String> TYPE_MAPPING;

    static {
        Map<String, String> typeMapping = new HashMap<>();
        // typeMapping.put(Character.class.getName(), JsonSchemaConstants.TYPE_STRING);
        typeMapping.put(String.class.getName(), JsonSchemaConstants.TYPE_STRING);
        // typeMapping.put(CharSequence.class.getName(), JsonSchemaConstants.TYPE_STRING);
        typeMapping.put(Schema.class.getName(), JsonSchemaConstants.TYPE_STRING);
        typeMapping.put(Date.class.getName(), JsonSchemaConstants.TYPE_STRING);

        typeMapping.put(Boolean.class.getName(), JsonSchemaConstants.TYPE_BOOLEAN);

        typeMapping.put(Float.class.getName(), JsonSchemaConstants.TYPE_NUMBER);
        typeMapping.put(Double.class.getName(), JsonSchemaConstants.TYPE_NUMBER);
        // typeMapping.put(BigDecimal.class.getName(), JsonSchemaConstants.TYPE_NUMBER);

        // typeMapping.put(Byte.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        // typeMapping.put(Short.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        typeMapping.put(Integer.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        typeMapping.put(Long.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        // typeMapping.put(BigInteger.class.getName(), JsonSchemaConstants.TYPE_INTEGER);

        // Array
        typeMapping.put(List.class.getName(), JsonSchemaConstants.TYPE_ARRAY);

        TYPE_MAPPING = Collections.unmodifiableMap(typeMapping);
    }

    public static Map<String, String> getTypeMapping() {
        return TYPE_MAPPING;
    }

}
