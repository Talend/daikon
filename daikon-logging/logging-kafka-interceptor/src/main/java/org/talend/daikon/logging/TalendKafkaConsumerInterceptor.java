package org.talend.daikon.logging;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TalendKafkaConsumerInterceptor implements ConsumerInterceptor<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalendKafkaConsumerInterceptor.class);

    @SuppressWarnings({ "unchecked" })
    @Override
    public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> records) {
        if (LOGGER.isTraceEnabled()) {
            try {
                Iterator<ConsumerRecord<Object, Object>> consumerRecords = records.iterator();
                if (consumerRecords != null) {
                    consumerRecords.forEachRemaining(c -> {
                        String message = new String((byte[]) c.value(), StandardCharsets.UTF_8);
                        LOGGER.trace(String.format("onConsume topic=%s partition=%d message=%s \n", c.topic(), c.partition(),
                                message));
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Error executing interceptor onConsume", e);
            }
        }
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        //TODO
    }

    @Override
    public void close() {
        //TODO
    }

    @Override
    public void configure(Map<String, ?> arg0) {
        //TODO
    }
}
