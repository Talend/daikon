package org.talend.daikon.logging.kafka;

import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.internals.ProducerInterceptors;
import org.junit.Test;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.talend.daikon.logging.TalendKafkaProducerInterceptor;
import static org.junit.Assert.assertEquals;

public class ProducerInterceptorsTest {

    @Test
    public void testOnSend() {

        String ip = "192.168.50.130";
        String msg = "kafka_message," + ip;
        Message<String> message = MessageBuilder.withPayload(msg).setHeader(KafkaHeaders.MESSAGE_KEY, "key").build();
        ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>("test", 0, 1, message);

        List<ProducerInterceptor<Object, Object>> interceptorList = new ArrayList<>();
        TalendKafkaProducerInterceptor interceptor = new TalendKafkaProducerInterceptor();
        interceptorList.add(interceptor);
        ProducerInterceptors<Object, Object> interceptors = new ProducerInterceptors<>(interceptorList);

        // verify that onSend() mutates the record as expected
        ProducerRecord<Object, Object> interceptedRecord = interceptors.onSend(producerRecord);
        //assertEquals(2, onSendCount);
        assertEquals(producerRecord.topic(), interceptedRecord.topic());
        assertEquals(producerRecord.partition(), interceptedRecord.partition());
        assertEquals(producerRecord.key(), interceptedRecord.key());
        assertEquals(interceptedRecord.value(), producerRecord.value());
        ProducerRecord<Object, Object> anotherRecord = interceptors.onSend(producerRecord);
        assertEquals(interceptedRecord, anotherRecord);

        ProducerRecord<Object, Object> partInterceptRecord = interceptors.onSend(producerRecord);
        assertEquals(partInterceptRecord.value(), producerRecord.value());

        // verify the record remains valid if all onSend throws an exception
        ProducerRecord<Object, Object> noInterceptRecord = interceptors.onSend(producerRecord);
        assertEquals(producerRecord, noInterceptRecord);

        interceptors.close();
    }

}
