CQRS POC
========
The goal of this is to show an example of the CQRS pattern in java using the [Axon](http://www.axonframework.org/) framework and spring boot. 


here is a view of the pattern architecture
![CQRS pattern](CQRS.jpg)

Rest APIS
--
This project provides 2 sets of REST APIs, you may call them using the [postman commands](CQRS_poc.postman_collection.json) provided:
1. one for the commands to be created in order to create and update the domain model. [package command](src/main/java/org/talend/cqrs/poc/preparation/command/)
2. to query the domain model. [package query](src/main/java/org/talend/cqrs/poc/preparation/query/)

Please note that those package should never be named as such and should represent the domain model, this was named like this for more clariry in the poc.

Kafka
--
The kafka messaging tests is located in the [messaging package](src/main/java/org/talend/cqrs/poc/messaging/). This just sends all the CQRS event in a kafka topic named _output_ using the default spring-cloud-stream serialization.
In order to enable kafka binding you just have to uncomment the pom.xml kafka dependency.

Unit tests
--
You'll find some unit test located [here](src/test/java/org/talend/cqrs/poc/command/PreparationCommandTest.java)