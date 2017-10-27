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
package org.talend.daikon.messages.envelope;

import org.talend.daikon.messages.MessageEnvelope;
import org.talend.daikon.messages.MessageHeader;
import org.talend.daikon.messages.MessagePayload;
import org.talend.daikon.messages.MessageTypes;
import org.talend.daikon.messages.header.MessageHeaderFactory;

public class MessageEnvelopeHandlerImpl implements MessageEnvelopeHandler {

    private final MessageConverterRegistry messageConverterRegistry;

    private final MessageHeaderFactory messageHeaderFactory;

    public MessageEnvelopeHandlerImpl(MessageConverterRegistry messageConverterRegistry,
            MessageHeaderFactory messageHeaderFactory) {
        this.messageConverterRegistry = messageConverterRegistry;
        this.messageHeaderFactory = messageHeaderFactory;
    }

    @Override
    public <T> MessageEnvelope wrap(MessageTypes type, String messageName, T payload, String format) {
        return MessageEnvelope.newBuilder().setHeader(createMessageHeader(type, messageName))
                .setPayload(createMessagePayload(payload, format)).build();
    }

    @Override
    public <T> T unwrap(MessageEnvelope envelop) {
        MessageConverter messageConverter = this.getMessageConverter(envelop.getPayload().getFormat());
        String content = envelop.getPayload().getContent();
        return messageConverter.deserialize(content);
    }

    private <T> MessagePayload createMessagePayload(T payload, String format) {
        MessageConverter messageConverter = this.getMessageConverter(format);
        String content = messageConverter.serialize(payload);
        return MessagePayload.newBuilder().setFormat(format).setContent(content).build();
    }

    private MessageHeader createMessageHeader(MessageTypes type, String messageName) {
        switch (type) {
        case COMMAND:
            return messageHeaderFactory.createCommandHeader(messageName);
        case EVENT:
            return messageHeaderFactory.createEventHeader(messageName);
        default:
            throw new IllegalArgumentException("Unsupported message type " + type);
        }
    }

    private MessageConverter getMessageConverter(String format) {
        return this.messageConverterRegistry.getMessageConverter(format);
    }
}
