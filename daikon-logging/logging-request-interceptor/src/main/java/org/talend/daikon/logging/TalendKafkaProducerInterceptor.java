package org.talend.daikon.logging;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TalendKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendKafkaProducerInterceptor.class);

    @Override
    public ProducerRecord<Object, Object> onSend(final ProducerRecord<Object, Object> record) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("onSend topic=%s key=%s value=%s %d \n", record.topic(), record.key(),
                    record.value().toString(), record.partition()));
        }
        return record;
    }

    @Override
    public void configure(Map<String, ?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAcknowledgement(RecordMetadata arg0, Exception arg1) {
        // TODO Auto-generated method stub

    }
}
