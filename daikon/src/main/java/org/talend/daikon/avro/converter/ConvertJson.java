package org.talend.daikon.avro.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ConvertJson implements AvroConverter<String, Schema> {

    private static final Logger logger = LoggerFactory.getLogger(AvroConverter.class);

    private static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String ARRAY = "array";

    private static final String ITEMS = "items";

    private static final String STRING = "string";

    private static final String RECORD = "record";

    private static final String FIELDS = "fields";

    private static final String NULL = "null";

    private static final String INT = "int";

    private static final String DOUBLE = "double";

    private static final String LONG = "long";

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
            finalSchema.put(NAME, "outer_record");
            finalSchema.put(TYPE, RECORD);
            finalSchema.set(FIELDS, getFields(jsonNode));
            outputSchema = new Schema.Parser().parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalSchema));
        } catch(Exception ex) {
            logger.error(ex.getMessage());
        }
        return outputSchema;
    }

    /**
     * Construct the fields schema of json node. Supported data types are: INT, LONG, DOUBLE, STRING, ARRAY, OBJECT.
     *
     * Example:
     *
     * jsonNode parameter: {"a": {"b": "b1"}, "d": 100}
     *
     * jsonNode parameter fields schema:
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
                        fieldNode.put(NAME, map.getKey());
                        fieldTypeArray.add(NULL);
                        if (nextNode.isInt()) {
                            fieldTypeArray.add(INT);
                        } else if (nextNode.isLong()) {
                            fieldTypeArray.add(LONG);
                        } else {
                            fieldTypeArray.add(DOUBLE);
                        }
                        fieldNode.put(TYPE, fieldTypeArray);
                        fields.add(fieldNode);
                        break;

                    case STRING:
                        fieldNode.put(NAME, map.getKey());
                        fieldTypeArray.add(NULL);
                        fieldTypeArray.add(STRING);
                        fieldNode.put(TYPE, fieldTypeArray);
                        fields.add(fieldNode);
                        break;

                    case ARRAY:
                        final ArrayNode arrayNode = (ArrayNode) nextNode;
                        final JsonNode element = arrayNode.get(0);
                        final ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put(NAME, map.getKey());

                        if (element.getNodeType() == JsonNodeType.NUMBER) {
                            fieldNode.put(TYPE, ARRAY);
                            fieldTypeArray.add(NULL);
                            if (nextNode.get(0).isInt()) {
                                fieldTypeArray.add(INT);
                            } else if (nextNode.get(0).isLong()) {
                                fieldTypeArray.add(LONG);
                            } else {
                                fieldTypeArray.add(DOUBLE);
                            }
                            fieldNode.put(ITEMS, fieldTypeArray);
                            objectNode.set(TYPE, fieldNode);
                        } else if (element.getNodeType() == JsonNodeType.STRING) {
                            fieldNode.put(TYPE, ARRAY);
                            fieldTypeArray.add(NULL);
                            fieldTypeArray.add(STRING);
                            fieldNode.put(ITEMS, fieldTypeArray);
                            objectNode.set(TYPE, fieldNode);
                        } else {
                            objectNode.set(TYPE, mapper.createObjectNode().put(TYPE, ARRAY).set(ITEMS, mapper.createObjectNode()
                                    .put(TYPE, RECORD).put(NAME, generateRandomNumber(map)).set(FIELDS, getFields(element))));
                        }
                        fields.add(objectNode);
                        break;

                    case OBJECT:
                        ObjectNode node = mapper.createObjectNode();
                        node.put(NAME, map.getKey());
                        node.set(TYPE, mapper.createObjectNode().put(TYPE, RECORD).put(NAME, generateRandomNumber(map)).set(FIELDS,
                                getFields(nextNode)));
                        fields.add(node);
                        break;

                    default:
                        logger.error("Node type not found - " + nextNode.getNodeType());
                }
            } else {
                fieldNode.put(NAME, map.getKey());
                fieldTypeArray.add(NULL);
                fieldTypeArray.add(STRING);
                fieldNode.put(TYPE, fieldTypeArray);
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
        return (map.getKey() + "_" + new Random().nextInt(100));
    }
}
