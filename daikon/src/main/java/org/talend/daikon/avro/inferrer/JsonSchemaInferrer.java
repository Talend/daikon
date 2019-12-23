// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro.inferrer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.exception.TalendRuntimeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Converts json string to avro schema.
 */
public class JsonSchemaInferrer implements SchemaInferrer<String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaInferrer.class);

    private final ObjectMapper mapper;

    /**
     * Constructor
     * 
     * @param mapper
     */
    public JsonSchemaInferrer(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @return new JsonSchemaInferrer instance
     */
    public static JsonSchemaInferrer createJsonSchemaInferrer() {
        return new JsonSchemaInferrer(new ObjectMapper());
    }

    /**
     * Create an Avro Schema based on a JSON String.
     *
     * Example:
     *
     * json string parameter: {"a": {"b": "b1"}, "d": 100}
     *
     * avro schema constructed:
     * {"type":"record","name":"outer_record","namespace":"org.talend",
     * "fields":[{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},
     * {"name":"d","type":["int","null"]}]}
     *
     * TalendRuntimeException thrown when an IOException or RuntimeException occurred.
     *
     * @param json string to convert
     * @return Avro schema constructed
     */
    @Override
    public Schema inferSchema(String json) {
        try {
            final JsonNode jsonNode = mapper.readTree(json);
            // Create a nameless temporary record to get a fingerprint from.
            Schema record = Schema.createRecord(getFields(jsonNode));
            long fingerprint = SchemaNormalization.parsingFingerprint64(record);
            return Schema.createRecord(("outer_record" + fingerprint).replace('-', '_'), null, "org.talend", false,
                    getFields(jsonNode));
        } catch (IOException | TalendRuntimeException e) {
            throw TalendRuntimeException.createUnexpectedException(e.getCause());
        }
    }

    /**
     * Get the fields schema from json node. Supported data types are: INT, BOOLEAN, LONG, DOUBLE, STRING, ARRAY, OBJECT.
     *
     * A primitive field may be either a null or string|int|boolean|long|double.
     *
     * Example:
     *
     * jsonNode parameter: {"a": {"b": "b1"}, "d": 100}
     *
     * jsonNode fields schema:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},{"name":"d","type":["int","null"]}]
     *
     * @param jsonNode
     * @return fields schema of json node
     */
    List<Schema.Field> getFields(final JsonNode jsonNode) {
        List<Schema.Field> fields = new ArrayList<>();
        final Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();
        Map.Entry<String, JsonNode> mapEntry;

        while (elements.hasNext()) {
            mapEntry = elements.next();
            final JsonNode nextNode = mapEntry.getValue();
            Schema.Field field = null;

            if (!(nextNode instanceof NullNode)) {
                switch (nextNode.getNodeType()) {
                case NUMBER:
                    field = new Schema.Field(mapEntry.getKey(), getAvroSchema(nextNode), null, null,
                            Schema.Field.Order.ASCENDING);
                    fields.add(field);
                    break;

                case STRING:
                    field = new Schema.Field(mapEntry.getKey(), AvroUtils.wrapAsNullable(AvroUtils._string()), null, null,
                            Schema.Field.Order.ASCENDING);
                    fields.add(field);
                    break;

                case BOOLEAN:
                    field = new Schema.Field(mapEntry.getKey(), AvroUtils.wrapAsNullable(AvroUtils._boolean()), null, null,
                            Schema.Field.Order.ASCENDING);
                    fields.add(field);
                    break;

                case ARRAY:
                    final ArrayNode arrayNode = (ArrayNode) nextNode;
                    Iterator<JsonNode> nodeIterator = arrayNode.elements();
                    if (nodeIterator.hasNext()) {
                        field = new Schema.Field(mapEntry.getKey(), Schema.createArray(getAvroSchema(nodeIterator.next())), null,
                                null, Schema.Field.Order.ASCENDING);
                    } else {
                        // if array field is empty
                        field = new Schema.Field(mapEntry.getKey(),
                                Schema.createArray(AvroUtils.wrapAsNullable(AvroUtils._string())), null, null,
                                Schema.Field.Order.ASCENDING);
                    }
                    fields.add(field);
                    break;

                case OBJECT:
                    field = new Schema.Field(mapEntry.getKey(), createSubRecord(nextNode), null, null,
                            Schema.Field.Order.ASCENDING);
                    fields.add(field);
                    break;

                default:
                    logger.error("Node type not found - " + nextNode.getNodeType());
                    break;
                }
            } else {
                field = new Schema.Field(mapEntry.getKey(), AvroUtils.wrapAsNullable(AvroUtils._string()), null, null,
                        Schema.Field.Order.ASCENDING);
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Get an Avro schema using {@link AvroUtils#wrapAsNullable(Schema)} by node type.
     * 
     * @param node Json node.
     * @return an Avro schema using {@link AvroUtils#wrapAsNullable(Schema)} by node type.
     */
    Schema getAvroSchema(JsonNode node) {
        if (node instanceof TextNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._string());
        } else if (node instanceof IntNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._int());
        } else if (node instanceof LongNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._long());
        } else if (node instanceof DoubleNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._double());
        } else if (node instanceof BooleanNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._boolean());
        } else if (node instanceof NullNode) {
            return AvroUtils.wrapAsNullable(AvroUtils._string());
        } else {
            return createSubRecord(node);
        }
    }

    /**
     * @return subrecord random name.
     * @param node
     */
    private Schema createSubRecord(JsonNode node) {
        // Create a nameless temporary record to get a fingerprint from.
        Schema record = Schema.createRecord(getFields(node));
        long fingerprint = SchemaNormalization.parsingFingerprint64(record);

        // Use the fingerprint in the record name. Note that we have to traverse the node twice.
        return Schema.createRecord(("subrecord" + fingerprint).replace('-', '_'), null, null, false, getFields(node));
    }
}
