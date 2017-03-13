package org.talend.daikon.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.converter.ConvertBigDecimal;
import org.talend.daikon.avro.converter.ConvertByte;
import org.talend.daikon.avro.converter.ConvertCharacter;
import org.talend.daikon.avro.converter.ConvertDate;
import org.talend.daikon.avro.converter.ConvertShort;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

/**
 * Helper methods for accessing Avro {@link Schema} and Avro-compatible objects.
 */
public class AvroUtils {

    private static final ConvertByte BYTE_TYPE = new ConvertByte();

    private static final ConvertCharacter CHARACTER_TYPE = new ConvertCharacter();

    private static final ConvertDate DATE_TYPE = new ConvertDate();

    private static final ConvertShort SHORT_TYPE = new ConvertShort();

    private static final ConvertBigDecimal DECIMAL_TYPE = new ConvertBigDecimal();

    public static String REJECT_FIELD_INPUT = "input";

    public static String REJECT_FIELD_ERROR_MESSAGE = "errorMessage";

    public static Schema _boolean() {
        return Schema.create(Schema.Type.BOOLEAN);
    }

    public static Schema _byte() {
        return BYTE_TYPE.getSchema();
    }

    public static Schema _bytes() {
        return Schema.create(Schema.Type.BYTES);
    }

    public static Schema _character() {
        return CHARACTER_TYPE.getSchema();
    }

    // FIXME - remove this one, this is not the date representation we ultimately want to use
    public static Schema _date() {
        return DATE_TYPE.getSchema();
    }

