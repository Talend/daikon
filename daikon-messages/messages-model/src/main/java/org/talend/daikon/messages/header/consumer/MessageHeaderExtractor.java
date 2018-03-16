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
package org.talend.daikon.messages.header.consumer;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.specific.SpecificData;
import org.talend.daikon.messages.MessageHeader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to access normalized message header
 */
public final class MessageHeaderExtractor {

    private static final int EXPECTED_HEADER_POSITION = 0;

    private static final String PROVIDED_FIRST_FIELD_RECORD_LABEL = "Provided message's first field is not a record but ";

    private static final String PROVIDED_FIRST_FIELD_HEADER_LABEL = "Provided message's first field is not a header but ";

    private static final String MESSAGE_SHOULD_NOT_BE_NULL_LABEL = "Message should not be null";

    private static final String CANNOT_INITIALIZE_MESSAGE_HEADER_SCHEMA_LABEL = "Cannot initialize message header schema";

    private final Schema messageHeaderSchema;

    /**
     * Creates a new MessageHeaderExtractor.
     *
     * Tries to load the message header schema
     *
     * @throws IllegalStateException if the message header schema cannot be loaded
     */
    public MessageHeaderExtractor() {
        try (InputStream schemaInputStream = this.getClass().getResourceAsStream("/MessageHeader.avsc")) {
            messageHeaderSchema = new Schema.Parser().parse(schemaInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(CANNOT_INITIALIZE_MESSAGE_HEADER_SCHEMA_LABEL, e);
        }
    }

    /**
     * Extracts a {@link MessageHeader} from the provided message
     *
     * @param message the message
     * @return the message's header
     * @throws IllegalArgumentException if the message does not contain a header
     */
    public MessageHeader extractHeader(IndexedRecord message) {
        assert message != null : MESSAGE_SHOULD_NOT_BE_NULL_LABEL;
        final Schema messageSchema = message.getSchema();
        final Schema firstFieldSchema = messageSchema.getFields().get(EXPECTED_HEADER_POSITION).schema();
        if (firstFieldSchema.getType() != Schema.Type.RECORD) {
            throw new IllegalArgumentException(PROVIDED_FIRST_FIELD_RECORD_LABEL + firstFieldSchema.getType());
        }
        if (!firstFieldSchema.getFullName().equals(messageHeaderSchema.getFullName())) {
            throw new IllegalArgumentException(PROVIDED_FIRST_FIELD_HEADER_LABEL + firstFieldSchema.getFullName());
        }
        final Object firstFieldValue = message.get(EXPECTED_HEADER_POSITION);
        if (firstFieldValue instanceof MessageHeader) {
            return MessageHeader.class.cast(firstFieldValue);
        } else {
            return (MessageHeader) SpecificData.get().deepCopy(messageHeaderSchema, firstFieldValue);
        }
    }
}
