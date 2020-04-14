package org.talend.daikon.spring.audit.logs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.kafka")
public class AuditKafkaProperties {

    private String bootstrapServers;

    private String topic = "audit-logs";

    private String partitionKeyName = "account_id";

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartitionKeyName() {
        return partitionKeyName;
    }

    public void setPartitionKeyName(String partitionKeyName) {
        this.partitionKeyName = partitionKeyName;
    }
}
