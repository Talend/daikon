# Talend Daikon messages

## Overview

The purpose of this module is to provide a common infrastructure to normalize messages exchanged by Talend services.

Messages can either be events or commands:
- events relate past actions or modifications that were processed by the issuing service. 
- commands are execution orders published by services to trigger actions. 

When a command is executed by a service, this service will generate events once the action is completed. 
When a service consumes an event, it can issue new commands or events as a consequence.

TODO: asynchronous communication only.

### Messages standardization levels and benefits

This implementation provides normalization at 3 levels:

**Messages format normalization** using Apache Avro, all messages will use the same serialization mechanism and schema definition
framework. This level is a mandatory piece to achieve the other levels. 

TODO: reasons why Avro

TODO: use standardization

**Infrastructure metadata normalization** is mandatory to achieve technical and functional integration and ensure interoperability. 
 It mainly focuses on execution context propagation (messages correlation, security context propagation) but will help in other subjects as 
 messages routing, filtering and basic audit trail construction. To achieve this part, this module provides a normalized message header and 
 a framework to generate this header as well as a normalized message key.

**Domain metadata normalization** will help creating generic added-value services (lineage, human readable audit, ...) by 
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

A [MessageHeaderExtractor](messages-model/src/main/java/org/talend/daikon/messages/MessageHeaderExtractor.java) utility is provided to extract message headers from
an IndexedRecord

```
MessageHeaderExtractor extractor = new MessageHeaderExtractor();
IndexedRecord message = ...;
MessageHeader header = exactor.extractMessageHeader(message);
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



 
 





