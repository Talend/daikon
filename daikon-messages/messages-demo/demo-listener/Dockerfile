FROM openjdk:8-alpine
MAINTAINER bguillon@talend.com

ARG ARTIFACT

ENV KAFKA_CONNECT ""
ENV ZK_CONNECT ""
ENV SCHEMA_REGISTRY_URL ""
ENV JAVA_OPTS ""
ENV GROUP_ID "service2"

ENTRYPOINT java $JAVA_OPTS -jar /service.jar

COPY target/${ARTIFACT} /service.jar

