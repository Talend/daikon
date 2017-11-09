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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.messages.MessageKey;
import org.talend.daikon.messages.MessageTypes;
import org.talend.daikon.messages.demo1.ProductCreated;
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

}
