# Daikon Kafka utilities

## Background

When trying to send a record to an unavailable Kafka broker, the producer will block until Kafka gets
back available. This mechanism is controlled by a timeout configured at the producer level
(see org.apache.kafka.clients.producer.ProducerConfig.MAX_BLOCK_MS_CONFIG). But:

- during this period while the producer retries to reach Kafka, if the producer is destroyed (example the application is killed),
the record is lost.
- when this timeout is reached, an exception is thrown back to the caller who becomes responsible to
handle this failure. In case some changes were persisted somewhere else before the application tried to send
the record to Kafka, those changes should be reverted to leave the application in an accurate state and
this is not always possible.

## Kafka-reliable-producer

The "Kafka-reliable-producer" module provides a wrapper around a Kafka producer that detects failures while
sending a record and delegates the responsibility of the record to a storage mechanism.

### Wrapping an existing producer

```
final Map<String, Object> conf = new HashMap<>();
...
final Producer<String, String> producer = new KafkaProducer<>(config);

final PendingRecordRepository pendingRecordRepository = ...;

final String applicationName = "myProducer";

KafkaReliableProducer<String, String> reliableProducer = new KafkaReliableProducer<>(
    applicationName,
    producer,
    pendingRecordRepository);
```

The PendingRecordRepository will be responsible to save records that could not be sent.

The applicationName parameter is used to identify the producer in case the same storage is used
by different producers.

### Sending records

KafkaReliableProducer implements the Kafka Producer interface, so just use the usual methods to send records.

###
