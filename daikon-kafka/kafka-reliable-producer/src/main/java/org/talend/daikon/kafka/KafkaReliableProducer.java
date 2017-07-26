package org.talend.daikon.kafka;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A wrapper around a Kafka producer that saves failed records in a repository so that these records
 * can be sent again when Kafka gets available. This wrapper implements the Kafka {@link Producer} interface
 * so that it can be used as a regular Kafka producer.
 * </p>
 *
 * <p>
 * This implementation does not take care of scheduling pending records re-sending but provides a method
 * to re-send these records. Scheduling mechanism must be provided and set up independently.
 * </p>
 *
 * <p>
 * When creating a wrapper around an existing Kafka producer, the following additional information can be provided:
 * </p>
 *
 * <p>
 * applicationName is mandatory and defines a logical name of the application / service trying to send records to Kafka
 * using this wrapper. When failed records will be resent to Kafka, this current wrapper will only take care of sending
 * its own records
 * </p>
 *
 * <p>
 * pendingRecordRepository is mandatory and defines the storage where records will be saved in case of
 * failure
 * </p>
 *
 * <p>
 * defaultRecordPriority: an optional metadata that denotes how critical are the records for the application /
 * service {@link RecordPriority}. This metadata is actually useful when the application / service has to implement a
 * particular behaviour when some critical records were not sent. The method {@link #hasHighPriorityPendingRecords()}
 * can
 * be used yo detect this case. If no default RecordPriority is provided,
 * the default one will be {@link RecordPriority#LOW}. It is up to the application / service to implement
 * </p>
 *
 * <p>
 * This wrapper implements {@link Producer}'s methods to send records to Kafka and adds a new method
 * {@link #send(ProducerRecord, Callback, RecordPriority)} that allows overriding the default record priority.
 * </p>
 *
 * @param <K> type of key for the records managed by this {@link Producer}
 * @param <V> type of value for the records managed by this {@link Producer}
 */
public class KafkaReliableProducer<K, V> implements Producer<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReliableProducer.class);

    private final String applicationName;

    private final Producer<K, V> kafkaProducer;

    private final PendingRecordRepository<K, V> pendingRecordRepository;

    private final RecordPriority defaultRecordPriority;

    /**
     * Creates a new wrapper around kafkaProducer
     * 
     * @param applicationName the logical name of the application / service
     * @param kafkaProducer the Kafka producer to be wrapped
     * @param pendingRecordRepository pending records storage
     */
    public KafkaReliableProducer(String applicationName, Producer<K, V> kafkaProducer,
            PendingRecordRepository<K, V> pendingRecordRepository) {
        this(applicationName, kafkaProducer, pendingRecordRepository, RecordPriority.LOW);
    }

    /**
     * Creates a new wrapper around kafkaProducer
     * 
     * @param applicationName the logical name of the application / service
     * @param kafkaProducer the Kafka producer to be wrapped
     * @param pendingRecordRepository pending records storage
     * @param defaultRecordPriority the default records priority
     */
    public KafkaReliableProducer(String applicationName, Producer<K, V> kafkaProducer,
            PendingRecordRepository<K, V> pendingRecordRepository, RecordPriority defaultRecordPriority) {
        this.applicationName = applicationName;
        this.kafkaProducer = kafkaProducer;
        this.pendingRecordRepository = pendingRecordRepository;
        this.defaultRecordPriority = defaultRecordPriority;
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
        return this.send(record, null, null);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
        return this.send(record, callback, null);
    }

    /**
     * Same as {@link #send(ProducerRecord, Callback)} but specifies a custom priority
     * for the record being sent.
     *
     * In case of failure, saves the record in the configured repository.
     *
     * In any case, the provided callback (if any) will be executed once the record was sent
     * or stored in the repository in case of failure.
     * 
     * @param record the record to send
     * @param callback an eventual callback to execute once record is sent
     * @param recordPriority the priority of this record.
     * @return the underlying future
     */
    public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback, final RecordPriority recordPriority) {
        return new InternalFuture(this.kafkaProducer.send(record, (metadata, e) -> {
            handleKafkaResponse(record, recordPriority, e);
            if (callback != null) {
                callback.onCompletion(metadata, null);
            }
        }), null);
    }

    /**
     * Fetches all pending records from the repository which matches the application name, and tries to send them again
     * through kafka.
     *
     * The method is synchronised to avoid concurrency problems when calling this method simultaneously.
     *
     * TODO: if a record cannot be sent to Kafka, stop the loop
     */
    public synchronized void sendPendingRecords() throws Exception {
        List<PendingRecord<K, V>> pendingRecords = pendingRecordRepository.findPendingRecordByApplication(applicationName);
        LOGGER.info("Kafka message scheduler will try to send " + pendingRecords.size() + " messages for application "
                + applicationName);

        pendingRecords.sort((o1, o2) -> Long.compare(o1.getCreationDate(), o2.getCreationDate()));

        for (PendingRecord pendingRecord : pendingRecords) {
            Future<RecordMetadata> result = this.send(pendingRecord.getProducerRecord(),
                    (metadata, e) -> this.handlePendingRecordResendResponse(pendingRecord, metadata, e),
                    pendingRecord.getRecordPriority());
            // force synchronous resending
            result.get();
        }
    }

    private void handlePendingRecordResendResponse(PendingRecord pendingRecord, RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            pendingRecordRepository.delete(pendingRecord.getId());
        }
    }

    /**
     * @return true if there are currently high priority pending records.
     */
    public boolean hasHighPriorityPendingRecords() {
        LOGGER.debug("hasHighPriorityPendingRecords for application " + applicationName);
        return pendingRecordRepository.hasHighPriorityPendingRecords(applicationName);
    }

    @Override
    public void flush() {
        this.kafkaProducer.flush();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return this.kafkaProducer.partitionsFor(topic);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return this.kafkaProducer.metrics();
    }

    @Override
    public void close() {
        this.kafkaProducer.close();
    }

    @Override
    public void close(long timeout, TimeUnit unit) {
        this.kafkaProducer.close(timeout, unit);
    }

    private void handleKafkaResponse(ProducerRecord<K, V> record, RecordPriority recordPriority, Exception e) {
        if (e != null) {
            final RecordPriority priority = recordPriority != null ? recordPriority : defaultRecordPriority;
            final PendingRecord pendingRecord = new PendingRecord<>(applicationName, record, priority);
            pendingRecordRepository.savePendingRecord(pendingRecord);
            LOGGER.warn("Following record sending failed and will be stored for application " + applicationName
                    + " until kafka is up again " + record, e);
        } else {
            LOGGER.debug("Record was sent to kafka without error " + record);
        }
    }

    private static class InternalFuture implements Future<RecordMetadata> {

        private final Future<RecordMetadata> wrappedFuture;

        private final RecordMetadata resultOnError;

        public InternalFuture(Future<RecordMetadata> wrappedFuture, RecordMetadata resultOnError) {
            this.wrappedFuture = wrappedFuture;
            this.resultOnError = resultOnError;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return wrappedFuture.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return wrappedFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return wrappedFuture.isDone();
        }

        @Override
        public RecordMetadata get() throws InterruptedException, ExecutionException {
            try {
                return wrappedFuture.get();
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.info("Error when sending a record to Kafka", e);
                return resultOnError;
            }
        }

        @Override
        public RecordMetadata get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            try {
                return wrappedFuture.get(timeout, unit);
            } catch (InterruptedException | ExecutionException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.info("Error when sending a record to Kafka", e);
                return resultOnError;
            }
        }
    }

}
