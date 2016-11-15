package org.talend.daikon.serialize.jsonschema;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * Built-in tag. Conbine with array type, a json object which represent the element of array
     */
    public static final String TAG_ITEMS = "items";

    // TODO(bchen) How it work when Property do not support default value, but use current value as default value, think
    // about cell of table
    /**
     * Built-in tag. Default value
     */
    public static final String TAG_DEFAULT = "default";

    // Mapping between Property type and json-schema type
    private static Map<String, String> TYPE_MAPPING = new HashMap<>();

    static {
        // TYPE_MAPPING.put(Character.class.getName(), JsonSchemaConstants.TYPE_STRING);
        TYPE_MAPPING.put(String.class.getName(), JsonSchemaConstants.TYPE_STRING);
        // TYPE_MAPPING.put(CharSequence.class.getName(), JsonSchemaConstants.TYPE_STRING);
        TYPE_MAPPING.put(Schema.class.getName(), JsonSchemaConstants.TYPE_STRING);
        TYPE_MAPPING.put(Date.class.getName(), JsonSchemaConstants.TYPE_STRING);

        TYPE_MAPPING.put(Boolean.class.getName(), JsonSchemaConstants.TYPE_BOOLEAN);

        TYPE_MAPPING.put(Float.class.getName(), JsonSchemaConstants.TYPE_NUMBER);
        TYPE_MAPPING.put(Double.class.getName(), JsonSchemaConstants.TYPE_NUMBER);
        // TYPE_MAPPING.put(BigDecimal.class.getName(), JsonSchemaConstants.TYPE_NUMBER);

        // TYPE_MAPPING.put(Byte.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        // TYPE_MAPPING.put(Short.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        TYPE_MAPPING.put(Integer.class.getName(), JsonSchemaConstants.TYPE_INTEGER);
        TYPE_MAPPING.put(Long.class.getName(), JsonSchemaConstants.TYPE_STRING);
        // TYPE_MAPPING.put(BigInteger.class.getName(), JsonSchemaConstants.TYPE_INTEGER);

        TYPE_MAPPING = Collections.unmodifiableMap(TYPE_MAPPING);
    }

    public static Map<String, String> getTypeMapping() {
        return TYPE_MAPPING;
    }

    public static final String DEFINITION_NAME_JSON_METADATA = "@definitionName";
}
