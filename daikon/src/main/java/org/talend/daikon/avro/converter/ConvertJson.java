package org.talend.daikon.avro.converter;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConvertJson implements AvroConverter<String, Schema> {

    private static final Logger logger = LoggerFactory.getLogger(AvroConverter.class);

    private final ObjectMapper mapper;

    /**
     * Constructor
     *
     * @param mapper
     */
    public ConvertJson(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public Class<String> getDatumClass() {
        return null;
    }

    @Override
    public String convertToDatum(Schema value) {
        return null;
    }

    /**
     * Convert json string to avro schema.
     *
     * Example:
     *
     * json string parameter: {"a": {"b": "b1"}, "d": 100}
     *
     * avro schema constructed:
     * {"type":"record","name":"outer_record","namespace":"org.talend",
     * "fields":[{"name":"a","type":{"type":"record","name":"a_98","fields":[{"name":"b","type":["null","string"]}]}},
     * {"name":"d","type":["null","int"]}]}
     *
     * @param json string to convert
     * @return avro schema constructed
     */
    @Override
    public Schema convertToAvro(String json) {
        Schema outputSchema = null;
        try {
            final JsonNode jsonNode = mapper.readTree(json);
            final ObjectNode finalSchema = mapper.createObjectNode();
            finalSchema.put("namespace", "org.talend");
            finalSchema.put(ConvertJsonConstant.NAME, "outer_record");
            finalSchema.put(ConvertJsonConstant.TYPE, ConvertJsonConstant.RECORD);
            finalSchema.set(ConvertJsonConstant.FIELDS, getFields(jsonNode));
            outputSchema = new Schema.Parser().parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalSchema));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return outputSchema;
    }

    /**
     * Construct the fields schema from json node. Supported data types are: INT, LONG, DOUBLE, STRING, ARRAY, OBJECT.
     *
     * Example:
     *
     * jsonNode parameter: {"a": {"b": "b1"}, "d": 100}
     *
     * jsonNode fields schema:
     * [{"name":"a","type":{"type":"record","name":"a_49","fields":[{"name":"b","type":["null","string"]}]}},{"name":"d","type":["null","int"]}]
     *
     * @param jsonNode
     * @return fields schema of json node
     */
    public ArrayNode getFields(final JsonNode jsonNode) {
        final ArrayNode fields = mapper.createArrayNode();
        final Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();

        Map.Entry<String, JsonNode> map;
        while (elements.hasNext()) {
            map = elements.next();
            final JsonNode nextNode = map.getValue();
            ObjectNode fieldNode = mapper.createObjectNode();
            ArrayNode fieldTypeArray = mapper.createArrayNode();

            if (!(nextNode instanceof NullNode)) {
                switch (nextNode.getNodeType()) {
                case NUMBER:
                    fieldNode.put(ConvertJsonConstant.NAME, map.getKey());
                    fieldTypeArray.add(ConvertJsonConstant.NULL);
                    if (nextNode.isInt()) {
                        fieldTypeArray.add(ConvertJsonConstant.INT);
                    } else if (nextNode.isLong()) {
                        fieldTypeArray.add(ConvertJsonConstant.LONG);
                    } else {
                        fieldTypeArray.add(ConvertJsonConstant.DOUBLE);
                    }
                    fieldNode.put(ConvertJsonConstant.TYPE, fieldTypeArray);
                    fields.add(fieldNode);
                    break;

                case STRING:
                    fieldNode.put(ConvertJsonConstant.NAME, map.getKey());
                    fieldTypeArray.add(ConvertJsonConstant.NULL);
                    fieldTypeArray.add(ConvertJsonConstant.STRING);
                    fieldNode.put(ConvertJsonConstant.TYPE, fieldTypeArray);
                    fields.add(fieldNode);
                    break;

                case ARRAY:
                    final ArrayNode arrayNode = (ArrayNode) nextNode;
                    final JsonNode element = arrayNode.get(0);
                    final ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put(ConvertJsonConstant.NAME, map.getKey());

                    if (element.getNodeType() == JsonNodeType.NUMBER) {
                        fieldNode.put(ConvertJsonConstant.TYPE, ConvertJsonConstant.ARRAY);
                        fieldTypeArray.add(ConvertJsonConstant.NULL);
                        if (nextNode.get(0).isInt()) {
                            fieldTypeArray.add(ConvertJsonConstant.INT);
                        } else if (nextNode.get(0).isLong()) {
                            fieldTypeArray.add(ConvertJsonConstant.LONG);
                        } else {
                            fieldTypeArray.add(ConvertJsonConstant.DOUBLE);
                        }
                        fieldNode.put(ConvertJsonConstant.ITEMS, fieldTypeArray);
                        objectNode.set(ConvertJsonConstant.TYPE, fieldNode);
                    } else if (element.getNodeType() == JsonNodeType.STRING) {
                        fieldNode.put(ConvertJsonConstant.TYPE, ConvertJsonConstant.ARRAY);
                        fieldTypeArray.add(ConvertJsonConstant.NULL);
                        fieldTypeArray.add(ConvertJsonConstant.STRING);
                        fieldNode.put(ConvertJsonConstant.ITEMS, fieldTypeArray);
                        objectNode.set(ConvertJsonConstant.TYPE, fieldNode);
                    } else {
                        objectNode.set(ConvertJsonConstant.TYPE,
                                mapper.createObjectNode().put(ConvertJsonConstant.TYPE, ConvertJsonConstant.ARRAY).set(
                                        ConvertJsonConstant.ITEMS,
                                        mapper.createObjectNode().put(ConvertJsonConstant.TYPE, ConvertJsonConstant.RECORD)
                                                .put(ConvertJsonConstant.NAME, generateRandomNumber(map))
                                                .set(ConvertJsonConstant.FIELDS, getFields(element))));
                    }
                    fields.add(objectNode);
                    break;

                case OBJECT:
                    ObjectNode node = mapper.createObjectNode();
                    node.put(ConvertJsonConstant.NAME, map.getKey());
                    node.set(ConvertJsonConstant.TYPE,
                            mapper.createObjectNode().put(ConvertJsonConstant.TYPE, ConvertJsonConstant.RECORD)
                                    .put(ConvertJsonConstant.NAME, generateRandomNumber(map))
                                    .set(ConvertJsonConstant.FIELDS, getFields(nextNode)));
                    fields.add(node);
                    break;

                default:
                    logger.error("Node type not found - " + nextNode.getNodeType());
                    break;
                }
            } else {
                fieldNode.put(ConvertJsonConstant.NAME, map.getKey());
                fieldTypeArray.add(ConvertJsonConstant.NULL);
                fieldTypeArray.add(ConvertJsonConstant.STRING);
                fieldNode.put(ConvertJsonConstant.TYPE, fieldTypeArray);
                fields.add(fieldNode);
            }
        }
        return fields;
    }

    /**
     * Generate random.
     *
     * @param map to create random number
     */
    public String generateRandomNumber(Map.Entry<String, JsonNode> map) {
        return (map.getKey() + "_" + UUID.randomUUID().toString().replace("-", "_"));
    }
}
