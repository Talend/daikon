package org.talend.daikon.logging.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.internals.ConsumerInterceptors;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.junit.Test;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.talend.daikon.logging.TalendKafkaConsumerInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsumerInterceptorsTest {

    private final int filterPartition1 = 5;

    private final int filterPartition2 = 6;

    private final String topic = "test";

    private final int partition = 1;

    private final TopicPartition tp = new TopicPartition(topic, partition);

    private final TopicPartition filterTopicPart1 = new TopicPartition("test5", filterPartition1);

    private final TopicPartition filterTopicPart2 = new TopicPartition("test6", filterPartition2);

    String ip = "192.168.50.130";

    String msg = "kafka_message" + ip;

    Message<String> message = MessageBuilder.withPayload(msg).setHeader(KafkaHeaders.MESSAGE_KEY, "key").build();

    private final ConsumerRecord<Object, Object> consumerRecord = new ConsumerRecord<>(topic, partition, 0, 0L,
            TimestampType.CREATE_TIME, 0L, 0, 0, 1, message);

    private int onCommitCount = 0;

    private int onConsumeCount = 0;

    @Test
    public void testOnConsume() {
        List<ConsumerInterceptor<Object, Object>> interceptorList = new ArrayList<>();
        TalendKafkaConsumerInterceptor interceptor = new TalendKafkaConsumerInterceptor();
        interceptorList.add(interceptor);
        ConsumerInterceptors<Object, Object> interceptors = new ConsumerInterceptors<>(interceptorList);

        Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        List<ConsumerRecord<Object, Object>> list = new ArrayList<>();
        list.add(consumerRecord);
        records.put(tp, list);
        ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords<>(records);
        ConsumerRecords<Object, Object> interceptedRecords = interceptors.onConsume(consumerRecords);
        assertTrue(interceptedRecords.partitions().contains(tp));
        assertFalse(interceptedRecords.partitions().contains(filterTopicPart1));
        assertFalse(interceptedRecords.partitions().contains(filterTopicPart2));
        ConsumerRecords<Object, Object> partInterceptedRecs = interceptors.onConsume(consumerRecords);
        assertEquals(1, partInterceptedRecs.count());
        interceptors.close();
    }
}
