package org.talend.daikon.logging.kafka;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.internals.ProducerInterceptors;
import org.junit.Test;
import org.talend.daikon.logging.TalendKafkaProducerInterceptor;
import static org.junit.Assert.assertEquals;

public class ProducerInterceptorsTest {

    @Test
    public void testOnSend() {

        Charset UTF8 = Charset.forName("UTF-8");
        String ip = "192.168.50.130";
        String message = "kafka_message," + ip;

        ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>("test", 0, 1, message.getBytes(UTF8));

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
