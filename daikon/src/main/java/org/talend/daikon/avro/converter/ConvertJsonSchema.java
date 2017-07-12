package org.talend.daikon.avro.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class ConvertJsonSchema {

    private ConvertJson convertJson;

    private ObjectMapper mapper;

    /**
     * Constructor
     *
     * @param mapper
     */
    public ConvertJsonSchema(final ObjectMapper mapper) {
        this.convertJson = new ConvertJson(mapper);
        this.mapper = mapper;
    }

    /**
     * Generate Avro Generic Record from Json Node.
     *
     * Iterate Json Node fields and construct the Avro Generic Record.
     * 
     * @param jsonNode to convert to an Avro Generic Record
     * @param schema of jsonNode
     * @return Avro Generic Record
     */
    public GenericRecord getOutputRecord(final JsonNode jsonNode, Schema schema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(schema);

        final Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();
        Map.Entry<String, JsonNode> map;

        while (elements.hasNext()) {
            map = elements.next();
            final JsonNode nextNode = map.getValue();

            if (!(nextNode instanceof NullNode)) {
                if (nextNode instanceof TextNode) {
                    outputRecord.set(map.getKey(), nextNode.textValue());
                } else if (nextNode instanceof ObjectNode) {
                    Schema schemaTo = convertJson.convertToAvro(nextNode.toString());
                    GenericRecord record = getOutputRecord(nextNode, schemaTo);
                    outputRecord.set(map.getKey(), record);
                } else if (nextNode instanceof ArrayNode) {
                    List<Object> listRecords = new ArrayList<Object>();
                    Iterator<JsonNode> elementsIterator = ((ArrayNode) nextNode).elements();
                    while (elementsIterator.hasNext()) {
                        JsonNode nodeTo = elementsIterator.next();
                        if (nodeTo instanceof TextNode) {
                            listRecords.add(nodeTo.textValue());
                        } else {
                            Schema schemaTo = convertJson.convertToAvro(nodeTo.toString());
                            listRecords.add(getOutputRecord(nodeTo, schemaTo));
                        }
                    }
                    outputRecord.set(map.getKey(), listRecords);
                }
            } else {
                outputRecord.set(map.getKey(), null);
            }
        }
        return outputRecord.build();
    }
}