    public static Schema _logicalDate() {
        return LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT));
    }

    public static Schema _logicalTime() {
        return LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT));
    }

    public static Schema _logicalTimestamp() {
        return LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
    }

    public static Schema _double() {
        return Schema.create(Schema.Type.DOUBLE);
    }

    public static Schema _float() {
        return Schema.create(Schema.Type.FLOAT);
    }

    public static Schema _int() {
        return Schema.create(Schema.Type.INT);
    }

    public static Schema _long() {
        return Schema.create(Schema.Type.LONG);
    }

    public static Schema _short() {
        return SHORT_TYPE.getSchema();
    }

    public static Schema _string() {
        return Schema.create(Schema.Type.STRING);
    }

    public static Schema _decimal() {
        return DECIMAL_TYPE.getSchema();
    }

    public static boolean isSameType(Schema actual, Schema expect) {
        String expectJavaClass = expect.getProp(SchemaConstants.JAVA_CLASS_FLAG);
        String actualJavaClass = actual.getProp(SchemaConstants.JAVA_CLASS_FLAG);
        if (actual.getType() == expect.getType()) {
            if (actualJavaClass == null) {
                if (expectJavaClass == null) {
                    return true;
                }
            } else {
                if (actualJavaClass.equals(expectJavaClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return Given any Schema, return whether the null value is possible.
     */
    public static boolean isNullable(Schema schema) {
        if (schema.getType() == Schema.Type.NULL) {
            return true;
        }
        if (schema.getType() == Schema.Type.UNION) {
            for (Schema unionType : schema.getTypes()) {
                if (unionType.getType() == Schema.Type.NULL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return Given any Schema, return the schema as a {@link Schema.Type#UNION} containing {@link Schema.Type#NULL} as
     * an option.
     */
    public static Schema wrapAsNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            // TODO(rskraba): The nullable schema can be a singleton?
            List<Schema> unionTypes = schema.getTypes();
            if (unionTypes.contains(Schema.create(Schema.Type.NULL))) {
                return schema;
            }

            ArrayList<Schema> typesWithNullable = new ArrayList<>(unionTypes);
            typesWithNullable.add(Schema.create(Schema.Type.NULL));
            return Schema.createUnion(typesWithNullable);
        }
        return SchemaBuilder.nullable().type(schema);
    }

    /**
     * @return Given any Schema, removes {@link Schema.Type#NULL} as an option.
     */
    public static Schema unwrapIfNullable(Schema schema) {
        // If this is a simple type wrapped in a nullable, then just use the
        // non-nullable
        if (schema.getType() == Schema.Type.UNION) {
            List<Schema> unionTypes = schema.getTypes();
            // The majority of cases can be unwrapped by removing the union with null.
            if (unionTypes.size() == 2) {
                if (unionTypes.get(0).getType().equals(Schema.Type.NULL)) {
                    return unionTypes.get(1);
                } else if (unionTypes.get(1).getType().equals(Schema.Type.NULL)) {
                    return unionTypes.get(0);
                }
            } else if (unionTypes.contains(Schema.create(Type.NULL))) {
                ArrayList<Schema> typesWithoutNullable = new ArrayList<>(unionTypes);
                typesWithoutNullable.remove(Schema.create(Schema.Type.NULL));
                return Schema.createUnion(typesWithoutNullable);
            }
        }
        return schema;
    }

    public static Map<String, Schema.Field> makeFieldMap(Schema schema) {
        Map<String, Schema.Field> map = new HashMap<>();
        for (Schema.Field field : schema.getFields()) {
            map.put(field.name(), field);
        }
        return map;
    }

    /**
     * Schema don't support overwrite property's value, so have to clone it then put the new value
     *
     * @return schema with the new value for the property: key
     */
    public static Schema setProperty(Schema schema, String key, String value) {
        Schema newSchema = schema;
        if (schema.getProp(key) != null) {
            if (schema.getType() == Type.RECORD) {
                newSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
                List<Schema.Field> copyFieldList = new ArrayList<>();
                for (Schema.Field se : schema.getFields()) {
                    copyFieldList.add(new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal()));
                }
                newSchema.setFields(copyFieldList);
                Map<String, Object> props = schema.getObjectProps();
                for (String propKey : props.keySet()) {
                    if (propKey.equals(key)) {
                        newSchema.addProp(key, value);
                    } else {
                        newSchema.addProp(propKey, props.get(propKey));
                    }
                }
            } else {// FIXME for other type
                throw new RuntimeException("Not support this type " + newSchema.getType() + " now, need to implement");
            }
        } else {
            schema.addProp(key, value);
        }
        return newSchema;
    }

    /**
     * Schema don't support overwrite field, so have to clone it then put the new value
     *
     * @return schema with the new fields
     */
    public static Schema appendFields(Schema schema, Schema.Field... fields) {
        Schema newSchema = schema;
        if (schema.getType() == Type.RECORD) {
            newSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
            List<Schema.Field> copyFieldList = new ArrayList<>();
            for (Schema.Field se : schema.getFields()) {
                copyFieldList.add(new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal()));
            }
            for (Schema.Field field : fields) {
                copyFieldList.add(field);
            }
            newSchema.setFields(copyFieldList);
            Map<String, Object> props = schema.getObjectProps();
            for (String propKey : props.keySet()) {
                newSchema.addProp(propKey, props.get(propKey));
            }
        } else {
            TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_EXCEPTION)
                    .setAndThrow("Not support this type " + schema.getType() + ", only support record type");
        }
        return newSchema;
    }

    /**
     * Schema is immutable, so have to clone it and do some changes
     * 
     * @param schema
     * @param fieldNames
     * @return schema without the fields which in fieldNames
     */
    public static Schema removeFields(Schema schema, Set<String> fieldNames) {
        Schema newSchema = schema;
        if (schema.getType() == Type.RECORD) {
            newSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
            List<Schema.Field> copyFieldList = new ArrayList<>();
            for (Schema.Field se : schema.getFields()) {
                if (fieldNames.contains(se.name())) {
                    continue;
                }
                copyFieldList.add(new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal()));
            }
            newSchema.setFields(copyFieldList);
            Map<String, Object> props = schema.getObjectProps();
            for (String propKey : props.keySet()) {
                newSchema.addProp(propKey, props.get(propKey));
            }
        } else {
            TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_EXCEPTION)
                    .setAndThrow("Not support this type " + schema.getType() + ", only support record type");
        }
        return newSchema;
    }

    /**
     * @return the value of the property include-all-fields, false if there is no this property
     */
    public static boolean isIncludeAllFields(Schema schema) {
        String prop = schema.getProp(SchemaConstants.INCLUDE_ALL_FIELDS);
        return !(prop == null || !Boolean.valueOf(prop));
    }

    /**
     * @return set boolean value for the property include-all-fields
     */
    public static Schema setIncludeAllFields(Schema schema, boolean value) {
        return setProperty(schema, SchemaConstants.INCLUDE_ALL_FIELDS, String.valueOf(value));
    }

    /**
     * check if schema is empty before using it. If empty, return true, if not, return false.
     * 
     * @param schema
     * @return
     */
    public static boolean isSchemaEmpty(Schema schema) {
        if (schema == null) {
            return true;
        }

        if (isIncludeAllFields(schema)) {
            return false;
        }

        List<Schema.Field> fields = schema.getFields();

        return fields == null || fields.isEmpty();
    }

    /**
     * Creates empty record schema, i.e. record schema, which has no fields. "EmptyRecord" name is used for this schema
     * 
     * @return empty record schema
     */
    public static Schema createEmptySchema() {
        Schema emptySchema = SchemaBuilder.builder().record("EmptySchema").fields().endRecord(); //$NON-NLS-1$
        return emptySchema;
    }

    /**
     * @return The names of the different fields for a given stage of a Schema
     */
    public static List<String> getFieldNames(Schema schema) {
        List<String> fieldNames = new ArrayList<>();
        for (Schema.Field f : schema.getFields()) {
            fieldNames.add(f.name());
        }
        return fieldNames;
    }

    /**
     * Generate the reject schema associated to a schema. A rejected Schema will follow the pattern:
     *
     * {"input": {originalSchema}, "errorMessage": "error message as a String"}
     *
     * @param originalSchema the original Schema
     * @param rejectSchemaName the name of the rejected schema
     * @return a rejected schema
     */
    public static Schema createRejectSchema(Schema originalSchema, String rejectSchemaName) {
        Schema newSchema = Schema.createRecord(rejectSchemaName, originalSchema.getDoc(), originalSchema.getNamespace(),
                originalSchema.isError());
        Schema.Field inputField = new Schema.Field(REJECT_FIELD_INPUT, originalSchema, null, (Object) null);
        Schema.Field errorMessageField = new Schema.Field(REJECT_FIELD_ERROR_MESSAGE, Schema.create(Schema.Type.STRING), null,
                (Object) null);
        newSchema.setFields(Arrays.asList(inputField, errorMessageField));

        for (Map.Entry<String, Object> entry : originalSchema.getObjectProps().entrySet()) {
            newSchema.addProp(entry.getKey(), entry.getValue());
        }

        return newSchema;
    }

    /**
     * Check if a {@link Schema.Type) is a String
     *
     * @param type The type of a field
     * @return true if the type is String
     */
    public static boolean isString(Schema.Type type) {
        return Schema.Type.STRING.equals(type);
    }

    /**
     * Check if a {@link Schema.Type) is a Numerical. A numerical type is either of {@link Schema.Type.INT),
     * {@link Schema.Type.LONG), {@link Schema.Type.DOUBLE) or {@link Schema.Type.FLOAT).
     *
     * @param type The type of a field
     * @return true if the type is String
     */
    public static boolean isNumerical(Schema.Type type) {
        return Schema.Type.INT.equals(type) || Schema.Type.LONG.equals(type) //
                || Schema.Type.DOUBLE.equals(type) || Schema.Type.FLOAT.equals(type);
    }
}
