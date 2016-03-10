package org.talend.daikon.avro.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Helper methods for accessing Avro {@link Schema} and Avro-compatible objects.
 */
public class AvroUtils {

    /**
     * @return Given any Schema, return whether the null value is possible.
     */
    public static boolean isNullable(Schema schema) {
        if (schema.getType() == Schema.Type.NULL) {
            return true;
        }
        if (schema.getType() == Schema.Type.UNION) {
            for (Schema unionType : schema.getTypes()) {
                if (schema.getType() == Schema.Type.NULL) {
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

    public static boolean isDynamic(Schema schema) {
        return schema.getType() == Type.BYTES && schema.getLogicalType().getName().equals(SchemaConstants.LOGICAL_DYNAMIC);
    }

    public static Schema.Field setFieldDynamic(Schema.Field field) {
        LogicalType lt = new LogicalType(SchemaConstants.LOGICAL_DYNAMIC);
        Schema fs = field.schema();
        lt.addToSchema(fs);
        return field;
    }

}
