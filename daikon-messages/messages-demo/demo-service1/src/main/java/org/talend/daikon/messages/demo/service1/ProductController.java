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
package org.talend.daikon.messages.demo.service1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.talend.daikon.messages.MessageEnvelope;
import org.talend.daikon.messages.MessageKey;
import org.talend.daikon.messages.MessageTypes;
import org.talend.daikon.messages.demo1.ProductCreated;
import org.talend.daikon.messages.envelope.MessageEnvelopeHandler;
import org.talend.daikon.messages.header.producer.MessageHeaderFactory;
import org.talend.daikon.messages.keys.MessageKeyFactory;

@RestController("/")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private MessageHeaderFactory messageHeaderFactory;

    @Autowired
    private MessageKeyFactory messageKeyFactory;

    @Autowired
    private MessageEnvelopeHandler messageEnvelopeHandler;

    @Autowired
    private ProductEventsSink productEventsSink;

    @RequestMapping(method = RequestMethod.POST)
    public void createProduct(@RequestBody Product product) {
        // create event
        ProductCreated event = new ProductCreated();
        event.setHeader(messageHeaderFactory.createMessageHeader(MessageTypes.EVENT, "ProductCreated"));
        event.setId(product.getId());
        event.setLabel(product.getLabel());
        event.setColor(product.getColor());

        MessageKey messageKey = messageKeyFactory.createMessageKey();

        Message<ProductCreated> message = MessageBuilder.withPayload(event).setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build();

        // send event
        productEventsSink.productEvents().send(message);

        LOG.info("Message with UUID " + event.getHeader().getId() + " sent");
    }

    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    public void editProduct(@PathVariable("id") String id, @RequestBody Product product) {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        event.setProductId(id);
        event.setColor(product.getColor());
        event.setLabel(product.getLabel());
        MessageEnvelope envelope = messageEnvelopeHandler.wrap(MessageTypes.EVENT, "ProductUpdated", event, "json");

        MessageKey messageKey = messageKeyFactory.createMessageKey();

        Message<MessageEnvelope> message = MessageBuilder.withPayload(envelope).setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build();

        // send event
        productEventsSink.productEvents().send(message);

    }

    private static class ProductUpdatedEvent {

        private String productId;

        private String label;

        private String color;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

}
