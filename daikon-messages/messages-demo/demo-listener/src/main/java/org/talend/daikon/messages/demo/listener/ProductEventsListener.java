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
package org.talend.daikon.messages.demo.listener;

import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.talend.daikon.messages.header.consumer.ExecutionContextUpdater;

@Component
public class ProductEventsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsListener.class);

    @Autowired
    private ExecutionContextUpdater executionContextUpdater;

    @StreamListener("productEvents")
    public void onEvent(Message message) {
        IndexedRecord record = (IndexedRecord) message.getPayload();
        executionContextUpdater.updateExecutionContext(record);
        LOGGER.info("Received a product event " + message.getPayload());

    }
}
