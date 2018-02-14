package org.talend.daikon.logging.kafka;

import com.github.charithe.kafka.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.ProducerInterceptors;
import org.apache.kafka.common.TopicPartition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.talend.daikon.logging.TalendKafkaProducerInterceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class ProducerInterceptorsTest {

    private final TopicPartition tp = new TopicPartition("test", 0);

    private final ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>("test", 0, 1, "value");

    private int onSendCount = 0;

    @Test
    public void testOnSend() {
        List<ProducerInterceptor<Object, Object>> interceptorList = new ArrayList<>();
        TalendKafkaProducerInterceptor interceptor = new TalendKafkaProducerInterceptor();
        interceptorList.add(interceptor);
        ProducerInterceptors<Object, Object> interceptors = new ProducerInterceptors<>(interceptorList);

        // verify that onSend() mutates the record as expected
        ProducerRecord<Object, Object> interceptedRecord = interceptors.onSend(producerRecord);
        assertEquals(2, onSendCount);
        assertEquals(producerRecord.topic(), interceptedRecord.topic());
        assertEquals(producerRecord.partition(), interceptedRecord.partition());
        assertEquals(producerRecord.key(), interceptedRecord.key());
        assertEquals(interceptedRecord.value(), producerRecord.value());

        // onSend() mutates the same record the same way
        ProducerRecord<Object, Object> anotherRecord = interceptors.onSend(producerRecord);
        assertEquals(4, onSendCount);
        assertEquals(interceptedRecord, anotherRecord);

        // verify that if one of the interceptors throws an exception, other interceptors' callbacks are still called
        //interceptor1.injectOnSendError(true);
        ProducerRecord<Object, Object> partInterceptRecord = interceptors.onSend(producerRecord);
        assertEquals(6, onSendCount);
        assertEquals(partInterceptRecord.value(), producerRecord.value());

        // verify the record remains valid if all onSend throws an exception
        //interceptor2.injectOnSendError(true);
        ProducerRecord<Object, Object> noInterceptRecord = interceptors.onSend(producerRecord);
        assertEquals(producerRecord, noInterceptRecord);

        interceptors.close();
    }

}
