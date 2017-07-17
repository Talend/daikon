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
package org.talend.daikon.avro.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.talend.daikon.avro.inferrer.JsonSchemaInferrer;
import org.talend.daikon.exception.TalendRuntimeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Converts Json String to Avro Generic Record and vice-versa.
 */
public class JsonGenericRecordConverter implements AvroConverter<String, GenericRecord> {

    private JsonSchemaInferrer jsonSchemaInferrer;

    private Schema schema;

    /**
     * Constructor
     */
    public JsonGenericRecordConverter() {
        this.jsonSchemaInferrer = JsonSchemaInferrer.createJsonSchemaInferrer();
    }

    /**
     * Constructor
     * 
     * @param schema
     */
    public JsonGenericRecordConverter(Schema schema) {
        this.jsonSchemaInferrer = JsonSchemaInferrer.createJsonSchemaInferrer();
        this.schema = schema;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public Class<String> getDatumClass() {
        return String.class;
    }

    @Override
    public String convertToDatum(GenericRecord record) {
        return record.toString();
    }

    /**
     * Convert Json String to Avro Generic Record.
     *
     * TalendRuntimeException thrown when an IOException or RuntimeException occurred.
     *
     * @param json string to convert
     * @return Avro Generic Record.
     */
    @Override
    public GenericRecord convertToAvro(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            return getOutputRecord(jsonNode, schema);
        } catch (IOException | TalendRuntimeException e) {
            throw TalendRuntimeException.createUnexpectedException(e.getCause());
        }
    }

    /**
     * Generate Avro Generic Record from Json Node.
     *
     * Iterate Json Node fields and construct the Avro Generic Record.
     *
     * @param jsonNode to convert to Avro Generic Record
     * @param schema of jsonNode
     * @return Avro Generic Record
     */
    private GenericRecord getOutputRecord(final JsonNode jsonNode, Schema schema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(schema);
        final Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();
        Map.Entry<String, JsonNode> mapEntry;

        while (elements.hasNext()) {
            mapEntry = elements.next();
            final JsonNode nextNode = mapEntry.getValue();

            if (!(nextNode instanceof NullNode)) {
                if (nextNode instanceof ValueNode) {
                    if (nextNode instanceof TextNode) {
                        outputRecord.set(mapEntry.getKey(), nextNode.textValue());
                    } else if (nextNode instanceof IntNode) {
                        outputRecord.set(mapEntry.getKey(), nextNode.intValue());
                    } else if (nextNode instanceof LongNode) {
                        outputRecord.set(mapEntry.getKey(), nextNode.longValue());
                    } else if (nextNode instanceof DoubleNode) {
                        outputRecord.set(mapEntry.getKey(), nextNode.doubleValue());
                    } else if (nextNode instanceof BooleanNode) {
                        outputRecord.set(mapEntry.getKey(), nextNode.booleanValue());
                    }
                } else if (nextNode instanceof ObjectNode) {
                    Schema schemaTo = jsonSchemaInferrer.inferSchema(nextNode.toString());
                    GenericRecord record = getOutputRecord(nextNode, schemaTo);
                    outputRecord.set(mapEntry.getKey(), record);
                } else if (nextNode instanceof ArrayNode) {
                    List<Object> listRecords = new ArrayList<Object>();
                    Iterator<JsonNode> elementsIterator = ((ArrayNode) nextNode).elements();
                    while (elementsIterator.hasNext()) {
                        JsonNode nodeTo = elementsIterator.next();
                        if (nodeTo instanceof ValueNode) {
                            if (nodeTo instanceof TextNode) {
                                listRecords.add(nodeTo.textValue());
                            } else if (nodeTo instanceof IntNode) {
                                listRecords.add(nodeTo.intValue());
                            } else if (nodeTo instanceof LongNode) {
                                listRecords.add(nodeTo.longValue());
                            } else if (nodeTo instanceof DoubleNode) {
                                listRecords.add(nodeTo.doubleValue());
                            } else if (nodeTo instanceof BooleanNode) {
                                listRecords.add(nodeTo.booleanValue());
                            }
                        } else {
                            Schema schemaTo = jsonSchemaInferrer.inferSchema(nodeTo.toString());
                            listRecords.add(getOutputRecord(nodeTo, schemaTo));
                        }
                    }
                    outputRecord.set(mapEntry.getKey(), listRecords);
                }
            } else {
                outputRecord.set(mapEntry.getKey(), null);
            }
        }
        return outputRecord.build();
    }
}
