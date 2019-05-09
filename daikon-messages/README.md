# Talend Daikon messages

## Overview

The purpose of this module is to provide a common infrastructure to normalize asynchronous messages exchanged by Talend services.

Messages can either be events or commands:
- events relate past actions or modifications that were processed by the issuing service. 
- commands are execution orders published by services to trigger actions. 

When a command is executed by a service, this service will generate events once the action is completed. 
When a service consumes an event, it can issue new commands or events as a consequence.

### Messages standardization levels and benefits

This implementation provides standardization at 3 levels:

**Messages format standardization** using Apache Avro, all messages will use the same serialization mechanism and schema definition
framework. This level is a mandatory piece to achieve the other levels.
 
The reasons why Apache Avro was selected are:
- it is open-source and standard implementations are provided in different languages and framework (Java, C, C++, C#, Python)
- it ensures the messages sent across the platform are valid as it enforces data types and schema validation (not the case in JSON natively)  
- it minimizes the size of messages payload (compared to JSON or XML)
- it integrates well with Apache Kafka (See [Confluent platform](https://docs.confluent.io/current/platform.html) 
 and [Spring boot](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/schema-evolution.html))
- it is designed for schema evolution

**Infrastructure metadata standardization** is mandatory to achieve technical and functional integration and ensure interoperability. 
 It mainly focuses on execution context propagation (messages correlation, security context propagation) but will help in other subjects as 
 messages routing, filtering and basic audit trail construction. To achieve this part, this module provides a normalized message header and 
 a framework to generate this header as well as a normalized message key.

**Domain metadata standardization** will help creating generic added-value services (lineage, human readable audit, ...) by 
 making messages interpretation a generic process. To achieve this part, this module provides a way to normalize semantic information in 
 messages Avro schemas.

### Messages naming

Both events and commands have a name identifying their nature

When the event name contains a verb, this verb should be in past tense (e.g: actionPerformed, userCreated, databaseUpdated ...).
When a command name contains a verb, this verb should be in imperative form (e.g: performAction, createUser ...).

A message name must be camel-case.

## Implementation description

The [messages-model](messages-model) module provides the framework itself with very few external dependencies.

The [messages-model-spring-support](messages-model-spring-support) module provides a default spring implementation.

### Message format normalization

Apache Avro is used as serialization technology which provides strict schema validation, strong typing and compact payloads.

Implementation enforces use of Avro as serialization mechanism and enforces to use a schema-first approach:
- create an avsc schema file for each message type
- generate the corresponding Java classes using the Avro maven plugin or equivalent
- instantiate and manipulate messages using the generated Java classes 

Apache Kafka is used as messaging bus. It provides horizontal scalability and good performances. The messages-model module
provides implementation for both keys and values of Kafka records.

### Common message header

[The messages-model](messages-model) module provides a set of reusable [Avro schemas](messages-model/src/main/avro), corresponding Java POJOs 
(generated during the maven build) and the [base framework](messages-model/src/main/java/org/talend/daikon/messages/header) 
to instantiate and manipulate message headers.

A [common message header](messages-model/src/main/avro/MessageHeader.avsc) definition is provided. 
Every message schema definition must reference this common message header as first field so that it should be possible
to access the content of this normalized header by using

```
 IndexedRecord message = ...;
 MessageHeader header = (MessageHeader)message.get(0);
```

A [MessageHeaderExtractor](messages-model/src/main/java/org/talend/daikon/messages/header/MessageHeaderExtractor.java) utility is provided to extract message headers from
an IndexedRecord

```
MessageHeaderExtractor extractor = new MessageHeaderExtractor();
IndexedRecord message = ...;
MessageHeader header = exactor.extractMessageHeader(message);
```

The [MessageHeaderFactory](messages-model/src/main/java/org/talend/daikon/messages/header/MessageHeaderFactory.java) interface should be the main entry point to create
 message header objects. Its default [implementation](messages-model/src/main/java/org/talend/daikon/messages/header/MessageHeaderFactoryImpl.java) delegates header information retrieval
 to different providers:
 
- [IdGenerator](messages-model/src/main/java/org/talend/daikon/messages/header/IdGenerator.java): responsible to generate messages unique identified. A default implementation uses Java UUID  
- [ServiceInfoProvider](messages-model/src/main/java/org/talend/daikon/messages/header/ServiceInfoProvider.java): provides current service information
- [TimestampProvider](messages-model/src/main/java/org/talend/daikon/messages/header/TimestampProvider.java): current timestamp provider. A default implementation uses local timestamp
- [UserProvider](messages-model/src/main/java/org/talend/daikon/messages/header/UserProvider.java): provides the current user id
- [TenantIdProvider](messages-model/src/main/java/org/talend/daikon/messages/header/TenantIdProvider.java): provides the current tenant id  
- [CorrelationIdProvider](messages-model/src/main/java/org/talend/daikon/messages/header/CorrelationIdProvider.java): provides the current correlation id 
- [SecurityTokenProvider](messages-model/src/main/java/org/talend/daikon/messages/header/SecurityTokenProvider.java): provides the current security token 
 
These provides should be implemented by the different framework supports project (Spring boot support, Scala stack support ...)

As a result, generating a message header should be as simple as:

```
MessageHeaderFactory factory = ...;
MessageHeader header = factory.createMessageHeader(MessageTypes.COMMAND, "CreateDataset");
```

### Message envelope

To ease **migration** and adoption of this normalized header message, a [message envelope](messages-model/src/main/avro/MessageEnvelop.avsc) 
can be used to wrap existing messages whatever their format is - as long as these messages can be serialized as a string (JSON / XML ...)

The message envelope consists in 2 fields:
- the common message header mentioned above as first field
- a payload wrapper as second field.

The payload wrapper contains 2 fields:
- the format name (a mandatory string), used during message wrapping and unwrapping in order to resolve the appropriate serializer and deserializer.
- the actual message content as string.

All fields are mandatory.

The [message envelop handler](messages-model/src/main/java/org/talend/daikon/messages/envelope/MessageEnvelopeHandler.java) framework allows to 
easily manipulate the message envelope to wrap / unwrap messages.

Messages serialization and deserialization should be implemented by [Message converters](messages-model/src/main/java/org/talend/daikon/messages/envelope/MessageConverter.java) instances.
A [Message converter registry](messages-model/src/main/java/org/talend/daikon/messages/envelope/MessageConverterRegistry.java) should be implemented to retrieve message converters given
a format name.

Here is an example usage of the MessageEnvelopHandler:

```
MessageConverterRegistry messageConverterRegistry = ...;
MessageHeaderFactory messageHeaderFactory = ...;
MessageEnvelopeHandler handler = new MessageEnvelopeHandlerImpl(messageConverterRegistry, messageHeaderFactory);

[...]

// send a message
MyMessage message = ...;
MessageEnvelope envelope = handler.wrap(message);


// receive a message
MessageEnvelope envelope = ...;
MyMessage message = (MyMessage) handler.unwrap(envelope);
```

### Common message keys

As messages are distributed by Apache Kafka, messages keys should also be normalized.

Message keys are involved in the process of messages partitioning in Kafka, the provided design addresses the following use cases:
- the message key should contain the tenant identifier in a multi-tenant context so that the message payload can be entirely encrypted and decrypted using a tenant specific key
- by default, messages should be randomly partitioned in a topic to make sure the load is correctly balanced
- if required, message partitioning mechanism should influenced by implementation-specific partitioning keys so that messages with the same keys should be routed to the same partition.

The latest use case is important if messages ordering is required. In this case tenant identifier should be taken into account in the partitioning strategy.

The [messages-model](messages-model) module provides a common [Avro schema to represent message keys](messages-model/src/main/avro/MessageKey.avsc) and a 
[minimal framework](messages-model/src/main/java/org/talend/daikon/messages/keys/MessageKeyFactory.java) to manipulate these keys.

To generate a message key that will fit random partitioning (either multi-tenant or not)
 
```
MessageKeyFactory factory = ...;
MessageKey key = factory.createMessageKey();
```

The MessageKeyFactory implementation will ensure the tenant id is extracted from the current execution context. The implementation will add a random string in the message key
payload to enforce random distribution across tenants.

In order to implement a predictable partitioning strategy on messages based on 3 functional properties (property1, property2, property3)

```
MessageKeyFactory factory = ...;
MessageKey key = factory.buildMessageKey()
.withKey("property1", value1)
.withKey("property2", value2)
.withKey("property3", value3)
.buid();
```

Again, the MessageKeyFactory implementation will integrate current tenant id and will add the provided partitioning keys in the message key but won't add additional
information.

### Recommended implementation

New kafka event should not use [message envelope](messages-model/src/main/avro/MessageEnvelop.avsc) schema but define specific schema with one mandatory attribute called [header](messages-model/src/main/avro/MessageHeader.avsc).

Here is an example usage of a new implementation:

```
{
  "namespace": "org.talend.dataprep.messages",
  "name": "CacheMessage",
  "type": "record",
  "fields": [
    {
      "name": "header",
      "type": "org.talend.daikon.messages.MessageHeader"
    },
    {
      "name": "operationType",
      "type" : "org.talend.dataprep.messages.OperationTypes",
      "default": null
    },
    {
      "name": "cacheKey",
      "type": "string"
    },
    {
      "name": "partialKey",
      "type": "boolean",
      "default": "false"
    }
  ]
}

```

Messages used for communication between application like TDP, TDS, TDC, Streams should be created in a separate project inside Daikon-EE

### Spring implementation

Spring implementation should defined common bean and set properties to customize serializer/deserializer.

Properties should look like:

```
spring.cloud.stream.kafka.bindings.topic1.producer.configuration.key.serializer=org.talend.daikon.messages.serialization.DaikonMessageKeySerializer
spring.cloud.stream.kafka.bindings.topic1.consumer.configuration.key.deserializer=org.talend.daikon.messages.serialization.DaikonMessageKeyDeserializer
```

Applications should declare AvroSchemaMessageConverter like this:

```
@Configuration
@EnableBinding(...)
public class GlobalKafkaConfiguration {

    @Bean
    public MessageConverter avroMessageConverter() {
        return new AvroSchemaMessageConverter(new MimeType("application", "avro"));
    }
}
```

When this configuration is set. Producer can send Avro message by using:

```
    public void sendMessage(CacheMessage event) {

        MessageKey messageKey = messageKeyFactory.createMessageKey();

        // creating kafka message
        Message message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
                .build();

        outputChannel.send(message);
    }
```
and listener can receive Avro message by using:

```
    @StreamListener(value = CHANNEL)
    public void receive(Message<CacheMessage> message) {

        CacheMessage payload = message.getPayload();

        // setting context for kafka message
        executionContextUpdater.updateExecutionContext(message);
    }
```

## Sensitive data

Avro may contain sensitive data (e.g. authentication token) that must be excluded from the `toString()` result of the Avro record. This can happen when message is logged for example.

To indicate a sensitive field, you can use the "secured" flag in the field definition:

```json
{
  "name" : "securityToken",
  "doc" : "User's security token",
  "type" : ["null", "string"],
  "secured": true
}
```
