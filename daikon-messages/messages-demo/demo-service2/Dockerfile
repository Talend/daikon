FROM openjdk:8-alpine

ARG ARTIFACT

ENV KAFKA_CONNECT ""
ENV ZK_CONNECT ""
ENV SCHEMA_REGISTRY_URL ""
ENV JAVA_OPTS ""
ENV GROUP_ID "service2"

ENTRYPOINT java $JAVA_OPTS -jar /service.jar

ADD target/${ARTIFACT} /service.jar

