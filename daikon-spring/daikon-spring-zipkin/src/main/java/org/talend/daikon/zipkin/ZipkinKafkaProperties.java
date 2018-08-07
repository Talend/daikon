package org.talend.daikon.zipkin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "talend.zipkin.kafka")
public class ZipkinKafkaProperties {

    /**
     * Kafka Topic
     */
    private String topic = "zipkin";

    /**
     * Broker dedicated to the infrastructure log events.
     */
    private String bootstrapServers;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}
