package org.talend.daikon.logging;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TalendKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendKafkaProducerInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public ProducerRecord<Object, Object> onSend(final ProducerRecord<Object, Object> record) {
        if (LOGGER.isTraceEnabled()) {
            try {
                String s = new ObjectMapper().writeValueAsString(record.key());
                GenericMessage<Object> message = (GenericMessage<Object>) record.value();
                if (message != null) {
                    LOGGER.trace(String.format("onSend topic=%s message=%s \n", record.topic(), message.getPayload()));
                }
            } catch (Exception e) {
                LOGGER.error("Error executing interceptor onSend for topic: {}, partition: {}", record.topic(),
                        record.partition(), e);
            }
        }

        return record;
    }

    @Override
    public void configure(Map<String, ?> arg0) {

    }

    @Override
    public void close() {

    }

    @Override
    public void onAcknowledgement(RecordMetadata arg0, Exception arg1) {

    }
}
