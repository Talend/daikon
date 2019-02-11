package org.talend.daikon.logging;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.messages.MessageKey;

public class TalendKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendKafkaProducerInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public ProducerRecord<Object, Object> onSend(final ProducerRecord<Object, Object> record) {
        if (LOGGER.isTraceEnabled()) {
            try {
                String tenantId = ((MessageKey) record.key()).getTenantId();
                LOGGER.trace(String.format("onSend topic=%s tenantId=%s \n", record.topic(), tenantId));
            } catch (Exception e) {
                LOGGER.error("Error executing interceptor onSend for topic: {}, partition: {}", record.topic(),
                        record.partition(), e);
            }
        }

        return record;
    }

    @Override
    public void configure(Map<String, ?> arg0) {
        // TODO
    }

    @Override
    public void close() {
        // TODO
    }

    @Override
    public void onAcknowledgement(RecordMetadata arg0, Exception arg1) {
        // TODO
    }
}
