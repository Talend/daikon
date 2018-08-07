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
package org.talend.daikon.messages.demo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public void createProduct(@RequestBody Product product) {
        // create event
        ProductCreated event = ProductCreated.newBuilder()
                .setHeader(messageHeaderFactory.createMessageHeader(MessageTypes.EVENT, "ProductCreated")).setId(product.getId())
                .setLabel(product.getLabel()).setColor(product.getColor()).build();

        MessageKey messageKey = messageKeyFactory.createMessageKey();

        Message<ProductCreated> message = MessageBuilder.withPayload(event).setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build();

        // send event
        productEventsSink.productEvents().send(message);

        LOG.info("Message with UUID {} sent", event.getHeader().getId());
    }

    @PutMapping(path = "{id}")
    public void editProduct(@PathVariable("id") String id, @RequestBody Product product) {
        ProductUpdated event = ProductUpdated.newBuilder().setProductId(id).setColor(product.getColor())
                .setLabel(product.getLabel()).build();

        MessageEnvelope envelope = messageEnvelopeHandler.wrap(MessageTypes.EVENT, "ProductUpdated", event, "json");

        MessageKey messageKey = messageKeyFactory.createMessageKey();

        Message<MessageEnvelope> message = MessageBuilder.withPayload(envelope).setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build();

        // send event
        productEventsSink.productEvents().send(message);

        LOG.info("Message with UUID {} sent", event.getProductId());
    }

    static class ProductUpdated {

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

        public static ProductUpdated.Builder newBuilder() {
            return new ProductUpdated.Builder();
        }

        static class Builder {

            private String productId;

            private String label;

            private String color;

            public ProductUpdated.Builder setProductId(String productId) {
                this.productId = productId;
                return this;
            }

            public ProductUpdated.Builder setLabel(String label) {
                this.label = label;
                return this;
            }

            public ProductUpdated.Builder setColor(String color) {
                this.color = color;
                return this;
            }

            public ProductUpdated build() {
                ProductUpdated record = new ProductUpdated();
                record.setColor(this.color);
                record.setLabel(this.label);
                record.setProductId(productId);

                return record;
            }
        }
    }

}
