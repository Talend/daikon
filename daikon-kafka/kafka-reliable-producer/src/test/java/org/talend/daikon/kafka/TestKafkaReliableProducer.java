package org.talend.daikon.kafka;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class TestKafkaReliableProducer {

    private static final String applicationName = "appName";

    private PendingRecordRepository<String, String> repository;

    private Producer<String, String> producer;

    private Future<RecordMetadata> anyResponse = mock(Future.class);

    @Before
    public void setUp() throws Exception {
        repository = mock(PendingRecordRepository.class);
        producer = createSuccessfulProducer();
    }

    @Test
    public void testSendNoCallback() {
        // send a message
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);
        ProducerRecord<String, String> record = createRecord();
        wrapper.send(record);

        // make sure the producer was called
        ArgumentCaptor<ProducerRecord> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(recordCaptor.capture(), Mockito.any(Callback.class));
        Assert.assertEquals(record, recordCaptor.getValue());

        // verify the repository was not called
        Mockito.verifyZeroInteractions(repository);

    }

    @Test
    public void testSendWithCallback() {
        // send a message and pass a callback
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);
        ProducerRecord<String, String> record = createRecord();

        Callback callback = Mockito.mock(Callback.class);
        wrapper.send(record, callback);

        // make sure the producer was called
        ArgumentCaptor<ProducerRecord> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(recordCaptor.capture(), Mockito.any(Callback.class));
        verify(callback).onCompletion(Mockito.any(RecordMetadata.class), Matchers.isNull(Exception.class));
        Assert.assertEquals(record, recordCaptor.getValue());

        // verify the repository was not called
        Mockito.verifyZeroInteractions(repository);

    }

    @Test
    public void testExceptionOnSend() {
        Exception exception = new Exception("My exception");
        this.producer = createProducer(exception);
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);
        ProducerRecord<String, String> record = createRecord();
        wrapper.send(record);

        // make sure the producer was called
        ArgumentCaptor<ProducerRecord> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(recordCaptor.capture(), Mockito.any(Callback.class));

        // make sure the repository is called
        ArgumentCaptor<PendingRecord> pendingRecordCaptor = ArgumentCaptor.forClass(PendingRecord.class);
        verify(repository).savePendingRecord(pendingRecordCaptor.capture());
        Assert.assertEquals(applicationName, pendingRecordCaptor.getValue().getApplicationName());
        Assert.assertEquals(record, recordCaptor.getValue());

    }

    @Test
    public void testExceptionOnSendWithCallback() {
        // send a record while the producer will generate an exception
        Exception exception = new Exception("My exception");
        this.producer = createProducer(exception);
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);

        ProducerRecord<String, String> record = createRecord();
        Callback callback = Mockito.mock(Callback.class);
        wrapper.send(record, callback);

        // make sure the producer is called
        ArgumentCaptor<ProducerRecord> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(recordCaptor.capture(), Mockito.any(Callback.class));
        Assert.assertEquals(record, recordCaptor.getValue());

        // make sure the repository is called
        ArgumentCaptor<PendingRecord> pendingRecordCaptor = ArgumentCaptor.forClass(PendingRecord.class);
        verify(repository).savePendingRecord(pendingRecordCaptor.capture());
        Assert.assertEquals(applicationName, pendingRecordCaptor.getValue().getApplicationName());

        // make sure the callback was called without exception
        verify(callback).onCompletion(Matchers.isNull(RecordMetadata.class), Matchers.isNull(Exception.class));
    }

    @Test
    public void testSendPendingRecords() throws Exception {
        this.producer = createSuccessfulProducer();
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);
        List<PendingRecord<String, String>> pendingRecords = new ArrayList<>();
        pendingRecords.add(createPendingRecord("topic1", "key1", "value1"));
        pendingRecords.add(createPendingRecord("topic2", "key2", "value2"));

        when(repository.findPendingRecordByApplication(applicationName)).thenReturn(pendingRecords);

        wrapper.sendPendingRecords();

        ArgumentCaptor<ProducerRecord> producerRecordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer, Mockito.times(2)).send(producerRecordCaptor.capture(), any(Callback.class));

        Assert.assertEquals(pendingRecords.size(), producerRecordCaptor.getAllValues().size());
        for (int i = 0; i < pendingRecords.size(); i++) {
            Assert.assertEquals(pendingRecords.get(i).getKey(), producerRecordCaptor.getAllValues().get(i).key());
            Assert.assertEquals(pendingRecords.get(i).getValue(), producerRecordCaptor.getAllValues().get(i).value());
            Assert.assertEquals(pendingRecords.get(i).getTopic(), producerRecordCaptor.getAllValues().get(i).topic());
            Assert.assertEquals(pendingRecords.get(i).getPartition(), producerRecordCaptor.getAllValues().get(i).partition());
        }
    }

    @Test
    public void testKafkaProducerInterface() throws Exception {
        this.producer = createSuccessfulProducer();
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);

        wrapper.close();
        verify(producer).close();

        wrapper.close(100, TimeUnit.MINUTES);
        verify(producer).close(100, TimeUnit.MINUTES);

        wrapper.flush();
        verify(producer).flush();

        wrapper.metrics();
        verify(producer).metrics();

        wrapper.partitionsFor("topic");
        verify(producer).partitionsFor("topic");
    }

    @Test
    public void testHasHighPriorityPendingRecords() throws Exception {
        this.producer = createSuccessfulProducer();
        KafkaReliableProducer<String, String> wrapper = new KafkaReliableProducer<>(applicationName, producer, repository);

        wrapper.hasHighPriorityPendingRecords();
        verify(repository).hasHighPriorityPendingRecords(applicationName);

    }

    private PendingRecord<String, String> createPendingRecord(String topic, String key, String value) {
        return new PendingRecord<>(applicationName, new ProducerRecord(topic, key, value), RecordPriority.LOW);
    }

    private Producer<String, String> createSuccessfulProducer() {
        return createProducer(null);
    }

    private Producer<String, String> createProducer(Exception e) {
        Producer<String, String> result = mock(Producer.class);
        when(result.send(any(ProducerRecord.class))).thenReturn(anyResponse);
        when(result.send(any(ProducerRecord.class), any(Callback.class))).then(invocation -> {
            Callback callback = invocation.getArgumentAt(1, Callback.class);
            callback.onCompletion(null, e);
            return anyResponse;
        });
        return result;
    }

    private ProducerRecord<String, String> createRecord() {
        return new ProducerRecord<>("topic", "key", "value");
    }

}
