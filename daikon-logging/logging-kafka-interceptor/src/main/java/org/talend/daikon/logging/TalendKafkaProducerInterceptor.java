package org.talend.daikon.logging;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.GenericMessage;
import org.talend.schema.model.KafkaMessageKey;
import org.talend.schema.serialization.KafkaMessageKeyDeserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TalendKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendKafkaProducerInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public ProducerRecord<Object, Object> onSend(final ProducerRecord<Object, Object> record) {
        if (LOGGER.isTraceEnabled()) {
            try {
                String s = new ObjectMapper().writeValueAsString(record.key());
                KafkaMessageKeyDeserializer deserializer = new KafkaMessageKeyDeserializer();
                KafkaMessageKey deserializedKey = deserializer.deserialize(null, s.getBytes(Charset.forName("UTF-8")));
                GenericMessage<Object> message = (GenericMessage<Object>) record.value();
                if (message != null) {
                    LOGGER.trace(String.format("onSend topic=%s tenantId=%s message=%s \n", record.topic(),
                            deserializedKey.getTenantId(), message.getPayload()));
                }
                deserializer.close();
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
