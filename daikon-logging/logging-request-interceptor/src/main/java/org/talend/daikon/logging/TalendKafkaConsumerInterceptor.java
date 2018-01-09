package org.talend.daikon.logging;

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

    @Override
    public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> records) {
        if (LOGGER.isTraceEnabled()) {
            Iterator<ConsumerRecord<Object, Object>> consumerRecords = records.iterator();
            if (consumerRecords != null) {
                consumerRecords.forEachRemaining(c -> {
                    LOGGER.trace(String.format("onConsume topic=%s partition=%d value=%s %d \n", c.topic(), c.partition(),
                            c.value().toString(), c.partition()));
                });
            }
        }

        return records;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void configure(Map<String, ?> arg0) {
        // TODO Auto-generated method stub

    }
}
