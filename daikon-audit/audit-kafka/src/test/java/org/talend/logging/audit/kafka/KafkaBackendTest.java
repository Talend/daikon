package org.talend.logging.audit.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.logging.audit.LogLevel;
import org.talend.logging.audit.impl.AuditConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KafkaBackendTest {

    private KafkaBackend kafkaBackend;

    @Test
    public void testKafkaProducerInitialization() {
        kafkaBackend = new KafkaBackend(AuditConfiguration.loadFromClasspath("/audit.full.properties"));

        assertEquals("testTopic", kafkaBackend.getKafkaTopic());
        assertEquals("tenantId", kafkaBackend.getPartitionKeyName());
        assertEquals("localhost:9092", kafkaBackend.getBootstrapServers());
        assertEquals((Long) 30000L, kafkaBackend.getBlockTimeoutMs());
    }

    @Test
    public void testKafkaProducerMinimalInitialization() {
        kafkaBackend = new KafkaBackend(AuditConfiguration.loadFromClasspath("/audit.minimal.properties"));

        assertEquals("testTopic", kafkaBackend.getKafkaTopic());
        assertNull(kafkaBackend.getPartitionKeyName());
        assertEquals("localhost:9092", kafkaBackend.getBootstrapServers());
        assertEquals((Long) 60000L, kafkaBackend.getBlockTimeoutMs());
    }

    @Test
    public void testLogEmptyMap() {
        KafkaProducer<String, String> kafkaProducerMock = mock(KafkaProducer.class);
        Future futureMock = mock(Future.class);
        kafkaBackend = new KafkaBackend(kafkaProducerMock, "testTopic", "partitionKey", "localhost", 30000L);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        when(kafkaProducerMock.send(captor.capture())).thenReturn(futureMock);
        kafkaBackend.log("application security", LogLevel.INFO, "message", new Throwable());

        ProducerRecord<String, String> record = captor.getValue();
        assertNotNull(record);
        assertNull(record.key());
        assertEquals("null", record.value());
    }

    @Test
    public void testLogEventMap() {
        KafkaProducer<String, String> kafkaProducerMock = mock(KafkaProducer.class);
        Future futureMock = mock(Future.class);
        kafkaBackend = new KafkaBackend(kafkaProducerMock, "testTopic", "partitionKey", "localhost", 30000L);

        Map<String, String> eventMap = new HashMap<>();
        eventMap.put("partitionKey", "ID1234");
        eventMap.put("type", "audit");
        eventMap.put("operation", "read");
        kafkaBackend.setContextMap(eventMap);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        when(kafkaProducerMock.send(captor.capture())).thenReturn(futureMock);
        kafkaBackend.log("application security", LogLevel.INFO, "message", new Throwable());

        ProducerRecord<String, String> record = captor.getValue();
        assertNotNull(record);
        assertEquals("ID1234", record.key());
        assertEquals("{\"partitionKey\":\"ID1234\",\"type\":\"audit\",\"operation\":\"read\"}", record.value());
    }

    @Test
    public void testGetAndSetContextMap() {
        kafkaBackend = new KafkaBackend(AuditConfiguration.loadFromClasspath("/audit.minimal.properties"));

        Map<String, String> eventMap = new HashMap<>();
        eventMap.put("partitionKey", "ID1234");

        kafkaBackend.setContextMap(eventMap);

        Map<String, String> copyOfContextMap = kafkaBackend.getCopyOfContextMap();

        assertEquals(1, copyOfContextMap.size());
        assertEquals("ID1234", eventMap.get("partitionKey"));
    }
}