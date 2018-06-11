package org.talend.daikon.zipkin;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka11.KafkaSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures the zipkin kafka sender for another kafka broker than the functional broker.
 * spring.zipkin.kafka.bootstrapServers stores the broker dedicated to the infrastructure log events.
 */
@Configuration
@ConditionalOnClass(ByteArraySerializer.class)
@ConditionalOnMissingBean(Sender.class)
@ConditionalOnProperty(value = "spring.zipkin.enabled", havingValue = "true")
public class ZipkinKafkaSenderConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ZipkinKafkaSenderConfiguration.class);

    @Value("${spring.zipkin.kafka.topic:zipkin}")
    private String topic;

    @Value("${spring.zipkin.kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean
    public Sender kafkaSender() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", ByteArraySerializer.class.getName());
        properties.put("value.serializer", ByteArraySerializer.class.getName());
        properties.put("bootstrap.servers", bootstrapServers);

        LOG.info("building zipkin kafka sender with brokers " + bootstrapServers);

        return KafkaSender.newBuilder().topic(this.topic).overrides(properties).build();
    }
}
